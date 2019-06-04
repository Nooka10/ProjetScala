package controllers

import Utils.ImplicitsJson._
import dao.{BeerDAO, CompanyDAO, OfferDAO}
import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

@Singleton
class StatsController @Inject()(cc: ControllerComponents, offerDAO: OfferDAO, companyDAO: CompanyDAO, beerDAO: BeerDAO) extends AbstractController(cc) {

  /**
    * Retourne les informations de la company la plus fréquentée par les clients de notre BeerPass, ainsi que son nombre de clients.
    *
    * @return les informations de la company la plus fréquentée par les clients de notre BeerPass, ainsi que son nombre de clients.
    */
  def getMostPopularCompany = Action.async {
    offerDAO.getMostPopularCompany.map {
      case (companyId, nbClients) =>
        Ok(Json.obj("mostFamousCompany" -> Json.toJson(Await.result(companyDAO.findById(companyId), Duration.Inf)), "nbClients" -> nbClients))
    }
  }

  /**
    * Retourne les informations de la bière la plus appréciées des clients de notre BeerPass, ainsi que le nombre de fois qu'elle a été commandée par les clients.
    *
    * @return les informations de la bière la plus appréciées des clients de notre BeerPass, ainsi que le nombre de fois qu'elle a été commandée par les clients.
    */
  def getMostFamousBeer = Action.async {
    offerDAO.getMostFamousBeer.map {
      case (beerId, nbClients) => Ok(Json.obj("mostFamousBeer" -> Json.toJson(Await.result(beerDAO.findById(beerId), Duration.Inf)), "nbClients" -> nbClients))
    }
  }
}
