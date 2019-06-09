package controllers

import dao.ScheduleDAO
import javax.inject.{Inject, Singleton}
import models.DailySchedule
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ScheduleController @Inject()(cc: ControllerComponents, scheduleDAO: ScheduleDAO) extends AbstractController(cc) {

  /**
    * Enregistre le schedule reçu en paramètre et le link avec la company dont l'id est celui reçu en paramètre.
    *
    * @param schedule  , le schedule à enregistrer.
    * @param companyId , la company avec laquelle relier le nouveau schedule.
    *
    * @return le schedule enregistré.
    */
  def createSchedule(schedule: DailySchedule, companyId: Long): Future[DailySchedule] = scheduleDAO.insert(schedule, companyId)

  /**
    * Retourne tous les schedules de la company correspondante à l'id reçu en paramètre.
    *
    * @param companyId , l'id de la company dont on souhaite récupérer les schedules.
    *
    * @return tous les schedules de la company correspondante à l'id reçu en paramètre.
    */
  def getScheduleFromCompanyId(companyId: Long): Future[Seq[DailySchedule]] = scheduleDAO.findAllDailySchedulesFromCompanyId(companyId)

  /**
    * Met à jour tous les schedules reçus en paramètres ou les enregistre s'il n'existent pas déjà dans la base de données.
    *
    * @param schedules , une séquence de schedules à mettre à jour ou enregistrer.
    * @param companyId , l'id de la company à laquelle relier ces schedules.
    *
    * @return les schedules mis à jour.
    */
  def updateSchedule(schedules: Option[Seq[DailySchedule]], companyId: Long): Option[Future[Seq[DailySchedule]]] = {
    schedules.map(schedules => Future.sequence(schedules.map {
      case schedule if schedule.id.isEmpty => createSchedule(schedule, companyId)
      case schedule => scheduleDAO.update(schedule).map {
        case Some(s: DailySchedule) => s
      }
    }))
  }

  /**
    * Supprime le schedule correspondant à l'id reçu en paramètre.
    *
    * @param scheduleId , l'id du schedule à supprimer.
    *
    * @return 1 si le schedule a bien été supprimée, 0 si l'id ne correspond à aucun schedule dans la BDD.
    */
  def deleteSchedule(scheduleId: Long): Future[Int] = scheduleDAO.delete(scheduleId)
}
