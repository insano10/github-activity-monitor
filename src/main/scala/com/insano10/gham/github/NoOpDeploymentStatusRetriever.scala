package com.insano10.gham.github

class NoOpDeploymentStatusRetriever extends DeploymentStatusRetriever {
  override def repositoryNeedsDeployment(repoName: String) = false
}
