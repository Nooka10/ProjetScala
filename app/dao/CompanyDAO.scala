package dao

import javax.inject.{Inject, Singleton}
import models.Company
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait CompanyComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // val CompanyTable: CompanyTable

  // This class convert the database's companys table in a object-oriented entity: the Company model.
  class CompanyTable(tag: Tag) extends Table[Company](tag, "COMPANY") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def name = column[String]("NAME")

    def description = column[Option[String]]("DESCRIPTION")

    def scheduleId = column[Option[Long]]("SCHEDULE_ID")

    def addressId = column[Long]("ADDRESS_ID")

    def image = column[Option[String]]("IMAGE")

    // lazy val addresses = TableQuery[AddressTable]

    // def address = foreignKey("ADDRESS", addressId, addresses)(x => x.id)

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, name, description, scheduleId, addressId, image) <> (Company.tupled, Company.unapply)
  }

}

// This class contains the object-oriented list of company and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the company's query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class CompanyDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends CompanyComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  // Get the object-oriented list of courses directly from the query table.
  val companys = TableQuery[CompanyTable]

  /** Retrieve a company from the id. */
  def findById(id: Long): Future[Option[Company]] = db.run(companys.filter(_.id === id).result.headOption)

  /** Insert a new course, then return it. */
  def insert(company: Company): Future[Company] = db.run(companys returning companys.map(_.id) into ((company, id) => company.copy(Some(id))) += company)

  /** Update a company, then return an integer that indicates if the company was found (1) or not (0). */
  def update(id: Long, company: Company): Future[Int] = db.run(companys.filter(_.id === id).update(company.copy(Some(id))))

  /** Delete a company, then return an integer that indicates if the company was found (1) or not (0) */
  def delete(id: Long): Future[Int] = db.run(companys.filter(_.id === id).delete)

  // TODO: ajouter une méthode pour modifier l'horaire, l'adresse, les bières et l'image
}
