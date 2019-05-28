package dao

import javax.inject.{Inject, Singleton}
import models.{Company, CompanyWithObjects}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait CompanyComponent extends AddressComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's companys table in a object-oriented entity: the Company model.
  class CompanyTable(tag: Tag) extends Table[Company](tag, "COMPANY") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def name = column[String]("NAME")

    def description = column[Option[String]]("DESCRIPTION")

    def addressId = column[Long]("ADDRESS_ID")

    def image = column[Option[String]]("IMAGE")

    def address = foreignKey("ADDRESS", addressId, addresses)(x => x.id)

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, name, description, addressId, image) <> (Company.tupled, Company.unapply)
  }

  // Get the object-oriented list of courses directly from the query table.
  lazy val companies = TableQuery[CompanyTable]
}

// This class contains the object-oriented list of company and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the company's query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class CompanyDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, scheduleDAO: ScheduleDAO)(implicit executionContext: ExecutionContext)
  extends CompanyComponent with AddressComponent with ScheduleComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._
  import scala.collection.immutable

  /** Retrieve a company from the id. */
  def findById(id: Long): Future[Option[CompanyWithObjects]] = {
    val query = for {
      company <- companies if company.id === id
      address <- company.address
      linkSC <- linkScheduleCompany if linkSC.companyId === company.id
      schedule <- linkSC.schedule
    } yield (company, address, schedule)

    db.run(query.result).map(e => e.groupBy(x => (x._1, x._2)).map {
      case ((comp, addr), seq) => CompanyWithObjects.fromCompany(comp, addr, Some(seq.map(_._3)))
    }.headOption)
  }

  def find: Future[immutable.Iterable[CompanyWithObjects]] = {
    val query = for {
      company <- companies
      address <- company.address
      linkSC <- linkScheduleCompany if linkSC.companyId === company.id
      schedule <- linkSC.schedule
    } yield (company, address, schedule)

    db.run(query.result).map(e => e.groupBy(x => (x._1, x._2)).map {
      case ((comp, addr), seq) => CompanyWithObjects.fromCompany(comp, addr, Some(seq.map(_._3)))
    })
  }

  /** Insert a new course, then return it. */
  def insert(company: Company): Future[Company] = db.run(companies returning companies.map(_.id) into ((company, id) => company.copy(Some(id))) += company)

  /** Update a company, then return an integer that indicates if the company was found (1) or not (0). */
  def update(id: Long, company: Company): Future[Option[Company]] = db.run(companies.filter(_.id === id).update(company.copy(Some(id))).map {
    case 0 => None
    case _ => Some(company)
  })

  /** Delete a company, then return an integer that indicates if the company was found (1) or not (0) */
  def delete(id: Long): Future[Int] = db.run(companies.filter(_.id === id).delete)
}
