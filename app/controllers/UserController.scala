package controllers

import com.github.t3hnar.bcrypt._
import dao.{OfferDAO, UserDAO}
import javax.inject.{Inject, Singleton}
import models.{User, UserLogin}
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import Utils.ImplicitsJson._

@Singleton
class UserController @Inject()(cc: ControllerComponents, usersDAO: UserDAO, offersDAO: OfferDAO, tokenController: TokenController) extends AbstractController(cc) {

  def validateJson[A: Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def createUser = Action.async(validateJson[User]) { request =>
    usersDAO.isEmailAvailable(request.body.email).map {
      case true => Conflict(
        Json.obj(
          "status" -> "Conflict",
          "message" -> ("The email '" + request.body.email + "' is already used.")
        ))
      case false =>
        val password: String = request.body.password.bcrypt(10)
        val user: User = request.body.copy(password = password)

        Await.result(usersDAO.insert(user).map(newUser => Ok(
          Json.obj(
            "status" -> "OK",
            "id" -> newUser.id,
            "message" -> ("User '" + newUser.firstname + " " + newUser.lastname + "' saved."),
            "user infos" -> newUser,
            "token" -> tokenController.createConnectionToken(newUser)
          )
        )), Duration.Inf)
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
              "user infos" -> userInDB,
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
      case Some(c) => Ok(Json.toJson(c.copy(password = null))) // on supprime le password pour ne pas le retourner
      case None =>
        NotFound(Json.obj(
          "status" -> "Not Found",
          "message" -> ("User #" + userId + " not found.")
        ))
    }
  }

  def updateUser(userId: Long) = Action.async(validateJson[User]) { request =>
    usersDAO.findById(userId).map {
      case None => NotFound(Json.obj(
          "status" -> "Not Found",
          "message" -> ("User #" + userId + " not found.")
      ))
      case Some(userInDB) =>
        val userToUpdate: User = request.body

        userToUpdate.password.isBcryptedSafe(userInDB.password).map {
          case true => Await.result(usersDAO.update(userId, userToUpdate).map {
            case Some(user) => Ok(
              Json.obj(
                "status" -> "OK",
                "message" -> ("User '" + user.firstname + " " + user.lastname + "' updated."),
                "user info updated" -> user
              )
            )
            case None => NotFound(Json.obj(
              "status" -> "Not Found",
              "message" -> ("User #" + userId + " not found.")
            ))
          }, Duration.Inf)
          case false => Unauthorized(Json.obj(
            "status" -> "Unauthorized",
            "message" -> "The received password is not correct."
          ))
        }.getOrElse(InternalServerError("An error occured"))
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


  def getUserOffers(userId: Long) = Action.async {
    offersDAO.findAllOffersOfUser(userId).map(employee => Ok(Json.toJson(employee)))
  }

  /*
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

// TODO: Comment dockeriser un projet scala ?
