package dao

import javax.inject.{Inject, Singleton}
import models.Beer
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait BeerComponent extends CompanyComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import models.Link_Company_Beer
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

  // This class convert the database's beers table in a object-oriented entity: the Beer model.
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

  import profile.api._

  /** Retrieve all beers. */
  def find: Future[Seq[Beer]] = db.run(beers.result)

  /** Retrieve a beer from the id. */
  def findById(id: Long): Future[Option[Beer]] = db.run(beers.filter(_.id === id).result.headOption)

  /** Insert a new course, then return it. */
  def insert(beer: Beer): Future[Beer] = db.run(beers returning beers.map(_.id) into ((beer, id) => beer.copy(Some(id))) += beer)

  /** Update a beer, then return an integer that indicates if the beer was found (1) or not (0). */
  def update(beer: Beer): Future[Option[Beer]] = db.run(beers.filter(_.id === beer.id).update(beer.copy(beer.id)).map {
    case 0 => None
    case _ => Some(beer)
  })

  /** Delete a beer, then return an integer that indicates if the beer was found (1) or not (0) */
  def delete(id: Long): Future[Int] = db.run(beers.filter(_.id === id).delete)

  def getAllBeersOfCompany(companyId: Long) = {
    val query = for {
      linkCB <- linkCompanyBeer if linkCB.companyId === companyId
      beer <- linkCB.beer
    } yield beer

    db.run(query.result)
  }

  def getOneBeerOfCompany(companyId: Long, beerId: Long) = {
    val query = for {
      linkCB <- linkCompanyBeer if linkCB.companyId === companyId
      beer <- linkCB.beer if beer.id === beerId
    } yield beer

    db.run(query.result.headOption)
  }

  def addBeerToDrinkListOfCompany(companyId: Long, beerId: Long) = {
    import models.Link_Company_Beer
    // FIXME: C'est correct de faire ça??
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

  def removeBeerFromDrinkListOfCompany(companyId: Long, beerId: Long): Future[Int] = {
    db.run(linkCompanyBeer.filter(row => row.companyId === companyId && row.beerId === beerId).delete)
  }
}
