package dao

import javax.inject.{Inject, Singleton}
import models.Beer
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait BeerComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's beers table in a object-oriented entity: the Beer model.
  class BeerTable(tag: Tag) extends Table[Beer](tag, "BEER") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def name = column[String]("NAME")

    def brand = column[String]("BRAND")

    def degreeAlcohol = column[Option[Double]]("DEGREE_ALCOHOL")

    def image = column[Option[String]]("IMAGE")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, name, brand, degreeAlcohol, image) <> (Beer.tupled, Beer.unapply)
  }

  // Get the object-oriented list of courses directly from the query table.
  lazy val beers = TableQuery[BeerTable]
}

// This class contains the object-oriented list of beer and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the beer's query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class BeerDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends BeerComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  /** Retrieve a beer from the id. */
  def findById(id: Long): Future[Option[Beer]] = db.run(beers.filter(_.id === id).result.headOption)

  /** Insert a new course, then return it. */
  def insert(beer: Beer): Future[Beer] = db.run(beers returning beers.map(_.id) into ((beer, id) => beer.copy(Some(id))) += beer)

  /** Update a beer, then return an integer that indicates if the beer was found (1) or not (0). */
  def update(id: Long, beer: Beer): Future[Int] = db.run(beers.filter(_.id === id).update(beer.copy(Some(id))))

  /** Delete a beer, then return an integer that indicates if the beer was found (1) or not (0) */
  def delete(id: Long): Future[Int] = db.run(beers.filter(_.id === id).delete)

  // TODO: ajouter une méthode pour modifier l'horaire, l'adresse, les bières et l'image
}
