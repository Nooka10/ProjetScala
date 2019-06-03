package controllers

import dao.ScheduleDAO
import javax.inject.{Inject, Singleton}
import models.DailySchedule
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ScheduleController @Inject()(cc: ControllerComponents, scheduleDAO: ScheduleDAO) extends AbstractController(cc) {

  def validateJson[A: Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def createSchedule(schedule: DailySchedule, companyId: Long): Future[DailySchedule] = scheduleDAO.insert(schedule, companyId)

  def getScheduleFromCompanyId(companyId: Long): Future[Seq[DailySchedule]] = scheduleDAO.findAllDailySchedulesFromCompanyId(companyId)

  def updateSchedule(schedules: Option[Seq[DailySchedule]], companyId: Long): Option[Future[Seq[DailySchedule]]] = {
    schedules.map(schedules => Future.sequence(schedules.map {
      case schedule if schedule.id.isEmpty => createSchedule(schedule, companyId)
      case schedule => scheduleDAO.update(schedule).map {
        case Some(s: DailySchedule) => s
      }
    }))
  }

  def deleteSchedule(scheduleId: Long): Future[Int] = scheduleDAO.delete(scheduleId)
  // FIXME: Pourquoi supprimer une company supprime le link_daily_schedule_company mais pas le daily_schedule correspondant?
  // Pourtant, j'ai mis un trigger qui fait qu'avant de supprimer un link_daily_schedule_company il supprime le daily_schedule....
}
