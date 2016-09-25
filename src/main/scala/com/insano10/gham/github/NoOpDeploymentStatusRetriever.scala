package com.insano10.gham.github

import com.insano10.gham.github.entities.GithubEntities.DeploymentStatus

class NoOpDeploymentStatusRetriever extends DeploymentStatusRetriever {
  override def getDeploymentStatus(repoName: String) = DeploymentStatus(needsDeployment = false, None)
  override def deploymentUrl(repoName: String): String = "#"
}
