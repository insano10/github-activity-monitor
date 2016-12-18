package com.insano10.observationdeck.gocd

import akka.actor.ActorSystem
import com.insano10.observationdeck.gocd.entities.GoCDEntities
import GoCDEntities.{Pipeline, GoCDDeploymentStatus, PipelineHistory}
import com.insano10.observationdeck.gocd.entities.GoCDEntities._
import dispatch.{url, _}
import org.json4s._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}


class GoCDClient(system: ActorSystem, baseUrl: String, username: String, password: String, ignoredCommitters: List[String]) {

  implicit val formats = DefaultFormats

  private val HISTORY_PAGE_SIZE = 10
  private val MAX_PAGES = 5

  def getPipelineDeploymentStatus(pipelineName: String): Future[GoCDDeploymentStatus] = {

    getPageOfPipelineHistory(pipelineName, 0).flatMap(history => {

      val mostRecentPipeline = history.pipelines.head
      val mostRecentPipelineHasBeenDeployed = mostRecentPipeline.stages.last.scheduled

      if (!mostRecentPipelineHasBeenDeployed) {
        findDeploymentCommitHash(pipelineName, 0).
          map(GoCDDeploymentStatus(needsDeployment = true, _))
      } else {
        Future.successful(GoCDDeploymentStatus(needsDeployment = false, None))
      }
    })
  }

  def getReleaseHistory(pipelineNames: List[PipelineConfig], daysDataToRetrieve: Int) = {

    //map the pipeline names into Lists of ReleasedPipelines
    val releasedPipelines: List[Future[List[ReleasedPipeline]]] = pipelineNames.map(pipeline => getPipelineReleaseHistory(pipeline, daysDataToRetrieve, 0))

    //squash all the Lists together and sort them by decreasing release date
    val mergedPipelines: Future[List[ReleasedPipeline]] = Future.fold(releasedPipelines)(List[ReleasedPipeline]())((acc, e) => (acc ++ e).sortWith(_.releasedAtMs > _.releasedAtMs))

    mergedPipelines
  }

  private def getPipelineReleaseHistory(pipelineConfig: PipelineConfig, daysDataToRetrieve: Int, currentDepth: Int): Future[List[ReleasedPipeline]] = {

    val minTime = System.currentTimeMillis() - (daysDataToRetrieve * 24 * 60 * 60 * 1000L)

    getPageOfPipelineHistory(pipelineConfig.pipelineName, currentDepth * HISTORY_PAGE_SIZE).flatMap(history => {

      val releasedPipelines = history.pipelines.
        filter(pipeline => pipeline.stages.last.scheduled).
        map(pipeline => {

          val pipelineUrl = s"$baseUrl/go/pipelines/${pipelineConfig.pipelineName}/${pipeline.counter}/${pipeline.stages.head.name}/1"

          ReleasedPipeline(
            pipelineConfig.shortRepoName,
            pipeline.stages.last.jobs.last.scheduled_date,
            pipelineUrl)
        })

      if (currentDepth == MAX_PAGES) {

        //stop fetching pages now
        Future.successful(releasedPipelines)
      } else if (history.pipelines.nonEmpty && history.pipelines.last.stages.head.jobs.head.scheduled_date > minTime) {

        //keep fetching if we have not gone back far enough in history
        getPipelineReleaseHistory(pipelineConfig, daysDataToRetrieve, currentDepth + 1).
          flatMap(moreReleasedPipelines =>
            Future.successful(moreReleasedPipelines ::: releasedPipelines))
      } else {

        //stop fetching as we've gone back far enough in history
        Future.successful(releasedPipelines)
      }

    })
  }

  private def findDeploymentCommitHash(pipelineName: String, currentDepth: Int): Future[Option[String]] = {

    getPageOfPipelineHistory(pipelineName, currentDepth * HISTORY_PAGE_SIZE).flatMap(history => {

      val incompletePipelines = getIncompletePipelines(history)

      if (incompletePipelines.length == HISTORY_PAGE_SIZE) {
        if (currentDepth == MAX_PAGES) {
          //we looked through over 50 pipelines and didn't find a triggering commit
          //either we haven't deployed in a LONG TIME or something is wrong
          Future.successful(None)
        } else {

          //either find the hash in a future page, or take the last hash of the current page
          findDeploymentCommitHash(pipelineName, currentDepth + 1).map {
            case Some(hash) => Some(hash)
            case None => getCommitHashOfLastPipeline(incompletePipelines)
          }
        }
      } else {
        val commitHash: Option[String] = getCommitHashOfLastPipeline(incompletePipelines)
        Future.successful(commitHash)
      }
    })
  }

  private def getCommitHashOfLastPipeline(pipelines: List[Pipeline]): Option[String] = {

    pipelines.reverse.dropWhile(p => {
      ignoredCommitters.exists(committer => p.build_cause.material_revisions.head.modifications.head.user_name.contains(committer))
    }).headOption.
      flatMap(p => {
        Some(p.build_cause.material_revisions.head.modifications.head.revision)
      })
  }

  private def getIncompletePipelines(history: PipelineHistory): List[Pipeline] = {
    history.pipelines.takeWhile(p => {
      val lastStageInPipeline = p.stages.length - 1
      !p.stages(lastStageInPipeline).scheduled
    })
  }

  private def getPageOfPipelineHistory(pipelineName: String, offset: Integer)(implicit ctx: ExecutionContext): Future[PipelineHistory] = {

    val prom = Promise[PipelineHistory]()

    dispatch.Http(
      url(s"$baseUrl/go/api/pipelines/$pipelineName/history/$offset").
        as_!(username, password) OK as.json4s.Json).map(_.extract[PipelineHistory]).
      onComplete {
        case Success(content) => prom.complete(Try(content))
        case Failure(exception) => prom.failure(exception)
      }

    prom.future
  }
}