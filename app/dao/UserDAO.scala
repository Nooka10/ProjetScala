package dao

import javax.inject.{Inject, Singleton}
import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait UserComponent extends CompanyComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>
  import profile.api._

  // This class convert the database's users table in a object-oriented entity: the User model.
  class UserTable(tag: Tag) extends Table[User](tag, "USER") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def firstname = column[String]("FIRSTNAME")

    def lastname = column[String]("LASTNAME")

    def email = column[String]("EMAIL", O.Unique)

    def password = column[String]("PASSWORD")

    // FIXME: comment faire un mapper pour pouvoir utiliser l'enum directement dans la DB MySQL ?
    def userType = column[String]("USERTYPE") // CLIENT or EMPLOYEE

    def companyId = column[Option[Long]]("COMPANYID")

    def company = foreignKey("COMPANY", companyId, companies)(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, firstname, lastname, email, password, userType, companyId) <> (User.tupled, User.unapply)
  }

  /*
  // FIXME: comment faire une enum en scala ?! ^^"
  object UserType extends Enumeration {
    type userType = Value
    val CLIENT = Value("CLIENT")
    val EMPLOYEE = Value("EMPLOYEE")
  }

  // FIXME: comment faire un mapper pour pouvoir utiliser l'enum directement dans la DB MySQL ?
  implicit val javaEnumMapper = MappedColumnType.base[UserType, String](
    e => e.toString,
    s => UserType.withName(s)
  )
  */
  
  // Get the object-oriented list of courses directly from the query table.
  lazy val users = TableQuery[UserTable]
}

// This class contains the object-oriented list of user and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the user's query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class UserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends UserComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  /** Retrieve a user from the id. */
  def findById(id: Long): Future[Option[User]] = db.run(users.filter(_.id === id).result.headOption)

  /** Retrieve a user from the id. */
  def isEmailAvailable(email: String): Future[Boolean] = db.run(users.filter(_.email === email).exists.result)

  /** Insert a new course, then return it. */
  def insert(user: User): Future[User] = db.run(users returning users.map(_.id) into ((user, id) => user.copy(Some(id))) += user)

  /** Update a user, then return an integer that indicates if the user was found (1) or not (0). */
  def update(id: Long, user: User): Future[Int] = db.run(users.filter(_.id === id).update(user.copy(Some(id)))) // FIXME: C'est possible de retourner l'utilisateur ou null plutôt qu'un 1 ou un 0 ?

  /** Delete a user, then return an integer that indicates if the user was found (1) or not (0) */
  def delete(id: Long): Future[Int] = db.run(users.filter(_.id === id).delete)

  // TODO: ajouter une méthode pour reset les offres + un autre pour indiquer qu'une offre a été consommée
}
