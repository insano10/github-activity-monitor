package com.insano10.gham.gocd

import java.util.concurrent.TimeUnit

import com.insano10.gham.github.DeploymentStatusRetriever
import com.insano10.gham.github.entities.GithubEntities.{DeploymentOwner, DeploymentStatus}
import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging
import org.kohsuke.github.{GHUser, GitHub}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}
import scala.collection.JavaConverters._

class GoCDDeploymentStatusRetriever(gocdClient: GoCDClient, config: Config, gocdUrl: String, github: GitHub) extends DeploymentStatusRetriever with StrictLogging {

  private val repoPipelineMap = config.getObject("gocd.repoPipelineMap")

  override def getDeploymentStatus(repoName: String) = {

    val deploymentStatus: (Boolean, Option[String]) = Try(repoPipelineMap.toConfig.getString(repoName)) match {
      case Success(value) => Await.result(gocdClient.getPipelineDeploymentStatus(value), Duration.apply(5, TimeUnit.SECONDS))
      case Failure(err) => logger.error(s"Missing gocd.repoPipelineMap value for repository $repoName"); (false, None)
    }

    deploymentStatus match {
      case (true, Some(ownerFullName)) => DeploymentStatus(needsDeployment = true, Some(getDeploymentOwner(ownerFullName)))
      case _ => DeploymentStatus(needsDeployment = false, None)
    }
  }

  private def getDeploymentOwner(ownerFullName: String): DeploymentOwner =
    github.searchUsers().q(ownerFullName).in("fullname").list().withPageSize(1).asList().asScala.toList match {
      case x :: xs => DeploymentOwner(ownerFullName, x.getAvatarUrl)
      case _ => DeploymentOwner("Unknown", "#")
    }

  override def deploymentUrl(repoName: String): String = {

    Try(repoPipelineMap.toConfig.getString(repoName)) match {
      case Success(value) => s"$gocdUrl/go/tab/pipeline/history/$value"
      case Failure(err) => logger.error(s"Missing gocd.repoPipelineMap value for repository $repoName"); "#"
    }

  }
}
