package controllers

import com.typesafe.config.ConfigFactory
import models.GithubEntities._
import org.kohsuke.github.GitHub
import play.api.cache.CacheApi
import play.api.libs.json._
import play.api.mvc._
import repositories.{PullRequestRepository, RepositoryRepository}
import Repository._

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global

class Repository(cache: CacheApi) extends Controller {

  private val typesafeConfig = ConfigFactory.load(sys.env("CONFIG_FILE"))
  private val daysDataToRetrieve = typesafeConfig.getInt("daysDataToRetrieve")

  private val github = GitHub.connectUsingOAuth(typesafeConfig.getString("github.oauthToken"))
  private val repoList = typesafeConfig.getStringList("repos").asScala.toList

  private val pullRequestRepository = new PullRequestRepository(cache)
  private val repoRepository = new RepositoryRepository(github, pullRequestRepository, cache)

  def repository() = Action.async {
    repoRepository.getRepositorySummaries(repoList, daysDataToRetrieve)
      .map(summaries => Ok(Json.toJson(summaries)))
  }
}

object Repository {

  implicit val CommentToJson: Writes[Comment] = Json.writes[Comment]
  implicit val CommitToJson: Writes[Commit] = Json.writes[Commit]
  implicit val DeploymentOwnerToJson: Writes[DeploymentOwner] = Json.writes[DeploymentOwner]
  implicit val DeploymentStatusToJson: Writes[DeploymentStatus] = Json.writes[DeploymentStatus]
  implicit val PullRequestToJson: Writes[PullRequest] = Json.writes[PullRequest]
  implicit val RepositorySummaryToJson: Writes[RepositorySummary] = Json.writes[RepositorySummary]
}
