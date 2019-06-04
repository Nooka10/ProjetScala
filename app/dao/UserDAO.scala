package dao

import javax.inject.{Inject, Singleton}
import models.{User, UserTypeEnum}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

trait UserComponent extends CompanyComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

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

    def email = column[String]("EMAIL")

    def password = column[String]("PASSWORD")

    def userType = column[UserTypeEnum.Value]("USER_TYPE") // CLIENT or EMPLOYEE

    def companyId = column[Option[Long]]("COMPANY_ID")

    def company = foreignKey("COMPANY", companyId, companies)(_.id)

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

  /**
    * Retourne l'utilisateur correspondant à l'id reçu en paramètre.
    *
    * @param id , l'id de l'utilisateur à retourner.
    *
    * @return l'utilisateur correspondant à l'id reçu en paramètre.
    */
  def findById(id: Long): Future[Option[User]] = db.run(users.filter(_.id === id).result.headOption)

  /**
    * Retourne l'utilisateur correspondant à l'email reçu en paramètre.
    *
    * @param email , l'email de l'utilisateur à retourner.
    *
    * @return l'utilisateur correspondant à l'email reçu en paramètre.
    */
  def findByEmail(email: String): Future[Option[User]] = db.run(users.filter(_.email === email).result.headOption)

  /**
    * Retourne tous les employés de la company correspondante à l'id reçu en paramètre.
    *
    * @param companyId , l'id de la company dont on souhaite récupérer les employés.
    *
    * @return tous les employés de la company correspondante à l'id reçu en paramètre.
    */
  def findAllEmployees(companyId: Long): Future[Seq[User]] = db.run(users.filter(_.companyId === companyId).result)

  /**
    * Retourne true si l'email reçu en paramètre est disponible (pas encore présent dans la base de données), false sinon (déjà utilisé par un utilisateur)
    *
    * @param email , l'email dont on souhaite vérifier l'existance dans la base de données.
    *
    * @return true si l'email reçu en paramètre est disponible (pas encore présent dans la base de données), false sinon (déjà utilisé par un utilisateur)
    */
  def isEmailAvailable(email: String): Future[Boolean] = db.run(users.filter(_.email === email).exists.result)

  /**
    * Enregistre un nouvel utilisateur dans la base de données.
    *
    * @param user , le nouvel utilisateur à enregistrer.
    *
    * @return le nouvel utilisateur enregistré.
    */
  def insert(user: User): Future[User] = db.run(users returning users.map(_.id) into ((user, id) => user.copy(Some(id))) += user)

  /**
    * Met à jour l'utilisateur reçu en paramètre.
    *
    * @param user , l'utilsiateur à mettre à jour.
    *
    * @return l'utilsiateur mis à jour.
    */
  def update(user: User): Future[Option[User]] = db.run(users.filter(_.id === user.id).update(user.copy(user.id)).map {
    case 0 => None
    case _ => Some(user)
  })

  /**
    * Supprime l'utilsiateur correspondant à l'id reçu en paramètre
    *
    * @param id , l'id de l'utilisateur à supprimer.
    *
    * @return 1 si l'utilisateur a été supprimée correctement, 0 s'il n'y a pas d'utilisateur correspondant à cet id dans la base de données.
    */
  def delete(id: Long): Future[Int] = db.run(users.filter(_.id === id).delete)
}
