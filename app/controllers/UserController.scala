package controllers

import Utils.ImplicitsJson._
import com.github.t3hnar.bcrypt._
import dao.{OfferDAO, UserDAO}
import javax.inject.{Inject, Singleton}
import models.{Offer, User, UserLogin}
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


@Singleton
class UserController @Inject()(cc: ControllerComponents, usersDAO: UserDAO, offersDAO: OfferDAO, tokenController: TokenController) extends AbstractController(cc) {

  def validateJson[A: Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  /**
    * Enregistre le nouvel utilisateur reçu dans le body de la requête si l'email est disponible.
    *
    * @return Conflict() / Ok() / BadRequest()
    */
  def createUser = Action.async(validateJson[User]) { request =>
    // on check si l'email de l'utilisateur à ajouter est disonpible (pas encore utilisé dans la base de données)
    usersDAO.isEmailAvailable(request.body.email).map {
      // l'email est déjà présent dans la base de données
      case true => Conflict(
        Json.obj(
          "status" -> "Conflict",
          "message" -> ("The email '" + request.body.email + "' is already used.")
        ))
      // l'email n'est pas encore présent dans la base de données
      case false =>
        val password: String = request.body.password.bcrypt(10) // on hash le mot de passe pour ne pas l'enregistrer en clair dans la base de données
        val user: User = request.body.copy(password = password)

        // on ajoute le nouvel utilisateur
        Await.result(usersDAO.insert(user).map(newUser =>
          if(newUser.companyId.isEmpty) { // le nouvel utilisateur est un client
            offersDAO.createAllOffersForNewUser(newUser.id.get).map(_ =>
              Ok(Json.obj(
                "status" -> "OK",
                "id" -> newUser.id,
                "message" -> ("User '" + newUser.firstname + " " + newUser.lastname + "' saved."),
                "userInfos" -> newUser.copy(password = null),
                "token" -> tokenController.createConnectionToken(newUser)
              )))
          } else { // le nouvel utilisateur est un employé (il n'a donc pas d'offres)
            Future(Ok(Json.obj(
              "status" -> "OK",
              "id" -> newUser.id,
              "message" -> ("User '" + newUser.firstname + " " + newUser.lastname + "' saved."),
              "userInfos" -> newUser.copy(password = null),
              "token" -> tokenController.createConnectionToken(newUser)
            )))
          }
        ).flatten, Duration.Inf)
    }
  }

  /**
    * Retourne un token de connexion si les informations de connexion reçues dans le body de la requête correspondent à un utilisateur enregistré (email et mdp corrects).
    *
    * @return Unauthorized() / Ok() / NotFound() / BadRequest()
    */
  def login = Action.async(validateJson[UserLogin]) { request =>
    val user: UserLogin = request.body

    // on cherche l'utilisateur dans la base de donnée à l'aide de son email
    usersDAO.findByEmail(user.email).map {
      // aucun utilisateur de la base de données ne correspond à cet email
      case None => NotFound(Json.obj(
          "status" -> "Not Found",
          "message" -> ("User with email '" + user.email + "' not found.")
      ))
      // un utilisateur dans la base de données correspond bien à cet email -> on check que le mot de passe reçu corresponde bien à celui enregistré dans la base de données.
      case Some(userInDB) => user.password.isBcryptedSafe(userInDB.password).map {
        // le mot de passe reçu est correct
        case true => Ok(
            Json.obj(
              "status" -> "OK",
              "id" -> userInDB.id,
              "message" -> "You are now logged in.",
              "userInfos" -> userInDB.copy(password = null),
              "token" -> tokenController.createConnectionToken(userInDB)
            ))
        // le mot de passe reçu n'est pas correct
        case false => Unauthorized(Json.obj(
            "status" -> "Unauthorized",
            "message" -> "The received password is not correct."
        ))
      }.get
    }
  }

  /**
    * Retourne l'utilisateur correspondant à l'id reçu en paramètre.
    *
    * @param userId , l'id de l'utilisateur à retourner.
    *
    * @return Ok() / NotFound()
    */
  def getUser(userId: Long) = Action.async {
    usersDAO.findById(userId).map {
      // un utilisateur correspondant à cet id a été trouvé dans la base de données
      case Some(c) => Ok(Json.toJson(c.copy(password = null)))
      // aucun utilisateur de la base de données ne correspond à cet id
      case None =>
        NotFound(Json.obj(
          "status" -> "Not Found",
          "message" -> ("User #" + userId + " not found.")
        ))
    }
  }

  /**
    * Met à jour les informations de l'utilisateur reçu dans le body de la requête.
    *
    * @return Ok() / NotFound() / Unauthorized() / InternalServerError() / BadRequest()
    */
  def updateUser = Action.async(validateJson[User]) { request =>
    if (request.body.id.isEmpty) {
      Future(BadRequest(Json.obj(
        "status" -> "Bad Request",
        "message" -> "The field 'id' is missing!"
      )))
    }

    val userId: Long = request.body.id.get

    usersDAO.findById(userId).map {
      // aucun utilisateur de la base de données ne correspond à cet id
      case None => NotFound(Json.obj(
        "status" -> "Not Found",
        "message" -> ("User #" + userId + " not found.")
      ))
      // un utilisateur correspondant à cet id a été trouvé dans la base de données
      case Some(userInDB) =>
        val userToUpdate: User = request.body

        // on check que le mot de passe reçu corresponde bien à celui enregistré dans la base de données.
        userToUpdate.password.isBcryptedSafe(userInDB.password).map {
          // le mot de passe reçu est correct -> on met l'utilisateur à jour
          case true => Await.result(usersDAO.update(userToUpdate).map {
            case Some(user) => Ok(
              Json.obj(
                "status" -> "OK",
                "message" -> ("User '" + user.firstname + " " + user.lastname + "' updated."),
                "userInfo" -> user.copy(password = null)
              )
            )
          }, Duration.Inf)
          // le mot de passe reçu n'est pas correct -> on ne met pas l'utilisateur à jour
          case false => Unauthorized(Json.obj(
            "status" -> "Unauthorized",
            "message" -> "The received password is not correct."
          ))
        }.getOrElse(InternalServerError("An error occured"))
    }
  }

  /**
    * Supprime l'utilisateur correspondant à l'id reçu en paramètre.
    *
    * @param userId , l'id de l'utilisateur à supprimer.
    *
    * @return Ok() / NotFound()
    */
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

  /**
    * Retourne toutes les offres de l'utilisateur correspondant à l'id reçu en paramètre.
    *
    * @param userId l'id de l'utilisateur dont on souhaite récupérer toutes les offres.
    *
    * @return Ok()
    */
  def getAllOffersOfUser(userId: Long) = Action.async {
    offersDAO.findAllOffersOfUser(userId).map(offers => Ok(Json.toJson(offers)))
  }

  /**
    * Retourne toutes les offres non utilisées de l'utilisateur correspondant à l'id reçu en paramètre.
    *
    * @param userId l'id de l'utilisateur dont on souhaite récupérer toutes les offres non utilisées.
    *
    * @return Ok()
    */
  def getAllUnusedOffersOfUser(userId: Long) = Action.async {
    offersDAO.findAllUnusedOffersOfUser(userId).map(offers => Ok(Json.toJson(offers)))
  }

  /**
    * Retourne toutes les offres utilisées de l'utilisateur correspondant à l'id reçu en paramètre.
    *
    * @param userId l'id de l'utilisateur dont on souhaite récupérer toutes les offres utilisées.
    *
    * @return Ok()
    */
  def getAllConsumedOffersOfUser(userId: Long) = Action.async {
    offersDAO.findAllConsumedOffersOfUser(userId).map(offers => Ok(Json.toJson(offers)))
  }

  /**
    * Utilise l'offre reçue en body de la requête.
    *
    * @return Ok() / BadRequest() / Unauthorized() / NotFound()
    */
  def useOffer = Action.async(validateJson[Offer]) { request =>
    if (request.body.beerId.isEmpty) {
      Future(BadRequest(Json.obj(
        "status" -> "Bad Request",
        "message" -> "The field 'beerId' is missing!"
      )))
    } else {
      usersDAO.findById(request.body.clientId).map {
        // un utilisateur correspondant à cet id a été trouvé dans la base de données -> on met l'offre à jour
        case Some(_) => Await.result(offersDAO.update(request.body).map{
          // une erreur s'est produite. Soit l'offre n'a pas été trouvée dans la base de données, soit elle a déjà été utilisée par l'utilisateur.
          case None => Unauthorized(Json.obj(
            "status" -> "Not Found or already used",
            "message" -> "This offer has not been found or has already been used by the client."
          ))
          // l'offre a bien été mise à jour.
          case Some(newOffer) => Ok(Json.toJson(newOffer))
        }, Duration.Inf)
        // aucun utilisateur de la base de données ne correspond à cet id
        case None => NotFound(Json.obj(
          "status" -> "Not Found",
          "message" -> ("User #" + request.body.clientId + " not found.")
        ))
      }
    }
  }
}
