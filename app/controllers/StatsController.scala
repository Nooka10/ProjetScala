package controllers

import Utils.ImplicitsJson._
import dao.OfferDAO
import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class StatsController @Inject()(cc: ControllerComponents, offerDAO: OfferDAO) extends AbstractController(cc) {

  def getMostPopularCompany = Action.async {
    offerDAO.getMostPopularCompany.map(seq => Ok(Json.toJson(seq)))
  }

  def getMostFamousBeer = Action.async {
    offerDAO.getMostFamousBeer.map(seq => Ok(Json.toJson(seq)))
  }
}
