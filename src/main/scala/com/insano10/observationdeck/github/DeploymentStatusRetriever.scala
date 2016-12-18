package com.insano10.observationdeck.github

import com.insano10.observationdeck.github.entities.GithubEntities.DeploymentStatus
import org.kohsuke.github.GHRepository

trait DeploymentStatusRetriever {
  def getDeploymentStatus(gHRepository: GHRepository): DeploymentStatus
  def deploymentUrl(repoName: String): String
}
