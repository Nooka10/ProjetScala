package controllers

import dao.{CompanyDAO, UserDAO}
import javax.inject.{Inject, Singleton}
import models.{Address, Company, CompanyWithObjects, Schedule}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

@Singleton
class CompanyController @Inject()(cc: ControllerComponents, companyDAO: CompanyDAO, addressController: AddressController, scheduleController: ScheduleController, userDAO: UserDAO) extends AbstractController(cc) {

  implicit val companyToJson: Writes[CompanyWithObjects] = (
    (JsPath \ "id").writeNullable[Long] and
      (JsPath \ "name").write[String] and
      (JsPath \ "description").writeNullable[String] and
      (JsPath \ "schedule").writeNullable[Schedule](scheduleController.scheduleToJson) and
      (JsPath \ "address").write[Address](addressController.addressToJson) and
      (JsPath \ "image").writeNullable[String]
    ) (unlift(CompanyWithObjects.unapply))

  implicit val jsonToCompany: Reads[CompanyWithObjects] = (
    (JsPath \ "id").readNullable[Long] and
      (JsPath \ "name").read[String](minLength[String](3) keepAnd maxLength[String](30)) and
      (JsPath \ "description").readNullable[String](maxLength[String](300)) and
      (JsPath \ "schedule").readNullable[Schedule](scheduleController.jsonToSchedule) and
      (JsPath \ "address").read[Address](addressController.jsonToAddress) and
      (JsPath \ "image").readNullable[String]
    ) (CompanyWithObjects.apply _)

  def validateJson[A: Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def createCompany = Action.async(validateJson[CompanyWithObjects]) { request =>
    val newAddress: Future[Address] = addressController.createAddress(request.body.address)
    val newSchedule: Option[Seq[Future[Schedule]]] = request.body.schedule
      .map(schedules => schedules.map(schedule => scheduleController.createSchedule(schedule, request.body.id.get)))

    /*
    val a = for {
      schedules <- request.body.schedule
      schedule <- schedules
      future <- scheduleController.createSchedule(schedule, request.body.id.get)
    } yield future
     */

    val future: Future[(Company, Address)] = for {
      address <- newAddress
    } yield (Company(request.body.id, request.body.name, request.body.description, address.id.get, request.body.image), address)

    val results: (Company, Address) = Await.result(future, Duration.Inf) // FIXME: comment le faire attendre toutes les futures de la Seq schedule?

    companyDAO.insert(results._1).map(newCompany => {
      val schedule: Option[Seq[Schedule]] = newSchedule.map(seq => seq.map(s => Await.result(s, Duration.Inf)))
      val c: CompanyWithObjects = CompanyWithObjects.fromCompany(newCompany, results._2, schedule)

      Ok(
        Json.obj(
          "status" -> "OK",
          "id" -> c.id,
          "message" -> ("The company named '" + c.name + "' has been saved."),
          "company infos" -> c,
        )
      )
    })
  }

  def getCompanies = Action.async {
    companyDAO.find.map(companies => Ok(
      Json.obj(
        "status" -> "OK",
        "companies infos" -> companies,
      )))
  }

  def getCompany(companyId: Long) = Action.async {
    val optionalCompany: Future[Option[CompanyWithObjects]] = companyDAO.findById(companyId)

    optionalCompany.map {
      case Some(c) => Ok(c)
      case None =>
        NotFound(Json.obj(
          "status" -> "Not Found",
          "message" -> ("Company #" + companyId + " not found.")
        ))
    }
  }

  def updateCompany(companyId: Long) = Action.async(validateJson[CompanyWithObjects]) { request =>
    val schedule: Option[Seq[Option[Schedule]]] = request.body.schedule
      .map(schedules => schedules.map(s => Await.result(scheduleController.updateSchedule(s), Duration.Inf)))

    val future: Future[(Company, Option[Address])] = for {
      address <- addressController.updateAddress(request.body.address)
    } yield (Company(request.body.id, request.body.name, request.body.description, address.get.id.get, request.body.image), address)

    val results: (Company, Option[Address]) = Await.result(future, Duration.Inf)

    companyDAO.update(companyId, results._1).map {
      case Some(_) => Ok(
        Json.obj(
          "status" -> "OK",
          "message" -> ("The company named '" + request.body.name + "' has been updated."),
          "company info updated" -> Json.toJson(request.body)
        )
      )
      case None => NotFound(Json.obj(
        "status" -> "Not Found",
        "message" -> ("Company #" + companyId + " not found.")
      ))
    }
  }

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

  def getEmployees(companyId: Long) = Action.async {
    userDAO.findAllEmployees(companyId)
      .map { e => Ok(e) } // FIXME: comment régler l'erreur ?? --> Cannot write an instance of Seq[models.User] to HTTP response. Try to define a Writeable[Seq[models.User]]
  }
}
