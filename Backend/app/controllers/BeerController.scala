package controllers

import Utils.ImplicitsJson._
import dao.BeerDAO
import javax.inject.{Inject, Singleton}
import models.Beer
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class BeerController @Inject()(cc: ControllerComponents, beerDAO: BeerDAO) extends AbstractController(cc) {

  def validateJson[A: Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  /**
    * Enregistre la nouvelle bière reçue dans le body de la requête.
    *
    * @return Ok() / BadRequest()
    */
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

  /**
    * Retourne toutes les bières présentes dans la BDD.
    *
    * @return Ok()
    */
  def getBeers = Action.async {
    beerDAO.find.map(beers => Ok(Json.toJson(beers)))
  }

  /**
    * Retourne toutes les bières que propose la companie correspondant à l'id reçu en paramètre.
    *
    * @param companyId , l'id de la company dont on souhaite récupérer la liste de boissons proposées.
    *
    * @return Ok()
    */
  def getAllBeersOfCompany(companyId: Long) = Action.async {
    beerDAO.getAllBeersOfCompany(companyId).map(beers => Ok(Json.toJson(beers)))
  }

  /**
    * Retourne la bière correspondante à l'id reçu en paramètre.
    *
    * @param beerId , l'id de la bière que l'on souhaite récupérer.
    *
    * @return Ok()
    */
  def getBeer(beerId: Long) = Action.async {
    beerDAO.findById(beerId).map(beer => Ok(Json.toJson(beer)))
  }

  /**
    * Retourne la bières proposée par la companie correspondant au companyId reçu en paramètre et correspondant au beerId reçu.
    *
    * @param companyId , l'id de la company dont on souhaite récupérer une bière de la liste de boissons proposées.
    * @param beerId    , l'id de la bière que l'on souhaite récupérer.
    *
    * @return Ok()
    */
  def getOneBeersOfCompany(companyId: Long, beerId: Long) = Action.async {
    beerDAO.getOneBeerOfCompany(companyId, beerId).map(beers => Ok(Json.toJson(beers)))
  }

  /**
    * Ajoute la bière avec l'id beerId à la liste des boissons proposées par la company avec l'id companyId.
    *
    * @param companyId , l'id de la company pour laquelle on souhaite ajouter une boisson à la liste de boissons proposées.
    * @param beerId    , l'id de la bière que l'on souhaite ajouter à la liste de boissons proposées par la company.
    *
    * @return Ok()
    */
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

  /**
    * Supprime la bière avec l'id beerId de la liste des boissons proposées par la company avec l'id companyId.
    *
    * @param companyId , l'id de la company pour laquelle on souhaite supprimer une boisson de la liste de boissons proposées.
    * @param beerId    ,l'id de la bière que l'on souhaite supprimer de la liste de boissons proposées par la company.
    *
    * @return Ok() / NotFound()
    */
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

  /**
    * Met à jour la bières reçue dans le body de la requête.
    *
    * @return Ok() / BadRequest() / NotFound()
    */
  def updateBeer = Action.async(validateJson[Beer]) {
    request =>
      if (request.body.id.isEmpty) {
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

  /**
    * Supprime la bières correspondant à l'id reçu.
    *
    * @param beerId , l'id de la bière que l'on souhaite supprimer.
    *
    * @return Ok() / NotFound()
    */
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
