package controllers

import com.github.t3hnar.bcrypt._
import dao.{OfferDAO, UserDAO}
import javax.inject.{Inject, Singleton}
import models.{User, UserLogin}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

@Singleton
class UserController @Inject()(cc: ControllerComponents, usersDAO: UserDAO, offersDAO: OfferDAO, tokenController: TokenController) extends AbstractController(cc) {

  implicit val userToJson: Writes[User] = (
    (JsPath \ "id").writeNullable[Long] and
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
      (JsPath \ "email").read[String](email) and
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
    if (Await.ready(usersDAO.isEmailAvailable(request.body.email), Duration.Inf).value.get.get) {
      Future(Conflict(
        Json.obj(
          "status" -> "Conflict",
          "message" -> ("The email '" + request.body.email + "' is already used.")
        )
      ))
    } else {
      val password: String = request.body.password.bcrypt(10)
      val user: User = request.body.copy(password = password)

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
          case Some(user) => Ok(
            Json.obj(
              "status" -> "OK",
              "message" -> ("User '" + user.firstname + " " + user.lastname + "' updated."),
              "user info updated" -> Json.toJson(user)
            )
          )
          case None => NotFound(Json.obj(
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

  /*
  def getUserOffers(userId: Long) = {
    usersDAO.findById(userId).map {
      case Some(user) => offersDAO.findAllOffersOfUser(user.id)
      case None => NotFound(Json.obj(
        "status" -> "Not Found",
        "message" -> ("User #" + userId + " not found.")
      ))
    }
  }

  def useOffer(userId: Long, companyId: Long, choosenBeerId: Long) = {
    usersDAO.findById(userId).map {
      case Some(user) => offersDAO.update(user.id, companyId, choosenBeerId)
      case None => NotFound(Json.obj(
        "status" -> "Not Found",
        "message" -> ("User #" + userId + " not found.")
      ))
    }
  }
   */
}
