package dao

import javax.inject.{Inject, Singleton}
import models.{DailySchedule, DaysEnum, Link_DailySchedule_Company}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait ScheduleComponent extends CompanyComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  implicit lazy val DaysMapper = MappedColumnType.base[DaysEnum.Value, String](
    e => e.toString,
    s => DaysEnum.withName(s)
  )

  // This class convert the database's schedules table in a object-oriented entity: the Schedule model.
  class ScheduleTable(tag: Tag) extends Table[DailySchedule](tag, "DAILY_SCHEDULE") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def day = column[DaysEnum.Value]("DAY")

    def hOpenAM = column[String]("H_OPEN")

    def hCloseAM = column[Option[String]]("H_CLOSE_AM")

    def hOpenPM = column[Option[String]]("H_OPEN_PM")

    def hClosePM = column[String]("H_CLOSE")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, day, hOpenAM, hCloseAM, hOpenPM, hClosePM) <> (DailySchedule.tupled, DailySchedule.unapply)
  }

  // This class convert the database's LINK_SCHEDULE_COMPANY table in a object-oriented entity: the LINK_SCHEDULE_COMPANY model.
  class LinkScheduleCompanyTable(tag: Tag) extends Table[Link_DailySchedule_Company](tag, "LINK_DAILY_SCHEDULE_COMPANY") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def companyId = column[Long]("COMPANY_ID")

    def dailyScheduleId = column[Long]("DAILY_SCHEDULE_ID")

    def company = foreignKey("COMPANY", companyId, companies)(_.id, onDelete = ForeignKeyAction.Cascade)

    def schedule = foreignKey("SCHEDULE", dailyScheduleId, schedules)(_.id, onDelete = ForeignKeyAction.Cascade)

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, companyId, dailyScheduleId) <> (Link_DailySchedule_Company.tupled, Link_DailySchedule_Company.unapply)
  }

  // Get the object-oriented list of courses directly from the query table.
  lazy val schedules = TableQuery[ScheduleTable]
  lazy val linkScheduleCompany = TableQuery[LinkScheduleCompanyTable]
}

// This class contains the object-oriented list of Link_Schedule_Company and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the Link_Schedule_Company's query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class ScheduleDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends ScheduleComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  /**
    * Retourne tous les schedules de la company correspondante à l'id reçu en paramètre.
    *
    * @param companyId , l'id de la company dont on souhaite récupérer les schedules.
    *
    * @return tous les schedules de la company correspondante à l'id reçu en paramètre.
    */
  def findAllDailySchedulesFromCompanyId(companyId: Long): Future[Seq[DailySchedule]] = {
    val query = for {
      linkSC <- linkScheduleCompany if linkSC.companyId === companyId
      schedule <- linkSC.schedule
    } yield schedule

    db.run(query.result)
  }

  /**
    * Enregistre le schedule reçu en paramètre et le link avec la company dont l'id est celui reçu en paramètre.
    *
    * @param schedule  , le schedule à enregistrer.
    * @param companyId , la company avec laquelle relier le nouveau schedule.
    *
    * @return le schedule enregistré.
    */
  def insert(schedule: DailySchedule, companyId: Long): Future[DailySchedule] = {
    val query = for {
      schedule <- schedules returning schedules.map(_.id) into ((schedule, id) => schedule.copy(Some(id))) += schedule
      _ <- linkScheduleCompany returning linkScheduleCompany.map(_.id) into ((LinkScheduleCompany, id) => LinkScheduleCompany.copy(Some(id))) +=
        Link_DailySchedule_Company(null, companyId, schedule.id.get)
    } yield schedule

    db.run(query)
  }

  /**
    * Met à jour le schedule reçus en paramètres.
    *
    * @param schedule , le schedule à mettre à jour.
    *
    * @return le schedule mis à jour.
    */
  def update(schedule: DailySchedule): Future[Option[DailySchedule]] = {
    val query = for {
      schedule <- schedules.filter(_.id === schedule.id).update(schedule)
    } yield schedule

    db.run(query).map {
      case 0 => None
      case _ => Some(schedule)
    }
  }

  /**
    * Supprime le schedule correspondant à l'id reçu en paramètre.
    *
    * @param scheduleId , l'id du schedule à supprimer.
    *
    * @return 1 si le schedule a bien été supprimée, 0 si l'id ne correspond à aucun schedule dans la BDD.
    */
  def delete(scheduleId: Long): Future[Int] = {
    db.run(linkScheduleCompany.filter(_.dailyScheduleId === scheduleId).delete)
  }
}
