package dao

import javax.inject.{Inject, Singleton}
import models.Schedule
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait ScheduleComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's schedules table in a object-oriented entity: the Schedule model.
  class ScheduleTable(tag: Tag) extends Table[Schedule](tag, "SCHEDULE") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def day = column[String]("DAY")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, day) <> (Schedule.tupled, Schedule.unapply)
  }

  // Get the object-oriented list of courses directly from the query table.
  lazy val schedules = TableQuery[ScheduleTable]
}

// This class contains the object-oriented list of schedule and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the schedule's query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class ScheduleDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends ScheduleComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  /** Retrieve a schedule from the id. */
  def findById(id: Long): Future[Option[Schedule]] = db.run(schedules.filter(_.id === id).result.headOption)

  /** Insert a new course, then return it. */
  def insert(schedule: Schedule): Future[Schedule] = db.run(schedules returning schedules.map(_.id) into ((schedule, id) => schedule.copy(Some(id))) += schedule)

  /** Update a schedule, then return an integer that indicates if the schedule was found (1) or not (0). */
  def update(id: Long, schedule: Schedule): Future[Int] = db.run(schedules.filter(_.id === id).update(schedule.copy(Some(id))))

  /** Delete a schedule, then return an integer that indicates if the schedule was found (1) or not (0) */
  def delete(id: Long): Future[Int] = db.run(schedules.filter(_.id === id).delete)
}
