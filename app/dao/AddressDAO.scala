package dao

import javax.inject.{Inject, Singleton}
import models.Address
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait AddressComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // val AddressTable: AddressTable

  // This class convert the database's addresss table in a object-oriented entity: the Address model.
  class AddressTable(tag: Tag) extends Table[Address](tag, "ADDRESS") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def no = column[Option[String]]("NO")

    def road = column[String]("ROAD")

    def city = column[String]("CITY")

    def postalCode = column[Int]("POSTAL_CODE")

    def country = column[String]("COUNTRY")

    def lng = column[Double]("LNG")

    def lat = column[Double]("LAT")

    // lazy val addresses = TableQuery[AddressTable]

    // def address = foreignKey("ADDRESS", addressId, addresses)(x => x.id)

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, no, road, city, postalCode, country, lng, lat) <> (Address.tupled, Address.unapply)
  }

}

// This class contains the object-oriented list of address and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the address's query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class AddressDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends AddressComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  // Get the object-oriented list of courses directly from the query table.
  val addresss = TableQuery[AddressTable]

  /** Retrieve a address from the id. */
  def findById(id: Long): Future[Option[Address]] = db.run(addresss.filter(_.id === id).result.headOption)

  /** Insert a new course, then return it. */
  def insert(address: Address): Future[Address] = db.run(addresss returning addresss.map(_.id) into ((address, id) => address.copy(Some(id))) += address)

  /** Update a address, then return an integer that indicates if the address was found (1) or not (0). */
  def update(id: Long, address: Address): Future[Int] = db.run(addresss.filter(_.id === id).update(address.copy(Some(id))))

  /** Delete a address, then return an integer that indicates if the address was found (1) or not (0) */
  def delete(id: Long): Future[Int] = db.run(addresss.filter(_.id === id).delete)

  // TODO: ajouter une méthode pour modifier l'horaire, l'adresse, les bières et l'image
}
