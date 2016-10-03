package com.insano10.observationdeck.gocd

import akka.actor.ActorSystem
import com.insano10.observationdeck.github.entities.GoCDEntities.PipelineHistory
import dispatch.{url, _}
import org.json4s._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}


class GoCDClient(system: ActorSystem, baseUrl: String, username: String, password: String) {

  implicit val formats = DefaultFormats

  def getPipelineDeploymentStatus(pipelineName: String): Future[(Boolean, Option[String])] = {

    getPipelineHistory(pipelineName).flatMap(history => {

      val mostRecentPipeline = history.pipelines.head
      val lastStageInMostRecentPipeline = mostRecentPipeline.stages.length - 1
      val mostRecentPipelineHasBeenDeployed = mostRecentPipeline.stages(lastStageInMostRecentPipeline).scheduled

      if(!mostRecentPipelineHasBeenDeployed) {
        Future.successful((true, getDeploymentOwner(history)))
      } else {
        Future.successful(false, None)
      }
    })
  }

  private def getDeploymentOwner(history: PipelineHistory): Option[String] = {

    val incompletePipelines = history.pipelines.takeWhile(p => {
      val lastStageInPipeline = p.stages.length - 1
      !p.stages(lastStageInPipeline).scheduled
    })

    //look for the oldest undeployed pipeline that was triggered by a modification
    incompletePipelines.reverse.
      find(p => p.build_cause.trigger_message.contains("modified by")
      ).
      flatMap(p => {
        val pattern = "^modified by\\s(.*)\\s.*".r
        pattern.findFirstMatchIn(p.build_cause.trigger_message).flatMap(m => Some(m.group(1)))
      })
  }

  private def getPipelineHistory(pipelineName: String)(implicit ctx: ExecutionContext): Future[PipelineHistory] = {

    val prom = Promise[PipelineHistory]()

    dispatch.Http(
      url(s"$baseUrl/go/api/pipelines/$pipelineName/history").
        as_!(username, password) OK as.json4s.Json).map(_.extract[PipelineHistory]).
      onComplete {
        case Success(content) => prom.complete(Try(content))
        case Failure(exception) => prom.failure(exception)
      }

    prom.future
  }
}