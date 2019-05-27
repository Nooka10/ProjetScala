package dao

import javax.inject.{Inject, Singleton}
import models.Offer
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait OfferComponent extends CompanyComponent with UserComponent with BeerComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's offers table in a object-oriented entity: the Offer model.
  class OfferTable(tag: Tag) extends Table[Offer](tag, "OFFER") {
    def companyId = column[Long]("COMPANY_ID", O.PrimaryKey) // Primary key

    def userId = column[Long]("CLIENT_ID", O.PrimaryKey) // Primary key

    def beerId = column[Option[Long]]("BEER_ID")

    def company = foreignKey("COMPANY", companyId, companies)(x => x.id)

    def user = foreignKey("USER", userId, users)(x => x.id)

    def beer = foreignKey("BEER", beerId, beers)(x => x.id)

    // Map the attributes with the model; the ID is optional.
    def * = (companyId, userId, beerId) <> (Offer.tupled, Offer.unapply)
  }

  // Get the object-oriented list of courses directly from the query table.
  lazy val offers = TableQuery[OfferTable]
}

// This class contains the object-oriented list of offer and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the offer's query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class OfferDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends OfferComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import models.OfferWithObjects
  import profile.api._

  /** Retrieve a offer from the id. */
  def findById(companyId: Long, userId: Long, beerId: Option[Long]): Future[Option[OfferWithObjects]] = {

    val query = for {
      offer <- offers
      company <- offer.company if company.id === companyId
      user <- offer.user if user.id === userId && user.userType === "CLIENT"
      beer <- offer.beer
    } yield (company, user, beer)

    val triple = db.run(query.result.headOption)
    triple.map(offer => if (offer.nonEmpty) {
      Some(OfferWithObjects(offer.get._1, offer.get._2, Some(offer.get._3)))
    } else {
      None
    })
  }

  /*
  def findAllOffersOfUser(userId: Long) = ???

  /** Insert a new offer, then return it. */
  def insert(offer: Offer): Future[Offer] = db.run(offers returning offers.map(_.id) into ((offer, id) => offer.copy(Some(id))) += offer)

  /** Update a offer, then return an integer that indicates if the offer was found (1) or not (0). */
  def update(userId: Long, companyId: Long, beerId: Long): Future[Int] = {
    val query = for {
      offer <- offers if offer.userId === offerToUpdate.userId && offer.companyId === offerToUpdate.companyId
    } yield offer

    db.run(offers.filter(_.userId === offer.userId).update(offer.copy(Some(id))))
  }

  /** Delete a offer, then return an integer that indicates if the offer was found (1) or not (0) */
  def delete(userId: Long, companyId: Long): Future[Int] = db.run(offers.filter(_.id === id).delete)
   */
}
