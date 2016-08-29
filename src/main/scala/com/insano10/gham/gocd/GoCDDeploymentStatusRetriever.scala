package com.insano10.gham.gocd

import java.util.concurrent.TimeUnit

import com.insano10.gham.github.DeploymentStatusRetriever
import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

class GoCDDeploymentStatusRetriever(gocdClient: GoCDClient, config: Config) extends DeploymentStatusRetriever with StrictLogging {

  private val repoPipelineMap = config.getObject("gocd.repoPipelineMap")

  override def repositoryNeedsDeployment(repoName: String) = {

    Try(repoPipelineMap.toConfig.getString(repoName)) match {
      case Success(value) => Await.result(gocdClient.doesPipelineNeedDeployment(value), Duration.apply(5, TimeUnit.SECONDS))
      case Failure(err) => logger.error(s"Missing gocd.repoPipelineMap value for repository $repoName"); false
    }
  }
}
