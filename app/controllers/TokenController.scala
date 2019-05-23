package controllers

import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtJson}
import play.api.libs.json.JsObject

class TokenController {

  import models.User

  def createConnectionToken(user: User): String = {
    import play.api.libs.json.Json
    val content: JsObject = Json.obj(
      "id" -> user.id,
      "email" -> user.email,
      "userType" -> user.userType,
      "companyId" -> user.companyId
    )

    val claim: JwtClaim = JwtClaim(content.toString(), subject = Option("connectionToken")).issuedNow
    JwtJson.encode(claim, "secretKey", JwtAlgorithm.HS256)
  }


}
