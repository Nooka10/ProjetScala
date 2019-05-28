package dao

import javax.inject.{Inject, Singleton}
import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait UserComponent extends CompanyComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import models.UserTypeEnum
  import profile.api._

  implicit lazy val userTypeMapper = MappedColumnType.base[UserTypeEnum.Value, String](
    e => e.toString,
    s => UserTypeEnum.withName(s)
  )

  // This class convert the database's users table in a object-oriented entity: the User model.
  class UserTable(tag: Tag) extends Table[User](tag, "USER") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def firstname = column[String]("FIRSTNAME")

    def lastname = column[String]("LASTNAME")

    def email = column[String]("EMAIL", O.Unique)

    def password = column[String]("PASSWORD")

    def userType = column[UserTypeEnum.Value]("USER_TYPE") // CLIENT or EMPLOYEE

    def companyId = column[Option[Long]]("COMPANY_ID")

    def company = foreignKey("COMPANY", companyId, companies)(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, firstname, lastname, email, password, userType, companyId) <> (User.tupled, User.unapply)
  }
  
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

  /** Retrieve a user from the email. */
  def findByEmail(email: String): Future[Option[User]] = db.run(users.filter(_.email === email).result.headOption)

  def findAllEmployees(companyId: Long): Future[Seq[User]] = db.run(users.filter(_.companyId === companyId).result)

  /** Retrieve a user from the id. */
  def isEmailAvailable(email: String): Future[Boolean] = db.run(users.filter(_.email === email).exists.result)

  /** Insert a new user, then return it. */
  def insert(user: User): Future[User] = db.run(users returning users.map(_.id) into ((user, id) => user.copy(Some(id))) += user)

  /** Update a user, then return an integer that indicates if the user was found (1) or not (0). */
  def update(id: Long, user: User): Future[Option[User]] = db.run(users.filter(_.id === id).update(user.copy(Some(id))).map {
    case 0 => None
    case _ => Some(user)
  })

  /** Delete a user, then return an integer that indicates if the user was found (1) or not (0) */
  def delete(id: Long): Future[Int] = db.run(users.filter(_.id === id).delete)

  // TODO: ajouter une méthode pour reset les offres + un autre pour indiquer qu'une offre a été consommée
}
