package gocd

import java.util.concurrent.TimeUnit

import com.typesafe.config.Config
import controllers.Logging
import gocd.entities.GoCDEntities.GoCDDeploymentStatus
import models.GithubEntities.{DeploymentOwner, DeploymentStatus}
import org.kohsuke.github.{GHRepository, GitHub}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

class GoCDDeploymentStatusRetriever(gocdClient: GoCDClient, config: Config, gocdUrl: String, github: GitHub) extends DeploymentStatusRetriever with Logging {

  private val pipelineConfig = config.getObject("gocd.pipelines")

  override def getDeploymentStatus(gHRepository: GHRepository) = {

    val repoName = gHRepository.getFullName

    val deploymentStatus: GoCDDeploymentStatus = getPipelineForRepo(repoName) match {
      case Success(value) => isPipelineDeployable(repoName) match {
        case true => Await.result(gocdClient.getPipelineDeploymentStatus(value), Duration.apply(5, TimeUnit.SECONDS))
        case false => GoCDDeploymentStatus(needsDeployment = false, None)
      }
      case Failure(err) => log.error(s"Missing gocd.pipelines value for repository $repoName"); GoCDDeploymentStatus(needsDeployment = false, None)
    }

    deploymentStatus match {
      case GoCDDeploymentStatus(true, Some(commitHash)) => {

        val commit = gHRepository.getCommit(commitHash)

        if(commit.getAuthor != null) {
          DeploymentStatus(needsDeployment = true, Some(DeploymentOwner(commit.getAuthor.getLogin, commit.getAuthor.getAvatarUrl)))
        } else {
          DeploymentStatus(needsDeployment = false, None)
        }
      }
      case _ => DeploymentStatus(needsDeployment = false, None)
    }
  }

  override def deploymentUrl(repoName: String): String = {

    getPipelineForRepo(repoName) match {
      case Success(value) => s"$gocdUrl/go/tab/pipeline/history/$value"
      case Failure(err) => log.error(s"Missing gocd.pipelines value for repository $repoName"); "#"
    }
  }

  private def getPipelineForRepo(repoName: String): Try[String] = Try(pipelineConfig.toConfig.getObject(repoName).toConfig.getString("pipelineName"))
  private def isPipelineDeployable(repoName: String): Boolean = pipelineConfig.toConfig.getObject(repoName).toConfig.getBoolean("deployable")
}
