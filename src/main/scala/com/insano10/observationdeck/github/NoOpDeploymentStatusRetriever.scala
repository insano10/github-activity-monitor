package com.insano10.observationdeck.github

import com.insano10.observationdeck.github.entities.GithubEntities.DeploymentStatus
import org.kohsuke.github.GHRepository

class NoOpDeploymentStatusRetriever extends DeploymentStatusRetriever {
  override def getDeploymentStatus(gHRepository: GHRepository) = DeploymentStatus(needsDeployment = false, None)
  override def deploymentUrl(repoName: String): String = "#"
}
