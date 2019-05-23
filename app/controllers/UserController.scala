package controllers

import com.github.t3hnar.bcrypt._
import dao.UserDAO
import javax.inject.{Inject, Singleton}
import models.User
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

@Singleton
class UserController @Inject()(cc: ControllerComponents, usersDAO: UserDAO, tokenController: TokenController) extends AbstractController(cc) {

  import models.UserLogin

  implicit val userToJson: Writes[User] = (
    (JsPath \ "id").write[Option[Long]] and
      (JsPath \ "firstname").write[String] and
      (JsPath \ "lastname").write[String] and
      (JsPath \ "email").write[String] and
      (JsPath \ "password").write[String] and
      (JsPath \ "userType").write[String] and // FIXME: comment checker que la valeur contient CLIENT ou EMPLOYEE mais rien d'autre?
      (JsPath \ "companyId").writeNullable[Long]
    ) (unlift(User.unapply))

  implicit val jsonToUser: Reads[User] = (
    (JsPath \ "id").readNullable[Long] and
      (JsPath \ "firstname").read[String](minLength[String](3) keepAnd maxLength[String](30)) and
      (JsPath \ "lastname").read[String](minLength[String](3) keepAnd maxLength[String](30)) and
      (JsPath \ "email").read[String](minLength[String](3) keepAnd maxLength[String](30)) and
      (JsPath \ "password").read[String](minLength[String](6)) and
      (JsPath \ "userType").read[String] and // FIXME: comment checker que la valeur contient CLIENT ou EMPLOYEE mais rien d'autre?
      (JsPath \ "companyId").readNullable[Long]
    ) (User.apply _)

  implicit val jsonToLogin: Reads[UserLogin] = (
    (JsPath \ "email").read[String](minLength[String](3) keepAnd maxLength[String](30)) and
      (JsPath \ "password").read[String](minLength[String](6))
    ) (UserLogin.apply _)

  def validateJson[A: Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def createUser = Action.async(validateJson[User]) { request =>
    import scala.concurrent.Await
    import scala.concurrent.duration.Duration
    if (Await.ready(usersDAO.isEmailAvailable(request.body.email), Duration.Inf).value.get.get) {
      Future(Conflict(
        Json.obj(
          "status" -> "Conflict",
          "message" -> ("The email '" + request.body.email + "' is already used.")
        )
      ))
    } else {
      val password: String = request.body.password.bcrypt(10)
      // FIXME: Est-il possible de réassigner juste une valeur de la request? (modifier juste le champ password sans devoir recréer tout un User...)
      val user: User = User(null, request.body.firstname, request.body.lastname, request.body.email, password, request.body.userType, request.body.companyId)

      val createdUser: Future[User] = usersDAO.insert(user)

      createdUser.map(newUser => {
        Ok(
          Json.obj(
            "status" -> "OK",
            "id" -> newUser.id,
            "message" -> ("User '" + newUser.firstname + " " + newUser.lastname + "' saved."),
            "user infos" -> Json.toJson(newUser),
            "token" -> tokenController.createConnectionToken(newUser)
          )
        )
      })
    }
  }

  def login = Action.async(validateJson[UserLogin]) { request =>
    val user: UserLogin = request.body
    val userInDBFuture: Option[User] = Await.result(usersDAO.findByEmail(user.email), Duration.Inf)
    if (userInDBFuture.isEmpty) {
      Future(
        NotFound(Json.obj(
          "status" -> "Not Found",
          "message" -> ("User with email '" + user.email + "' not found.")
        )))
    } else {
      val userInDB: User = userInDBFuture.get

      if (user.password.isBcryptedSafe(userInDB.password).get) { // passwords match
        Future(
          Ok(
            Json.obj(
              "status" -> "OK",
              "id" -> userInDB.id,
              "message" -> "You are now logged in.",
              "user infos" -> Json.toJson(userInDB),
              "token" -> tokenController.createConnectionToken(userInDB)
            )))
      } else { // passwords doesn't match
        Future(
          Unauthorized(Json.obj(
            "status" -> "Unauthorized",
            "message" -> "The received password is not correct."
          )))
      }
    }
  }

  def getUser(userId: Long) = Action.async {
    val optionalUser: Future[Option[User]] = usersDAO.findById(userId)

    optionalUser.map {
      case Some(c) => Ok(Json.toJson(c)) // FIXME: comment faire pour ne pas récupérer le password? (Tout récupérer sauf le mdp)
      case None =>
        NotFound(Json.obj(
          "status" -> "Not Found",
          "message" -> ("User #" + userId + " not found.")
        ))
    }
  }

  def updateUser(userId: Long) = Action.async(validateJson[User]) { request =>
    val userInDBFuture: Option[User] = Await.result(usersDAO.findById(userId), Duration.Inf)
    if (userInDBFuture.isEmpty) {
      Future(
        NotFound(Json.obj(
          "status" -> "Not Found",
          "message" -> ("User #" + userId + " not found.")
        )))
    } else {
      val userInDB: User = userInDBFuture.get
      val userToUpdate: User = request.body
      if (userToUpdate.password.isBcryptedSafe(userInDB.password).get) { // passwords match
        usersDAO.update(userId, userToUpdate).map {
          case 1 => Ok(
            Json.obj(
              "status" -> "OK",
              "message" -> ("User '" + userToUpdate.firstname + " " + userToUpdate.lastname + "' updated."),
              "user info updated" -> Json.toJson(userToUpdate)
            )
          )
          case 0 => NotFound(Json.obj(
            "status" -> "Not Found",
            "message" -> ("User #" + userId + " not found.")
          ))
        }
      } else { // passwords doesn't match
        Future(
          Unauthorized(Json.obj(
            "status" -> "Unauthorized",
            "message" -> "The received password is not correct."
          )))
      }
    }
  }

  def deleteUser(userId: Long) = Action.async {
    usersDAO.delete(userId).map {
      case 1 => Ok(
        Json.obj(
          "status" -> "OK",
          "message" -> ("User #" + userId + " deleted.")
        )
      )
      case 0 => NotFound(Json.obj(
        "status" -> "Not Found",
        "message" -> ("User #" + userId + " not found.")
      ))
    }
  }


}
