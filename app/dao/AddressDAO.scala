package dao

import javax.inject.{Inject, Singleton}
import models.Address
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait AddressComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._
  import scala.concurrent.Await
  import scala.concurrent.duration.Duration
  import slick.dbio.DBIOAction
  
  // This class convert the database's addresses table in a object-oriented entity: the Address model.
  class AddressTable(tag: Tag) extends Table[Address](tag, "ADDRESS") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def no = column[Option[String]]("NO")

    def road = column[String]("ROAD")

    def city = column[String]("CITY")

    def postalCode = column[Int]("POSTAL_CODE")

    def country = column[String]("COUNTRY")

    def lng = column[Double]("LNG")

    def lat = column[Double]("LAT")
    
    // Map the attributes with the model; the ID is optional.
    def * = (id.?, no, road, city, postalCode, country, lng, lat) <> (Address.tupled, Address.unapply)
  }
  
  // Get the object-oriented list of courses directly from the query table.
  lazy val addresses = TableQuery[AddressTable]
  Await.result(db.run(DBIOAction.seq(addresses.schema.createIfNotExists)), Duration.Inf) // FIXME: Est-ce possible de créer toutes les tables d'un coup?
}

// This class contains the object-oriented list of address and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the address's query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class AddressDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends AddressComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  /** Retrieve a address from the id. */
  def findById(id: Long): Future[Option[Address]] = db.run(addresses.filter(_.id === id).result.headOption)

  /** Insert a new course, then return it. */
  def insert(address: Address): Future[Address] = db.run(addresses returning addresses.map(_.id) into ((address, id) => address.copy(Some(id))) += address)

  /** Update a address, then return an integer that indicates if the address was found (1) or not (0). */
  def update(address: Address) = db.run(addresses.filter(_.id === address.id).update(address.copy(address.id)).map{
    case 0 => None
    case _ => Some(address)
  })

  /** Delete a address, then return an integer that indicates if the address was found (1) or not (0) */
  def delete(id: Long): Future[Int] = db.run(addresses.filter(_.id === id).delete)
}
