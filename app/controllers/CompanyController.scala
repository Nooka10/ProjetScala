package controllers

import Utils.ImplicitsJson._
import dao.{CompanyDAO, OfferDAO, UserDAO}
import javax.inject.{Inject, Singleton}
import models.{Address, Company, CompanyWithObjects, DailySchedule}
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

@Singleton
class CompanyController @Inject()(cc: ControllerComponents, companyDAO: CompanyDAO, addressController: AddressController, scheduleController: ScheduleController, userDAO: UserDAO, offerDAO: OfferDAO) extends AbstractController(cc) {

  def validateJson[A: Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  /**
    * Enregistre la nouvelle company reçue dans le body de la requête.
    *
    * @return Ok() / BadRequest()
    */
  def createCompany = Action.async(validateJson[CompanyWithObjects]) { request =>
    val future: Future[(Company, Address)] = for {
      address <- addressController.createAddress(request.body.address)
    } yield (Company(request.body.id, request.body.name, request.body.description, address.id.get, request.body.image), address)

    val results: (Company, Address) = Await.result(future, Duration.Inf)
    companyDAO.insert(results._1).map(newCompany => {
      // Pour chaque client présent dans la BDD, on ajoute une offre pour cette nouvelle companie
      Await.result(offerDAO.createOffersForNewCompany(newCompany.id.get), Duration.Inf)

      // on enregistre tout les schedules de la company et on attend qu'ils soient tous bien enregistrés
      val newSchedule: Option[Future[Seq[DailySchedule]]] = request.body.schedules.map(schedules => Future.sequence(schedules.map(schedule => scheduleController.createSchedule(schedule, newCompany.id.get))))
      val schedule: Option[Seq[DailySchedule]] = newSchedule.map(seq => Await.result(seq, Duration.Inf))

      // on prépare le companyWithObject qui sera retourné
      val c: CompanyWithObjects = CompanyWithObjects.fromCompany(newCompany, results._2, schedule)

      Ok(
        Json.obj(
          "status" -> "OK",
          "id" -> c.id,
          "message" -> ("The company named '" + c.name + "' has been saved."),
          "companyInfos" -> c,
        )
      )
    })
  }

  /**
    * Retourne toutes les company présentes dans la BDD.
    *
    * @return Ok()
    */
  def getCompanies = Action.async {
    companyDAO.find.map(companies => Ok(
      Json.obj(
        "status" -> "OK",
        "companiesInfos" -> companies,
      )))
  }

  /**
    * Retourne la company correspondant à l'id reçu en paramètre.
    *
    * @param companyId , l'id de la company à retourner.
    *
    * @return Ok() / NotFound()
    */
  def getCompany(companyId: Long) = Action.async {
    companyDAO.findById(companyId).map {
      case Some(c) => Ok(Json.toJson(c))
      case None =>
        NotFound(Json.obj(
          "status" -> "Not Found",
          "message" -> ("Company #" + companyId + " not found.")
        ))
    }
  }

  /**
    * Met à jour la company avec les informations reçues dans le body de la requête.
    *
    * @return Ok() / NotFound() / BadRequest()
    */
  def updateCompany = Action.async(validateJson[CompanyWithObjects]) { request =>
    if (request.body.id.isEmpty) {
      Future(BadRequest(Json.obj(
        "status" -> "Bad Request",
        "message" -> "The field 'id' is missing!"
      )))
    }

    val companyId: Long = request.body.id.get
    val schedule: Option[Seq[DailySchedule]] = request.body.schedules

    val future: Future[(Company, Option[Address])] = for {
      schedules <- scheduleController.updateSchedule(schedule, companyId)
        .getOrElse(Future(None)) // on update les schedules de la company (ou enregistre les nouveaux s'il y en a)
      address <- addressController.updateAddress(request.body.address)
    } yield (Company(request.body.id, request.body.name, request.body.description, address.get.id.get, request.body.image), address)

    val results: (Company, Option[Address]) = Await.result(future, Duration.Inf) // on attend que toutes les futures aient été résolues

    companyDAO.update(results._1).map {
      case Some(_) => Ok(
        Json.obj(
          "status" -> "OK",
          "message" -> ("The company named '" + request.body.name + "' has been updated."),
          "companyInfo" -> request.body
        )
      )
      case None => NotFound(Json.obj(
        "status" -> "Not Found",
        "message" -> ("Company #" + companyId + " not found.")
      ))
    }
  }

  /**
    * Supprime la company correspondant à l'id reçu en paramètre.
    *
    * @return Ok() / NotFound()
    */
  def deleteCompany(companyId: Long) = Action.async {
    companyDAO.delete(companyId).map {
      case 1 => Ok(
        Json.obj(
          "status" -> "OK",
          "message" -> ("Company #" + companyId + " deleted.")
        )
      )
      case 0 => NotFound(Json.obj(
        "status" -> "Not Found",
        "message" -> ("Company #" + companyId + " not found.")
      ))
    }
  }

  /**
    * Retourne tous les employés de la company correspondante à l'id reçu en paramètre.
    *
    * @param companyId , l'id de la company dont on souhaite récupérer les employés.
    *
    * @return tous les employés de la company correspondante à l'id reçu en paramètre.
    */
  def getEmployees(companyId: Long) = Action.async {
    userDAO.findAllEmployees(companyId).map { e => Ok(Json.toJson(e)) }
  }
}
