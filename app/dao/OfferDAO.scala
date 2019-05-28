package dao

import javax.inject.{Inject, Singleton}
import models.Offer
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait OfferComponent extends CompanyComponent with UserComponent with BeerComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._
  import scala.concurrent.Await
  import scala.concurrent.duration.Duration
  import slick.dbio.DBIOAction

  // This class convert the database's offers table in a object-oriented entity: the Offer model.
  class OfferTable(tag: Tag) extends Table[Offer](tag, "OFFER") {
    def companyId = column[Long]("COMPANY_ID")

    def userId = column[Long]("CLIENT_ID")

    // def pKey = primaryKey("KEYS", (companyId, userId))

    def beerId = column[Option[Long]]("BEER_ID")

    def company = foreignKey("COMPANY", companyId, companies)(x => x.id)

    def user = foreignKey("USER", userId, users)(x => x.id)

    def beer = foreignKey("BEER", beerId, beers)(x => x.id)

    // Map the attributes with the model; the ID is optional.
    def * = (companyId, userId, beerId) <> (Offer.tupled, Offer.unapply)
  }

  // Get the object-oriented list of courses directly from the query table.
  lazy val offers = TableQuery[OfferTable]
  Await.result(db.run(DBIOAction.seq(offers.schema.createIfNotExists)), Duration.Inf) // FIXME: Est-ce possible de créer toutes les tables d'un coup?
}

// This class contains the object-oriented list of offer and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the offer's query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class OfferDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends OfferComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import models.{Beer, Company, OfferWithObjects, User, UserTypeEnum}
  import profile.api._

  /** Retrieve an offer from his companyId and userId. */
  def findById(companyId: Long, userId: Long): Future[Option[OfferWithObjects]] = {
    val query = for {
      offer <- offers
      company <- offer.company if company.id === companyId
      user <- offer.user if user.id === userId && user.userType === UserTypeEnum.CLIENT
      beer <- offer.beer
    } yield (company, user, beer)

    val triple: Future[Option[(Company, User, Beer)]] = db.run(query.result.headOption)
    triple.map(x => x.map(offer => OfferWithObjects(offer._1, offer._2, Some(offer._3))))
  }

  /** Retrieve all offers of a user from his userId. */
  def findAllOffersOfUser(userId: Long): Future[Seq[OfferWithObjects]] = {
    val query = for {
      offer <- offers
      user <- offer.user if user.id === userId && user.userType === UserTypeEnum.CLIENT
      company <- offer.company
      beer <- offer.beer
    } yield (company, user, beer)

    db.run(query.result).map(x => x.map(offer => OfferWithObjects(offer._1, offer._2, Some(offer._3))))
  }

  def findAllUnusedOffersOfUser(userId: Long): Future[Seq[OfferWithObjects]] = {
    val query = for {
      offer <- offers if offer.beerId.isEmpty // si beerId est null -> l'offre n'a pas encore été utilisée
      user <- offer.user if user.id === userId && user.userType === UserTypeEnum.CLIENT
      company <- offer.company
      beer <- offer.beer
    } yield (company, user, beer)

    db.run(query.result).map(x => x.map(offer => OfferWithObjects(offer._1, offer._2, Some(offer._3))))
  }

  /** Insert a new offer, then return it. */
  def insert(offer: Offer): Future[Offer] = db.run(offers returning offers.map(o => (o.companyId, o.userId)) into ((offer, ids) => offer.copy(ids._1, ids._2)) += offer)

  /** Update a offer, then return an integer that indicates if the offer was found (1) or not (0). */
  def update(userId: Long, companyId: Long, beerId: Long): Future[Int] = db
    .run(offers.filter(e => e.companyId === companyId && e.userId === userId).map(_.beerId).update(Some(beerId)))

  /** Delete a offer, then return an integer that indicates if the offer was found (1) or not (0) */
  def delete(userId: Long, companyId: Long): Future[Int] = db.run(offers.filter(e => e.companyId === companyId && e.userId === userId).delete)
}
