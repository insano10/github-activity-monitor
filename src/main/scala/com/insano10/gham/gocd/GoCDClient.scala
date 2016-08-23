package com.insano10.gham.gocd

import akka.actor.ActorSystem
import com.insano10.gham.github.entities.GoCDEntities.PipelineHistory
import dispatch.{url, _}
import org.json4s._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}


class GoCDClient(system: ActorSystem, baseUrl: String,  username: String, password: String) {

  implicit val formats = DefaultFormats

  def doesPipelineNeedDeployment(pipelineName: String): Future[Boolean] = {

    getPipelineHistory(pipelineName).onComplete {
      case Success(history) => {

        val mostRecentPipeline = history.pipelines.head
        val lastStageInMostRecentPipeline = mostRecentPipeline.stages.length - 1
        val finalStageExecuted = mostRecentPipeline.stages(lastStageInMostRecentPipeline).scheduled

        println(s"Success: Final stage in most recent pipeline executed? $finalStageExecuted")
      }
      case Failure(e) => println(s"Error: $e")
    }

    Future.successful(false)
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