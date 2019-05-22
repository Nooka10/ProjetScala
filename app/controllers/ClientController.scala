package controllers

import dao.ClientDAO
import javax.inject.{Inject, Singleton}
import models.Client
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ClientController @Inject()(cc: ControllerComponents, clientsDAO: ClientDAO) extends AbstractController(cc) {

  implicit val clientToJson: Writes[Client] = (
    (JsPath \ "id").write[Option[Long]] and
      (JsPath \ "firstname").write[String] and
      (JsPath \ "lastname").write[String] and
      (JsPath \ "email").write[String] and
      (JsPath \ "password").write[String]
    ) (unlift(Client.unapply))

  implicit val jsonToClient: Reads[Client] = (
    (JsPath \ "id").readNullable[Long] and
      (JsPath \ "firstname").read[String](minLength[String](3) keepAnd maxLength[String](30)) and
      (JsPath \ "lastname").read[String](minLength[String](3) keepAnd maxLength[String](30)) and
      (JsPath \ "email").read[String](minLength[String](3) keepAnd maxLength[String](30)) and
      (JsPath \ "password").read[String](minLength[String](6))
    ) (Client.apply _)

  def validateJson[A: Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def createClient = Action.async(validateJson[Client]) { request =>
    val client: Client = request.body
    val createdClient: Future[Client] = clientsDAO.insert(client)

    createdClient.map(c =>
      Ok(
        Json.obj(
          "status" -> "OK",
          "id" -> c.id,
          "message" -> ("Client '" + c.firstname + " " + c.lastname + "' saved."),
          "client infos" -> Json.toJson(c)
        )
      )
    )
  }

  def getClient(clientId: Long) = Action.async {
    val optionalClient: Future[Option[Client]] = clientsDAO.findById(clientId)

    optionalClient.map {
      case Some(c) => Ok(Json.toJson(c))
      case None =>
        NotFound(Json.obj(
          "status" -> "Not Found",
          "message" -> ("Client #" + clientId + " not found.")
        ))
    }
  }

  def updateClient(clientId: Long) = Action.async(validateJson[Client]) { request =>
    val clientToUpdate: Client = request.body

    clientsDAO.update(clientId, clientToUpdate).map {
      case 1 => Ok(
        Json.obj(
          "status" -> "OK",
          "message" -> ("Client '" + clientToUpdate.firstname + " " + clientToUpdate.lastname + "' updated."),
          "client info updated" -> Json.toJson(clientToUpdate)
        )
      )
      case 0 => NotFound(Json.obj(
        "status" -> "Not Found",
        "message" -> ("Client #" + clientId + " not found.")
      ))
    }
  }

  def deleteClient(clientId: Long) = Action.async {
    clientsDAO.delete(clientId).map {
      case 1 => Ok(
        Json.obj(
          "status" -> "OK",
          "message" -> ("Client #" + clientId + " deleted.")
        )
      )
      case 0 => NotFound(Json.obj(
        "status" -> "Not Found",
        "message" -> ("Client #" + clientId + " not found.")
      ))
    }
  }


}
