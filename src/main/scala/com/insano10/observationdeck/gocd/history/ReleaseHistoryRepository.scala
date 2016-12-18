package com.insano10.observationdeck.gocd.history

import com.insano10.observationdeck.gocd.entities.GoCDEntities.ReleasedPipeline
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Future
import scala.concurrent.duration._
import scalacache.ScalaCache
import scalacache.guava.GuavaCache
import scalacache.memoization._


class ReleaseHistoryRepository() extends StrictLogging {

  private var releaseHistoryRetriever: ReleaseHistoryRetriever = null

  implicit val cache = ScalaCache(GuavaCache())

  //Ew - I will totally fix this at some point
  def initialise(releaseHistoryRetriever: ReleaseHistoryRetriever) = {
    this.releaseHistoryRetriever = releaseHistoryRetriever
  }

  def getReleaseHistory: Future[Map[Long, List[ReleasedPipeline]]] = {

    if (releaseHistoryRetriever == null) {
      Future.successful(Map())
    } else {

      memoizeSync(10 minutes) {

        releaseHistoryRetriever.getReleaseHistory()
      }
    }
  }
}