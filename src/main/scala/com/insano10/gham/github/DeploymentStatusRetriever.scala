package com.insano10.gham.github

import com.insano10.gham.github.entities.GithubEntities.DeploymentStatus

trait DeploymentStatusRetriever {
  def getDeploymentStatus(repoName: String): DeploymentStatus
  def deploymentUrl(repoName: String): String
}
