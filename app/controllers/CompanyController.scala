package controllers

import dao.{CompanyDAO, UserDAO}
import javax.inject.{Inject, Singleton}
import models.{Address, Company, CompanyWithObjects, DailySchedule}
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import Utils.ImplicitsJson._

@Singleton
class CompanyController @Inject()(cc: ControllerComponents, companyDAO: CompanyDAO, addressController: AddressController, scheduleController: ScheduleController, userDAO: UserDAO) extends AbstractController(cc) {

  def validateJson[A: Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def createCompany = Action.async(validateJson[CompanyWithObjects]) { request =>
    val newAddress: Future[Address] = addressController.createAddress(request.body.address)
    val newSchedule: Option[Future[Seq[DailySchedule]]] = request.body.schedules
      .map(schedules => Future.sequence(schedules.map(schedule => scheduleController.createSchedule(schedule, request.body.id.get))))

    val future: Future[(Company, Address)] = for {
      address <- newAddress
    } yield (Company(request.body.id, request.body.name, request.body.description, address.id.get, request.body.image), address)

    val results: (Company, Address) = Await.result(future, Duration.Inf)
    companyDAO.insert(results._1).map(newCompany => {
      val schedule: Option[Seq[DailySchedule]] = newSchedule.map(seq => Await.result(seq, Duration.Inf))
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
    val optionalCompany = companyDAO.findById(companyId)

    optionalCompany.map {
      case Some(c) => Ok(Json.toJson(c))
      case None =>
        NotFound(Json.obj(
          "status" -> "Not Found",
          "message" -> ("Company #" + companyId + " not found.")
        ))
    }
  }

  def updateCompany(companyId: Long) = Action.async(validateJson[CompanyWithObjects]) { request =>
    val schedule: Option[Seq[Option[DailySchedule]]] = request.body.schedules
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
          "company info updated" -> request.body
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
      .map { e => Ok(Json.toJson(e)) }
  }
}
