package dao

import javax.inject.{Inject, Singleton}
import models.{Company, CompanyWithObjects}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait CompanyComponent extends AddressComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's company table in a object-oriented entity: the Company model.
  class CompanyTable(tag: Tag) extends Table[Company](tag, "COMPANY") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def name = column[String]("NAME")

    def description = column[Option[String]]("DESCRIPTION")

    def addressId = column[Long]("ADDRESS_ID")

    def image = column[Option[String]]("IMAGE")

    def address = foreignKey("ADDRESS", addressId, addresses)(_.id)

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

  /**
    * Retourne la company correspondante à l'id reçu en paramètre.
    *
    * @param id , l'id de la company à retourner.
    *
    * @return la company correspondante à l'id reçu en paramètre.
    */
  def findById(id: Long): Future[Option[CompanyWithObjects]] = {
    val query = for {
      company <- companies if company.id === id
      address <- company.address
    } yield (company, address)

    db.run(query.result.headOption)
      .map(option => option
        .map(tuple => CompanyWithObjects
          .fromCompany(tuple._1, tuple._2, Some(Await.result(scheduleDAO.findAllDailySchedulesFromCompanyId(tuple._1.id.get), Duration.Inf)))))
  }

  /**
    * Retourne toutes les company enregistrées dans la base de données.
    *
    * @return toutes les company enregistrées dans la base de données.
    */
  def find: Future[Seq[CompanyWithObjects]] = {
    val query = for {
      company <- companies
      address <- company.address
    } yield (company, address)

    db.run(query.result)
      .map(sec => sec
        .map(tuple => CompanyWithObjects
          .fromCompany(tuple._1, tuple._2, Some(Await.result(scheduleDAO.findAllDailySchedulesFromCompanyId(tuple._1.id.get), Duration.Inf)))))
  }

  /**
    * Ajoute la nouvelle company reçue en paramètre à la base de données.
    *
    * @param company , la company à ajouter à la base de données.
    *
    * @return la nouvelle company.
    */
  def insert(company: Company): Future[Company] = db.run(companies returning companies.map(_.id) into ((company, id) => company.copy(Some(id))) += company)

  /**
    * Met à jour la company reçue en paramètre.
    *
    * @param company , la company à mettre à jour.
    *
    * @return la company mise à jour.
    */
  def update(company: Company): Future[Option[Company]] = db.run(companies.filter(_.id === company.id).update(company.copy(company.id)).map {
    case 0 => None
    case _ => Some(company)
  })

  /**
    * Supprime la company correspondante à l'id reçu en paramètre.
    *
    * @param id l'id de la company à supprimer.
    *
    * @return 1 si la company a été supprimée avec succès, 0 s'il n'y a pas de company avec cet id dans la base de données.
    */
  def delete(id: Long): Future[Int] = db.run(companies.filter(_.id === id).delete)
}
