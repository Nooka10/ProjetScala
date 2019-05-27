package controllers

import dao.ScheduleDAO
import javax.inject.{Inject, Singleton}
import models.Schedule
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ScheduleController @Inject()(cc: ControllerComponents, scheduleDAO: ScheduleDAO) extends AbstractController(cc) {

  implicit val scheduleToJson: Writes[Schedule] = (
    (JsPath \ "id").writeNullable[Long] and
      (JsPath \ "day").write[String] and
      (JsPath \ "hOpenAM").write[String] and
      (JsPath \ "hCloseAM").writeNullable[String] and
      (JsPath \ "hOpenPM").writeNullable[String] and
      (JsPath \ "hClosePM").write[String]
    ) (unlift(Schedule.unapply))

  implicit val jsonToSchedule: Reads[Schedule] = (
    (JsPath \ "id").readNullable[Long] and
      (JsPath \ "day").readNullable[String] and
      (JsPath \ "hOpenAM").read[String] and
      (JsPath \ "hCloseAM").readNullable[String] and
      (JsPath \ "hOpenPM").readNullable[String] and
      (JsPath \ "hClosePM").read[String]
    ) (Schedule.apply _)

  def validateJson[A: Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def createSchedule(schedule: Schedule, companyId: Long): Future[Schedule] = scheduleDAO.insert(schedule, companyId)

  def getScheduleFromCompanyId(companyId: Long): Future[Seq[Schedule]] = scheduleDAO.findAllDailySchedulesFromCompanyId(companyId)

  def updateSchedule(schedule: Schedule): Future[Option[Schedule]] = scheduleDAO.update(schedule)

  def deleteSchedule(scheduleId: Long): Future[Int] = scheduleDAO.delete(scheduleId)
}
