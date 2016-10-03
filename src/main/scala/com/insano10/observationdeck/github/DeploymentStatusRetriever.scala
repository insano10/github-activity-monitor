package com.insano10.observationdeck.github

import com.insano10.observationdeck.github.entities.GithubEntities.DeploymentStatus

trait DeploymentStatusRetriever {
  def getDeploymentStatus(repoName: String): DeploymentStatus
  def deploymentUrl(repoName: String): String
}
