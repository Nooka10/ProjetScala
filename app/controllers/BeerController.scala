package controllers

import Utils.ImplicitsJson._
import dao.BeerDAO
import javax.inject.{Inject, Singleton}
import models.Beer
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class BeerController @Inject()(cc: ControllerComponents, beerDAO: BeerDAO) extends AbstractController(cc) {

  def validateJson[A: Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def createBeer = Action.async(validateJson[Beer]) { request =>
    beerDAO.insert(request.body).map(beer => Ok(
      Json.obj(
        "status" -> "OK",
        "id" -> beer.id,
        "message" -> ("The beer named '" + beer.name + "' has been saved."),
        "beerInfos" -> beer,
      )
    ))
  }

  def getBeers = Action.async {
    beerDAO.find.map(beers => Ok(Json.toJson(beers)))
  }

  def getAllBeersOfCompany(companyId: Long) = Action.async {
    beerDAO.getAllBeersOfCompany(companyId).map(beers => Ok(Json.toJson(beers)))
  }

  def getBeer(beerId: Long) = Action.async {
    beerDAO.findById(beerId).map(beer => Ok(Json.toJson(beer)))
  }

  def getOneBeersOfCompany(companyId: Long, beerId: Long) = Action.async {
    beerDAO.getOneBeerOfCompany(companyId, beerId).map(beers => Ok(Json.toJson(beers)))
  }

  def addBeerToDrinkListOfCompany(companyId: Long, beerId: Long) = Action.async {
    beerDAO.addBeerToDrinkListOfCompany(companyId, beerId).map(result => Ok(
      Json.obj(
        "status" -> "OK",
        "message" -> ("The beer #" + beerId + " has been added to the drink list of the company #" + companyId + "."),
        "beerInfos" -> result._2,
        "companyInfos" -> result._1,
      )
    ))
  }

  def removeBeerFromDrinkListOfCompany(companyId: Long, beerId: Long) = Action.async {
    beerDAO.removeBeerFromDrinkListOfCompany(companyId, beerId).map {
      case 1 => Ok(
        Json.obj(
          "status" -> "OK",
          "message" -> ("Beer with id #" + beerId + " has been removed from the drink list of the company with id #" + companyId + ".")
        )
      )
      case 0 => NotFound(Json.obj(
        "status" -> "Not Found",
        "message" -> ("Company #" + companyId + " or beer #" + beerId + " not found.")
      ))
    }
  }

  def updateBeer = Action.async(validateJson[Beer]) {
    request =>
      if (request.body.id.isEmpty) {
        import scala.concurrent.Future
        Future(BadRequest(Json.obj(
          "status" -> "Bad Request",
          "message" -> "The field 'id' is missing!"
        )))
      }

      val beerId: Long = request.body.id.get
      beerDAO.update(request.body).map {
        case Some(beer) => Ok(
          Json.obj(
            "status" -> "OK",
            "message" -> ("Beer '" + beer.name + "' updated."),
            "beerInfo updated" -> beer
          )
        )
        case None => NotFound(Json.obj(
          "status" -> "Not Found",
          "message" -> ("Beer #" + beerId + " not found.")
        ))
      }
  }

  def deleteBeer(beerId: Long) = Action.async {
    beerDAO.delete(beerId).map {
      case 1 => Ok(
        Json.obj(
          "status" -> "OK",
          "message" -> ("User #" + beerId + " deleted.")
        )
      )
      case 0 => NotFound(Json.obj(
        "status" -> "Not Found",
        "message" -> ("User #" + beerId + " not found.")
      ))
    }
  }
}
