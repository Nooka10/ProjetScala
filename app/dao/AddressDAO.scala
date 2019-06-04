package dao

import javax.inject.{Inject, Singleton}
import models.Address
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait AddressComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's address table in a object-oriented entity: the Address model.
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
}

// This class contains the object-oriented list of address and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the address's query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class AddressDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends AddressComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  /**
    * Retourne l'addresse correspondante à l'id reçu en paramètre.
    *
    * @param id , l'id de l'addresse à retourner.
    *
    * @return l'adresse correspondante à l'id reçu en paramètre.
    */
  def findById(id: Long): Future[Option[Address]] = db.run(addresses.filter(_.id === id).result.headOption)

  /**
    * Enregistre la nouvelle addresse reçue en paramètre.
    *
    * @param address , l'addresse à enregistrer dans la BDD.
    *
    * @return l'addresse enregistrée
    */
  def insert(address: Address): Future[Address] = db.run(addresses returning addresses.map(_.id) into ((address, id) => address.copy(Some(id))) += address)

  /**
    * Met à jour l'addresse reçue en paramètre.
    *
    * @param address , l'addresse à mettre à jour.
    *
    * @return l'addresse mise à jour.
    */
  def update(address: Address): Future[Option[Address]] = db.run(addresses.filter(_.id === address.id).update(address.copy(address.id)).map{
    case 0 => None
    case _ => Some(address)
  })

  /**
    * Supprime l'addresse correspondant à l'id reçu en paramètre.
    *
    * @param id , l'id de l'addresse à supprimer.
    *
    * @return 1 si l'addresse a bien été supprimée, 0 si l'id ne correspond à aucune addresse dans la BDD.
    */
  def delete(id: Long): Future[Int] = db.run(addresses.filter(_.id === id).delete)
}
