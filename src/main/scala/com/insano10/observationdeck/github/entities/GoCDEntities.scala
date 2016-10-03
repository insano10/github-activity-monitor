package com.insano10.observationdeck.github.entities

object GoCDEntities {

  case class Stage(val name: String, val scheduled: Boolean)
  case class BuildCause(val trigger_message: String)
  case class Pipeline(val build_cause: BuildCause, val stages: List[Stage])
  case class PipelineHistory(val pipelines: List[Pipeline])
}
