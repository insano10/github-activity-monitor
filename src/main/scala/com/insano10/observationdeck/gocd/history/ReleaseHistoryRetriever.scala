package com.insano10.observationdeck.gocd.history

import com.insano10.observationdeck.gocd.GoCDClient
import com.insano10.observationdeck.gocd.entities.GoCDEntities.PipelineConfig
import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging

import scala.collection.JavaConversions._

class ReleaseHistoryRetriever(gocdClient: GoCDClient, config: Config, gocdUrl: String, daysDataToRetrieve: Int) extends StrictLogging {

  private val repoList = config.getStringList("repos").toList
  private val repoPipelineMap = config.getObject("gocd.pipelines").toConfig

  def getReleaseHistory() = {

    val pipelines = repoList.
      map(repoName => PipelineConfig(
        repoName,
        repoPipelineMap.getObject(repoName).toConfig.getString("pipelineName"),
        repoPipelineMap.getObject(repoName).toConfig.getBoolean("deployable"))
      ).
      filter(config => config.deployable)

    gocdClient.getReleaseHistory(pipelines, daysDataToRetrieve)
  }
}
