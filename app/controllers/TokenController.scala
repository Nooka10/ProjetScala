package controllers

import models.User
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtJson}
import play.api.libs.json.JsObject

class TokenController {

  private val secretKey = "secretKey"
  private val algorithm = JwtAlgorithm.HS256

  def createConnectionToken(user: User): String = {
    import play.api.libs.json.Json
    val content: JsObject = Json.obj(
      "id" -> user.id,
      "email" -> user.email,
      "userType" -> user.userType,
      "companyId" -> user.companyId
    )

    val claim: JwtClaim = JwtClaim(content.toString(), subject = Option("connectionToken")).issuedNow
    JwtJson.encode(claim, secretKey, algorithm)
  }

  /*
  def validateToken(token: Option[String]): Try[JsObject] = {
    if (token.nonEmpty) {
      JwtJson.decodeJsonAll(token.get, secretKey, Seq(algorithm))
    }
  }
   */
}
