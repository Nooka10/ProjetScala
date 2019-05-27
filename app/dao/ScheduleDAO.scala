package dao

import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait ScheduleComponent extends CompanyComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import models.{Link_Schedule_Company, Schedule}
  import profile.api._

  // This class convert the database's schedules table in a object-oriented entity: the Schedule model.
  class ScheduleTable(tag: Tag) extends Table[Schedule](tag, "DAILY_SCHEDULE") {

    import models.Schedule

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def day = column[String]("DAY")

    def hOpenAM = column[String]("H_OPEN_AM")

    def hCloseAM = column[Option[String]]("H_CLOSE_AM")

    def hOpenPM = column[Option[String]]("H_OPEN_PM")

    def hClosePM = column[String]("H_CLOSE_PM")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, day, hOpenAM, hCloseAM, hOpenPM, hClosePM) <> (Schedule.tupled, Schedule.unapply)
  }

  // This class convert the database's linkScheduleHOpenCloses table in a object-oriented entity: the LinkScheduleHOpenClose model.
  class LinkScheduleCompanytable(tag: Tag) extends Table[Link_Schedule_Company](tag, "Link_Schedule_H_Open_Close") {

    import models.Link_Schedule_Company

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def companyId = column[Long]("COMPANY_ID")

    def scheduleId = column[Long]("DAILY_SCHEDULE_ID")

    def company = foreignKey("COMPANY", companyId, companies)(x => x.id)

    def schedule = foreignKey("SCHEDULE", scheduleId, schedules)(x => x.id)


    // Map the attributes with the model; the ID is optional.
    def * = (id.?, companyId, scheduleId) <> (Link_Schedule_Company.tupled, Link_Schedule_Company.unapply)
  }

  // Get the object-oriented list of courses directly from the query table.
  lazy val schedules = TableQuery[ScheduleTable]
  lazy val linkScheduleCompany = TableQuery[LinkScheduleCompanytable]
}

// This class contains the object-oriented list of Link_Schedule_Company and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the Link_Schedule_Company's query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class ScheduleDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends ScheduleComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import models.{Link_Schedule_Company, Schedule}
  import profile.api._

  /** Retrieve all daily schedules of a company from the companyId. */
  def findAllDailySchedulesFromCompanyId(companyId: Long): Future[Seq[Schedule]] = {
    val query = for {
      linkSC <- linkScheduleCompany if linkSC.companyId === companyId
      schedule <- linkSC.schedule
    } yield schedule

    db.run(query.result)
  }

  /** Retrieve the daily schedules of a company from the companyId and the day. */
  def findDailySchedulesFromCompanyIdAndDay(companyId: Long, day: String): Future[Option[Schedule]] = {
    val query = for {
      linkSC <- linkScheduleCompany if linkSC.companyId === companyId
      schedule <- linkSC.schedule if schedule.day === day
    } yield schedule

    db.run(query.result.headOption)
  }

  /** Insert a new schedule, then return it. */
  def insert(schedule: Schedule, companyId: Long): Future[Schedule] = {
    val query = for {
      schedule <- schedules returning schedules.map(_.id) into ((schedule, id) => schedule.copy(Some(id))) += schedule
      _ <- linkScheduleCompany returning linkScheduleCompany.map(_.id) into ((LinkScheduleCompany, id) => LinkScheduleCompany.copy(Some(id))) +=
        Link_Schedule_Company(null, companyId, schedule.id.get)
    } yield schedule

    db.run(query)
  }

  /** Update a schedule. */
  def update(schedule: Schedule): Future[Option[Schedule]] = {
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
    val query = for {
      linkSC <- linkScheduleCompany.filter(_.scheduleId === scheduleId).delete
      schedule <- schedules.filter(_.id === scheduleId).delete if linkSC == 1 // TODO: est-ce que faire le ==1 suffit pour ne pas le supprimer si la suppression de linkSC échoue?
    } yield schedule

    db.run(query)
  }
}
