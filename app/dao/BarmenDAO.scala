package dao

import javax.inject.{Inject, Singleton}
import models.Barmen
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait BarmenComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // val BarmenTable: BarmenTable

  // This class convert the database's barmens table in a object-oriented entity: the Barmen model.
  class BarmenTable(tag: Tag) extends Table[Barmen](tag, "BARMEN") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def firstname = column[String]("FIRSTNAME")

    def lastname = column[String]("LASTNAME")

    def email = column[String]("EMAIL", O.Unique)

    def password = column[String]("PASSWORD")

    def companyId = column[Long]("COMPANY_ID")

    // val companys = TableQuery[CompanyTable]

    // def companyFk = foreignKey("COMPANY_ID_FK", companyId, companys)(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, firstname, lastname, email, password, companyId) <> (Barmen.tupled, Barmen.unapply)
  }

}

// This class contains the object-oriented list of barmen and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the barmen's query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class BarmenDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends BarmenComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  // Get the object-oriented list of courses directly from the query table.
  val barmens = TableQuery[BarmenTable]

  /** Retrieve a barmen from the id. */
  def findById(id: Long): Future[Option[Barmen]] = db.run(barmens.filter(_.id === id).result.headOption)

  /** Insert a new course, then return it. */
  def insert(barmen: Barmen): Future[Barmen] = db.run(barmens returning barmens.map(_.id) into ((barmen, id) => barmen.copy(Some(id))) += barmen)

  /** Update a barmen, then return an integer that indicates if the barmen was found (1) or not (0). */
  def update(id: Long, barmen: Barmen): Future[Int] = db.run(barmens.filter(_.id === id).update(barmen.copy(Some(id))))

  /** Delete a barmen, then return an integer that indicates if the barmen was found (1) or not (0) */
  def delete(id: Long): Future[Int] = db.run(barmens.filter(_.id === id).delete)

  // TODO: ajouter une méthode pour scanner l'offre d'un client
}
