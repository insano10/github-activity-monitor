package com.insano10.gham.github.entities

object GoCDEntities {

  case class Stage(val name: String, val scheduled: Boolean)
  case class Pipeline(val stages: List[Stage])
  case class PipelineHistory(val pipelines: List[Pipeline])
}
