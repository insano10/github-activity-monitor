package gocd

import models.GithubEntities.DeploymentStatus
import org.kohsuke.github.GHRepository

trait DeploymentStatusRetriever {
  def getDeploymentStatus(gHRepository: GHRepository): DeploymentStatus
  def deploymentUrl(repoName: String): String
}
