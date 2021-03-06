package dao

import javax.inject.{Inject, Singleton}
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait OfferComponent extends CompanyComponent with UserComponent with BeerComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's offers table in a object-oriented entity: the Offer model.
  class OfferTable(tag: Tag) extends Table[OfferWithID](tag, "OFFER") {
    def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def companyId = column[Long]("COMPANY_ID")

    def clientId = column[Long]("CLIENT_ID")

    def pKey = primaryKey("KEYS", (companyId, clientId))

    def beerId = column[Option[Long]]("BEER_ID")

    def company = foreignKey("COMPANY", companyId, companies)(_.id)

    def user = foreignKey("USER", clientId, users)(_.id)

    def beer = foreignKey("BEER", beerId, beers)(_.id)

    // Map the attributes with the model; the ID is optional.
    def * = (companyId, clientId, beerId, id) <> (OfferWithID.tupled, OfferWithID.unapply)
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

  import profile.api._

  /**
    * Retourne l'offre correspondant au companyId et clientId reçus en paramètres.
    *
    * @param companyId
    * @param clientId
    *
    * @return l'offre correspondant au companyId et clientId reçus en paramètres.
    */
  def findById(companyId: Long, clientId: Long): Future[Option[OfferWithObjects]] = {
    val query = for {
      offer <- offers
      company <- offer.company if company.id === companyId
      user <- offer.user if user.id === clientId && user.userType === UserTypeEnum.CLIENT
      beer <- offer.beer
    } yield (company, user, beer)

    val triple: Future[Option[(Company, User, Beer)]] = db.run(query.result.headOption)
    triple.map(x => x.map(offer => OfferWithObjects(offer._1, offer._2, Some(offer._3))))
  }

  /**
    * Retourne toutes les offres de l'utilisateur correspondant à l'id reçu en paramètre.
    *
    * @param clientId l'id de l'utilisateur dont on souhaite récupérer toutes les offres.
    *
    * @return toutes les offres de l'utilisateur correspondant à l'id reçu en paramètre.
    */
  def findAllOffersOfUser(clientId: Long): Future[Seq[OfferWithObjects]] = {
    val query = for {
      (offer, beer) <- offers joinLeft beers on (_.beerId === _.id)
      user <- offer.user if user.id === clientId && user.userType === UserTypeEnum.CLIENT
      company <- offer.company
    } yield (company, user, beer)

    db.run(query.result).map(_.map(offer => OfferWithObjects(offer._1, offer._2, offer._3)))
  }

  /**
    * Retourne toutes les offres non utilisées de l'utilisateur correspondant à l'id reçu en paramètre.
    *
    * @param clientId l'id de l'utilisateur dont on souhaite récupérer toutes les offres non utilisées.
    *
    * @return toutes les offres non utilisées de l'utilisateur correspondant à l'id reçu en paramètre.
    */
  def findAllUnusedOffersOfUser(clientId: Long): Future[Seq[OfferWithObjects]] = {
    val query = for {
      (offer, beer) <- offers joinLeft beers on (_.beerId === _.id) if offer.beerId.isEmpty // si beerId est null -> l'offre n'a pas encore été utilisée
      user <- offer.user if user.id === clientId && user.userType === UserTypeEnum.CLIENT
      company <- offer.company
    } yield (company, user, beer)

    db.run(query.result).map(_.map(offer => OfferWithObjects(offer._1, offer._2, offer._3)))
  }

  /**
    * Retourne toutes les offres utilisées de l'utilisateur correspondant à l'id reçu en paramètre.
    *
    * @param clientId l'id de l'utilisateur dont on souhaite récupérer toutes les offres utilisées.
    *
    * @return toutes les offres utilisées de l'utilisateur correspondant à l'id reçu en paramètre.
    */
  def findAllConsumedOffersOfUser(clientId: Long): Future[Seq[OfferWithObjects]] = {
    val query = for {
      (offer, beer) <- offers joinLeft beers on (_.beerId === _.id) if offer.beerId.nonEmpty // si beerId est non null -> l'offre a déjà été utilisée
      user <- offer.user if user.id === clientId && user.userType === UserTypeEnum.CLIENT
      company <- offer.company
    } yield (company, user, beer)

    db.run(query.result).map(_.map(offer => OfferWithObjects(offer._1, offer._2, offer._3)))
  }

  /**
    * Crée une nouvelle offre dans la company correspondant à l'id reçu en paramètre pour tous les clients enregistrés dans la base de données.
    *
    * @param companyId , l'id de la company pour laquelle on souhaite créer une nouvelle offre pour tous les clients de la base de données.
    *
    * @return toutes les nouvelles offre créées.
    */
  def createOffersForNewCompany(companyId: Long): Future[Seq[Offer]] = {
    val query = for {
      user <- users if user.userType === UserTypeEnum.CLIENT
    } yield user

    val offers = query.result.map(users => Future.sequence(users.map(user => insert(Offer(companyId, user.id.get)))))
    db.run(offers).flatten // permet de passer d'une Future[Future[...]] à une Future[...]
  }

  /**
    * Crée une offre pour l'utilisateur correspondant à l'id reçu en paramètre pour chaque company de la base de données.
    *
    * @param clientId , l'id du client pour lequel on souhaite créer une nouvelle offre dans chaque company de la base de données.
    *
    * @return les nouvelles offres créées.
    */
  def createAllOffersForNewUser(clientId: Long): Future[Seq[Offer]] = {
    val query = for {
      company <- companies
    } yield company

    val offers = query.result.map(companies => Future.sequence(companies.map(comp => insert(Offer(comp.id.get, clientId)))))
    db.run(offers).flatten // permet de passer d'une Future[Future[...]] à une Future[...]
  }

  /**
    * Enregistre une nouvelle offre dans la base de données.
    *
    * @param offer , l'offre à enregistrer dans la base de données.
    *
    * @return la nouvelle offre enregistrée.
    */
  def insert(offer: Offer): Future[Offer] = db.run(offers returning offers.map(_.id) into ((offer, id) => offer.copy(id = id)) += OfferWithIDToOffer.fromOffer(offer))
    .map(o => Offer(o.companyId, o.clientId, o.beerId))

  /**
    * Met à jour l'offre reçue en paramètre.
    *
    * @param offer , l'offre à mettre à jour.
    *
    * @return l'offre mise à jour.
    */
  def update(offer: Offer): Future[Option[Offer]] = {
    db.run(offers.filter(e => e.companyId === offer.companyId && e.clientId === offer.clientId && e.beerId.isEmpty).map(_.beerId).update(offer.beerId)).map {
      case 0 => None
      case _ => Some(offer)
    }
  }

  /**
    * Supprime l'offre correspondant aux primaryKeys (companyId, clientId) reçues en paramètres.
    *
    * @param companyId
    * @param clientId
    *
    * @return 1 si l'offre a été supprimée avec succès, 0 s'il n'y a pas d'offre correspondant à ce couple de primaryKeys dans la base de données.
    */
  def delete(companyId: Long, clientId: Long): Future[Int] = db.run(offers.filter(e => e.companyId === companyId && e.clientId === clientId).delete)

  /**
    * Retourne les informations de la company la plus fréquentée par les clients de notre BeerPass, ainsi que son nombre de clients.
    *
    * @return les informations de la company la plus fréquentée par les clients de notre BeerPass, ainsi que son nombre de clients.
    */
  def getMostPopularCompany: Future[(Long, Int)] = {
    val query = (for {
      offer <- offers if offer.beerId.nonEmpty
      company <- offer.company
    } yield (offer, company)).groupBy(_._2.id).map {
      case (companyId, offersCompany) => (companyId, offersCompany.length)
    }.sortBy(_._2.desc).take(1)
    db.run(query.result.head)
  }

  /**
    * Retourne les informations de la bière la plus appréciées des clients de notre BeerPass, ainsi que le nombre de fois qu'elle a été commandée par les clients.
    *
    * @return les informations de la bière la plus appréciées des clients de notre BeerPass, ainsi que le nombre de fois qu'elle a été commandée par les clients.
    */
  def getMostFamousBeer: Future[(Long, Int)] = {
    val query = (for {
      offer <- offers if offer.beerId.nonEmpty
      beer <- offer.beer
    } yield (offer, beer)).groupBy(_._2.id).map {
      case (beerId, offersCompany) => (beerId, offersCompany.length)
    }.sortBy(_._2.desc).take(1)
    db.run(query.result.head)
  }

  /**
    * Retourne les informations de la bière la plus appréciées des clients de notre BeerPass dans la company correspondant à l'id reçu en paramètre, ainsi que le nombre de fois qu'elle a été commandée par les clients.
    *
    * @return les informations de la bière la plus appréciées des clients de notre BeerPass dans la company correspondant à l'id reçu en paramètre, ainsi que le nombre de fois qu'elle a été commandée par les clients.
    */
  def getMostFamousBeerForCompany(companyId: Long): Future[(Long, Int)] = {
    val query = (for {
      offer <- offers if offer.beerId.nonEmpty && offer.companyId === companyId
      beer <- offer.beer
    } yield (offer, beer)).groupBy(_._2.id).map {
      case (beerId, offersCompany) => (beerId, offersCompany.length)
    }.sortBy(_._2.desc).take(1)
    db.run(query.result.head)
  }
}
