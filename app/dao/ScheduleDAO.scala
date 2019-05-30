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

  /** Retrieve all daily schedules of a company from the companyId. */
  def findAllDailySchedulesFromCompanyId(companyId: Long): Future[Seq[DailySchedule]] = {
    val query = for {
      linkSC <- linkScheduleCompany if linkSC.companyId === companyId
      schedule <- linkSC.schedule
    } yield schedule

    db.run(query.result)
  }

  /** Retrieve the daily schedules of a company from the companyId and the day. */
  def findDailySchedulesFromCompanyIdAndDay(companyId: Long, day: DaysEnum.Value): Future[Option[DailySchedule]] = {
    val query = for {
      linkSC <- linkScheduleCompany if linkSC.companyId === companyId
      schedule <- linkSC.schedule if schedule.day === day
    } yield schedule

    db.run(query.result.headOption)
  }

  /** Insert a new schedule, then return it. */
  def insert(schedule: DailySchedule, companyId: Long): Future[DailySchedule] = {
    // FIXME: C'est correct de faire ça??
    val query = for {
      schedule <- schedules returning schedules.map(_.id) into ((schedule, id) => schedule.copy(Some(id))) += schedule
      _ <- linkScheduleCompany returning linkScheduleCompany.map(_.id) into ((LinkScheduleCompany, id) => LinkScheduleCompany.copy(Some(id))) +=
        Link_DailySchedule_Company(null, companyId, schedule.id.get)
    } yield schedule

    db.run(query)
  }

  /** Update a schedule. */
  def update(schedule: DailySchedule): Future[Option[DailySchedule]] = {
    val query = for {
      schedule <- schedules.filter(_.id === schedule.id).update(schedule)
    } yield schedule

    db.run(query).map {
      case 0 => None
      case _ => Some(schedule)
    }
  }

  /** Delete a schedule, then return an integer that indicates if the LinkScheduleCompany was found (1) or not (0) */
  def delete(scheduleId: Long): Future[Int] = {
    db.run(linkScheduleCompany.filter(_.dailyScheduleId === scheduleId).delete)
  }
}
