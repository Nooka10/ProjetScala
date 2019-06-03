package controllers

import dao.{BeerDAO, CompanyDAO, OfferDAO}
import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import Utils.ImplicitsJson._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class StatsController @Inject()(cc: ControllerComponents, offerDAO: OfferDAO, companyDAO: CompanyDAO, beerDAO: BeerDAO) extends AbstractController(cc) {

  def getMostPopularCompany = Action.async {
    offerDAO.getMostPopularCompany.map {
      case (companyId, nbClients) =>
        Ok(Json.obj("mostFamousCompany" -> Json.toJson(Await.result(companyDAO.findById(companyId), Duration.Inf)), "nbClients" -> nbClients))
    }
  }

  def getMostFamousBeer = Action.async {
    offerDAO.getMostFamousBeer.map {
      case (beerId, nbClients) => Ok(Json.obj("mostFamousBeer" -> Json.toJson(Await.result(beerDAO.findById(beerId), Duration.Inf)), "nbClients" -> nbClients))
    }
  }
}
