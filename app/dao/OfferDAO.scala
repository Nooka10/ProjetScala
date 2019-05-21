package dao

import javax.inject.{Inject, Singleton}
import models.Offer
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait OfferComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // val OfferTable: OfferTable

  // This class convert the database's offers table in a object-oriented entity: the Offer model.
  class OfferTable(tag: Tag) extends Table[Offer](tag, "BEER") {
    def companyId = column[Long]("COMPANY_ID", O.PrimaryKey) // Primary key

    def clientId = column[Long]("CLIENT_ID", O.PrimaryKey) // Primary key

    def beerId = column[Option[Long]]("BEER_ID")

    // lazy val offer = TableQuery[OfferTable]

    // def offer = foreignKey("ADDRESS", offerId, offeres)(x => x.id)

    // Map the attributes with the model; the ID is optional.
    def * = (companyId, clientId, beerId) <> (Offer.tupled, Offer.unapply)
  }

}

// This class contains the object-oriented list of offer and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the offer's query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class OfferDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends OfferComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  // Get the object-oriented list of courses directly from the query table.
  val offers = TableQuery[OfferTable]

  /*
  /** Retrieve a offer from the id. */
  def findById(companyId: Long, clientId: Long): Future[Option[Offer]] = db.run(offers.filter(_.companyId === companyId && _.clientId === clientId).result.headOption)

  /** Insert a new course, then return it. */
  def insert(offer: Offer): Future[Offer] = db.run(offers returning offers.map(_.id) into ((offer, id) => offer.copy(Some(id))) += offer)

  /** Update a offer, then return an integer that indicates if the offer was found (1) or not (0). */
  def update(id: Long, offer: Offer): Future[Int] = db.run(offers.filter(_.id === id).update(offer.copy(Some(id))))

  /** Delete a offer, then return an integer that indicates if the offer was found (1) or not (0) */
  def delete(id: Long): Future[Int] = db.run(offers.filter(_.id === id).delete)
   */

  // TODO: ajouter une méthode pour modifier l'horaire, l'adresse, les bières et l'image
}
