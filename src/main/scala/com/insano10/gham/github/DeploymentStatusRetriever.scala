package com.insano10.gham.github

trait DeploymentStatusRetriever {
  def repositoryNeedsDeployment(repoName: String): Boolean
  def deploymentUrl(repoName: String): String
}
