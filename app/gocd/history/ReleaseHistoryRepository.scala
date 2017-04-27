package gocd.history

import gocd.entities.GoCDEntities.ReleasedPipeline
import play.api.cache.CacheApi

import scala.concurrent.Future
import scala.concurrent.duration._


class ReleaseHistoryRepository(cache: CacheApi) {

  val CACHE_KEY = "RELEASE"

  private var releaseHistoryRetriever: ReleaseHistoryRetriever = null

  //Ew - I will totally fix this at some point
  def initialise(releaseHistoryRetriever: ReleaseHistoryRetriever) = {
    this.releaseHistoryRetriever = releaseHistoryRetriever
  }


  def getReleaseHistory: Future[Map[Long, List[ReleasedPipeline]]] = {

    if (releaseHistoryRetriever == null) {
      Future.successful(Map())
    } else {

      cache.get(CACHE_KEY)
        .getOrElse{
          val history = releaseHistoryRetriever.getReleaseHistory()

          cache.set(CACHE_KEY, history, 10.minutes)
          history
        }
    }
  }
}