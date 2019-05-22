package dao

import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait LinkScheduleHOpenCloseComponent extends ScheduleComponent with HOpenCloseComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import models.Link_Schedule_HOpenClose
  import profile.api._

  // This class convert the database's linkScheduleHOpenCloses table in a object-oriented entity: the LinkScheduleHOpenClose model.
  class LinkScheduleHOpenCloseTable(tag: Tag) extends Table[Link_Schedule_HOpenClose](tag, "Link_Schedule_H_Open_Close") {

    import models.Link_Schedule_HOpenClose

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def scheduleId = column[Long]("SCHEDULE_ID")

    def hOpenCloseId = column[Long]("H_OPEN_CLOSE_ID")

    def schedule = foreignKey("SCHEDULE", scheduleId, schedules)(x => x.id)

    def hOpenClose = foreignKey("H_OPENCLOSE", hOpenCloseId, hOpenCloses)(x => x.id)

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, scheduleId, hOpenCloseId) <> (Link_Schedule_HOpenClose.tupled, Link_Schedule_HOpenClose.unapply)
  }

  // Get the object-oriented list of courses directly from the query table.
  lazy val linkScheduleHOpenCloses = TableQuery[LinkScheduleHOpenCloseTable]
}

// This class contains the object-oriented list of linkScheduleHOpenClose and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the linkScheduleHOpenClose's query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class LinkScheduleHOpenCloseDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends LinkScheduleHOpenCloseComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import models.Link_Schedule_HOpenClose
  import profile.api._

  /** Retrieve a linkScheduleHOpenClose from the id. */
  def findById(id: Long): Future[Option[Link_Schedule_HOpenClose]] = db.run(linkScheduleHOpenCloses.filter(_.id === id).result.headOption)

  /** Insert a new course, then return it. */
  def insert(linkScheduleHOpenClose: Link_Schedule_HOpenClose): Future[Link_Schedule_HOpenClose] = db
    .run(linkScheduleHOpenCloses returning linkScheduleHOpenCloses.map(_.id) into ((linkScheduleHOpenClose, id) => linkScheduleHOpenClose.copy(Some(id))) += linkScheduleHOpenClose)

  /** Update a linkScheduleHOpenClose, then return an integer that indicates if the linkScheduleHOpenClose was found (1) or not (0). */
  def update(id: Long, linkScheduleHOpenClose: Link_Schedule_HOpenClose): Future[Int] = db.run(linkScheduleHOpenCloses.filter(_.id === id).update(linkScheduleHOpenClose.copy(Some(id))))

  /** Delete a linkScheduleHOpenClose, then return an integer that indicates if the linkScheduleHOpenClose was found (1) or not (0) */
  def delete(id: Long): Future[Int] = db.run(linkScheduleHOpenCloses.filter(_.id === id).delete)
}
