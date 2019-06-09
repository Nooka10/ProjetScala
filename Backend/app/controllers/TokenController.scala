package controllers

import models.User
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtJson}
import play.api.libs.json.{JsObject, Json}

class TokenController {

  private val secretKey = "secretKey"
  private val algorithm = JwtAlgorithm.HS256

  /**
    * Crée un token de connexion pour l'utilisateur reçu en paramètre.
    *
    * @param user , l'utilisateur pour lequel on souhaite créer un token de connexion.
    *
    * @return un token de connexion pour l'utilisateur reçu en paramètre.
    */
  def createConnectionToken(user: User): String = {
    val content: JsObject = Json.obj(
      "id" -> user.id,
      "email" -> user.email,
      "userType" -> user.userType,
      "companyId" -> user.companyId
    )

    val claim: JwtClaim = JwtClaim(content.toString(), subject = Option("connectionToken")).issuedNow
    JwtJson.encode(claim, secretKey, algorithm)
  }
}
