package controllers

import Utils.ImplicitsJson._
import com.github.t3hnar.bcrypt._
import dao.{OfferDAO, UserDAO}
import javax.inject.{Inject, Singleton}
import models.{User, UserLogin}
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

@Singleton
class UserController @Inject()(cc: ControllerComponents, usersDAO: UserDAO, offersDAO: OfferDAO, tokenController: TokenController) extends AbstractController(cc) {

  import models.Offer

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

        Await.result(usersDAO.insert(user).map(newUser =>
          if(newUser.companyId.isEmpty) { // le nouvel utilisateur est un client
            offersDAO.createAllOffersForNewUser(newUser.id.get).map(_ =>
              Ok(Json.obj(
                "status" -> "OK",
                "id" -> newUser.id,
                "message" -> ("User '" + newUser.firstname + " " + newUser.lastname + "' saved."),
                "userInfos" -> newUser,
                "token" -> tokenController.createConnectionToken(newUser)
              )))
          } else { // le nouvel utilisateur est un employé (il n'a donc pas d'offres)
            Future(Ok(Json.obj(
              "status" -> "OK",
              "id" -> newUser.id,
              "message" -> ("User '" + newUser.firstname + " " + newUser.lastname + "' saved."),
              "userInfos" -> newUser,
              "token" -> tokenController.createConnectionToken(newUser)
            )))
          }
        ).flatten, Duration.Inf)
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
              "userInfos" -> userInDB,
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
      case Some(c) => Ok(Json.toJson(c)) // on supprime le password pour ne pas le retourner
      case None =>
        NotFound(Json.obj(
          "status" -> "Not Found",
          "message" -> ("User #" + userId + " not found.")
        ))
    }
  }

  def updateUser = Action.async(validateJson[User]) { request =>
    if (request.body.id.isEmpty) {
      import scala.concurrent.Future
      Future(BadRequest(Json.obj(
        "status" -> "Bad Request",
        "message" -> "The field 'id' is missing!"
      )))
    }

    val userId: Long = request.body.id.get
    usersDAO.findById(userId).map {
      case None => NotFound(Json.obj(
        "status" -> "Not Found",
        "message" -> ("User #" + userId + " not found.")
      ))
      case Some(userInDB) =>
        val userToUpdate: User = request.body

        userToUpdate.password.isBcryptedSafe(userInDB.password).map {
          case true => Await.result(usersDAO.update(userToUpdate).map {
            case Some(user) => Ok(
              Json.obj(
                "status" -> "OK",
                "message" -> ("User '" + user.firstname + " " + user.lastname + "' updated."),
                "userInfo" -> user
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

  def getAllOffersOfUser(userId: Long) = Action.async {
    offersDAO.findAllOffersOfUser(userId).map(offers => Ok(Json.toJson(offers)))
  }

  def getAllUnusedOffersOfUser(userId: Long) = Action.async {
    offersDAO.findAllUnusedOffersOfUser(userId).map(offers => Ok(Json.toJson(offers)))
  }

  def getAllConsumedOffersOfUser(userId: Long) = Action.async {
    offersDAO.findAllConsumedOffersOfUser(userId).map(offers => Ok(Json.toJson(offers)))
  }

  def useOffer = Action.async(validateJson[Offer]) { request =>
    if (request.body.beerId.isEmpty) {
      Future(BadRequest(Json.obj(
        "status" -> "Bad Request",
        "message" -> "The field 'beerId' is missing!"
      )))
    } else {
      usersDAO.findById(request.body.clientId).map {
        case Some(_) => Await.result(offersDAO.update(request.body).map{
          case None => Unauthorized(Json.obj(
            "status" -> "Not Found or already used",
            "message" -> "This offer has not been found or has already been used by the client."
          ))
          case Some(newOffer) => Ok(Json.toJson(newOffer))
        }, Duration.Inf)
        case None => NotFound(Json.obj(
          "status" -> "Not Found",
          "message" -> ("User #" + request.body.clientId + " not found.")
        ))
      }
    }
  }
}
