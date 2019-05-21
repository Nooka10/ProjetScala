package dao

import javax.inject.{Inject, Singleton}
import models.HOpenClose
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait HOpenCloseComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's hOpenCloses table in a object-oriented entity: the HOpenClose model.
  class HOpenCloseTable(tag: Tag) extends Table[HOpenClose](tag, "H_OPEN_CLOSE") {

    import models.HOpenClose

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def hOpen = column[String]("H_OPEN")

    def hClose = column[String]("H_CLOSE")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, hOpen, hClose) <> (HOpenClose.tupled, HOpenClose.unapply)
  }

}

// This class contains the object-oriented list of hOpenClose and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the hOpenClose's query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class HOpenCloseDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HOpenCloseComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  // Get the object-oriented list of courses directly from the query table.
  val hOpenCloses = TableQuery[HOpenCloseTable]

  /** Retrieve a hOpenClose from the id. */
  def findById(id: Long): Future[Option[HOpenClose]] = db.run(hOpenCloses.filter(_.id === id).result.headOption)

  /** Insert a new course, then return it. */
  def insert(hOpenClose: HOpenClose): Future[HOpenClose] = db
    .run(hOpenCloses returning hOpenCloses.map(_.id) into ((hOpenClose, id) => hOpenClose.copy(Some(id))) += hOpenClose)

  /** Update a hOpenClose, then return an integer that indicates if the hOpenClose was found (1) or not (0). */
  def update(id: Long, hOpenClose: HOpenClose): Future[Int] = db.run(hOpenCloses.filter(_.id === id).update(hOpenClose.copy(Some(id))))

  /** Delete a hOpenClose, then return an integer that indicates if the hOpenClose was found (1) or not (0) */
  def delete(id: Long): Future[Int] = db.run(hOpenCloses.filter(_.id === id).delete)
}
