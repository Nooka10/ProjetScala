package dao

import javax.inject.{Inject, Singleton}
import models.{Beer, Link_Company_Beer}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait BeerComponent extends CompanyComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's beer table in a object-oriented entity: the Beer model.
  class BeerTable(tag: Tag) extends Table[Beer](tag, "BEER") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def name = column[String]("NAME")

    def brand = column[String]("BRAND")

    def degreeAlcohol = column[Option[Double]]("DEGREE_ALCOHOL")

    def image = column[Option[String]]("IMAGE")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, name, brand, degreeAlcohol, image) <> (Beer.tupled, Beer.unapply)
  }

  // This class convert the database's LinkCompanyBeer table in a object-oriented entity: the LinkCompanyBeer model.
  class LinkCompanyBeerTable(tag: Tag) extends Table[Link_Company_Beer](tag, "LINK_COMPANY_BEER") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def companyId = column[Long]("COMPANY_ID")

    def beerId = column[Long]("BEER_ID")

    def company = foreignKey("COMPANY", companyId, companies)(_.id, onDelete = ForeignKeyAction.Cascade)

    def beer = foreignKey("BEER", beerId, beers)(_.id, onDelete = ForeignKeyAction.Cascade)

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, companyId, beerId) <> (Link_Company_Beer.tupled, Link_Company_Beer.unapply)
  }

  // Get the object-oriented list of courses directly from the query table.
  lazy val beers = TableQuery[BeerTable]
  lazy val linkCompanyBeer = TableQuery[LinkCompanyBeerTable]
}

// This class contains the object-oriented list of beer and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the beer's query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class BeerDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends BeerComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import models.Company
  import profile.api._

  /**
    * Retourne toutes les bières présentes dans la BDD.
    *
    * @return toutes les bières présentes dans la BDD.
    */
  def find: Future[Seq[Beer]] = db.run(beers.result)

  /**
    * Retourne la bière correspondant à l'id reçu.
    *
    * @param id , l'id de la bière à retourner.
    *
    * @return la bière correspondant à l'id reçu.
    */
  def findById(id: Long): Future[Option[Beer]] = db.run(beers.filter(_.id === id).result.headOption)

  /**
    * Enregistre la nouvelle bière reçue en paramètre.
    *
    * @param beer , la nouvelle bière à enregistrer.
    *
    * @return la nouvelle bière enregistrée.
    */
  def insert(beer: Beer): Future[Beer] = db.run(beers returning beers.map(_.id) into ((beer, id) => beer.copy(Some(id))) += beer)

  /**
    * Met à jour la bière reçue en paramètre.
    *
    * @param beer , les informations de la bière à mettre à jour.
    *
    * @return la bière mise à jour.
    */
  def update(beer: Beer): Future[Option[Beer]] = db.run(beers.filter(_.id === beer.id).update(beer.copy(beer.id)).map {
    case 0 => None
    case _ => Some(beer)
  })

  /**
    * Supprime la bière correspondant à l'id reçu en paramètre.
    *
    * @param id , l'id de la bière à supprimer.
    *
    * @return 1 si la bière a bien été supprimée, 0 si l'id ne correspond à aucune bière dans la BDD.
    */
  def delete(id: Long): Future[Int] = db.run(beers.filter(_.id === id).delete)

  /**
    * Retourne toutes les bières proposées par la company correspondante au companyId reçu en paramètre.
    *
    * @param companyId , l'id de la company dont on souhaite récupérer la liste de boissons.
    *
    * @return toutes les bières proposées par la company correspondante au companyId reçu en paramètre.
    */
  def getAllBeersOfCompany(companyId: Long): Future[Seq[Beer]] = {
    val query = for {
      linkCB <- linkCompanyBeer if linkCB.companyId === companyId
      beer <- linkCB.beer
    } yield beer

    db.run(query.result)
  }

  /**
    * Retourne la bières proposée par la companie correspondant au companyId reçu en paramètre et correspondant au beerId reçu.
    *
    * @param companyId , l'id de la company dont on souhaite récupérer une bière de la liste de boissons proposées.
    * @param beerId    , l'id de la bière que l'on souhaite récupérer.
    *
    * @return la bières proposée par la companie correspondant au companyId reçu en paramètre et correspondant au beerId reçu.
    */
  def getOneBeerOfCompany(companyId: Long, beerId: Long): Future[Option[Beer]] = {
    val query = for {
      linkCB <- linkCompanyBeer if linkCB.companyId === companyId
      beer <- linkCB.beer if beer.id === beerId
    } yield beer

    db.run(query.result.headOption)
  }

  /**
    * Ajoute la bière avec l'id beerId à la liste des boissons proposées par la company avec l'id companyId.
    *
    * @param companyId , l'id de la company pour laquelle on souhaite ajouter une boisson à la liste de boissons proposées.
    * @param beerId    , l'id de la bière que l'on souhaite ajouter à la liste de boissons proposées par la company.
    *
    * @return un tuple (company, beer) contenant les informations de la company à laquelle on a ajouté une boisson, ainsi que celles de la bière ajoutée.
    */
  def addBeerToDrinkListOfCompany(companyId: Long, beerId: Long): Future[(Company, Beer)] = {
    val query = for {
      beer <- beers if beer.id === beerId
      company <- companies if company.id === companyId
    } yield (company, beer)

    db.run(query.result.headOption.map{
      case Some((company, beer)) =>
        db.run(linkCompanyBeer returning linkCompanyBeer.map(_.id) into ((LinkCompanyBeer, id) => LinkCompanyBeer.copy(Some(id))) += Link_Company_Beer(null, company.id.get, beer.id.get))
        (company, beer)
    })
  }

  /**
    * Supprime la bière avec l'id beerId de la liste des boissons proposées par la company avec l'id companyId.
    *
    * @param companyId , l'id de la company pour laquelle on souhaite supprimer une boisson de la liste de boissons proposées.
    * @param beerId    , l'id de la bière que l'on souhaite supprimer de la liste de boissons proposées par la company.
    *
    * @return 1 si la bière a bien été supprimée de la liste de boissons de la company correspondant à l'id companyIs, 0 si l'id ne correspond à aucune bière de la liste de boissons de la company.
    */
  def removeBeerFromDrinkListOfCompany(companyId: Long, beerId: Long): Future[Int] = {
    db.run(linkCompanyBeer.filter(row => row.companyId === companyId && row.beerId === beerId).delete)
  }
}
