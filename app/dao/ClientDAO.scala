package dao

import javax.inject.{Inject, Singleton}
import models.Client
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait ClientComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's clients table in a object-oriented entity: the Client model.
  class ClientTable(tag: Tag) extends Table[Client](tag, "CLIENTS") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def firstname = column[String]("FIRSTNAME")

    def lastname = column[String]("LASTNAME")

    def email = column[String]("EMAIL", O.Unique)

    def password = column[String]("PASSWORD")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, firstname, lastname, email, password) <> (Client.tupled, Client.unapply)
  }

}

// This class contains the object-oriented list of client and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the client's query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class ClientDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends ClientComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  // Get the object-oriented list of courses directly from the query table.
  val clients = TableQuery[ClientTable]

  /** Retrieve a client from the id. */
  def findById(id: Long): Future[Option[Client]] = db.run(clients.filter(_.id === id).result.headOption)

  /** Insert a new course, then return it. */
  def insert(client: Client): Future[Client] = db.run(clients returning clients.map(_.id) into ((client, id) => client.copy(Some(id))) += client)

  /** Update a client, then return an integer that indicates if the client was found (1) or not (0). */
  def update(id: Long, client: Client): Future[Int] = db.run(clients.filter(_.id === id).update(client.copy(Some(id))))

  /** Delete a client, then return an integer that indicates if the client was found (1) or not (0) */
  def delete(id: Long): Future[Int] = db.run(clients.filter(_.id === id).delete)

  // TODO: ajouter une méthode pour reset les offres + un autre pour indiquer qu'une offre a été consommée
}
