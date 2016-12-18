package com.insano10.observationdeck.gocd.entities

object GoCDEntities {

  case class PipelineConfig(val repoName: String, val pipelineName: String, val deployable: Boolean) {
    def shortRepoName:String = repoName.split("/")(1)
  }

  case class Job(val name: String, val scheduled_date: Long)
  case class Stage(val name: String, val scheduled: Boolean, val jobs: List[Job])
  case class MaterialModification(val revision: String, val user_name: String)
  case class MaterialRevision(val modifications: List[MaterialModification])
  case class BuildCause(val trigger_message: String, val material_revisions: List[MaterialRevision])
  case class Pipeline(val name: String, val counter:Integer, val build_cause: BuildCause, val stages: List[Stage])
  case class PipelineHistory(val pipelines: List[Pipeline])

  case class GoCDDeploymentStatus(needsDeployment: Boolean, triggerCommitHash: Option[String])

  case class ReleasedPipeline(val shortRepoName: String, val releasedAtMs: Long, val pipelineUrl: String)
}
