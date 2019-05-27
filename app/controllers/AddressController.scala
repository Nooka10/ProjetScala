package controllers

import dao.AddressDAO
import javax.inject.{Inject, Singleton}
import models.Address
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AddressController @Inject()(cc: ControllerComponents, addressDAO: AddressDAO) extends AbstractController(cc) {

  implicit val addressToJson: Writes[Address] = (
    (JsPath \ "id").writeNullable[Long] and
      (JsPath \ "no").writeNullable[String] and
      (JsPath \ "road").write[String] and
      (JsPath \ "city").write[String] and
      (JsPath \ "postalCode").write[Int] and
      (JsPath \ "country").write[String] and
      (JsPath \ "lng").write[Double] and
      (JsPath \ "lat").write[Double]
    ) (unlift(Address.unapply))

  implicit val jsonToAddress: Reads[Address] = (
    (JsPath \ "id").readNullable[Long] and
      (JsPath \ "no").readNullable[String] and
      (JsPath \ "road").read[String] and
      (JsPath \ "city").read[String] and
      (JsPath \ "postalCode").read[Int] and
      (JsPath \ "country").read[String] and
      (JsPath \ "lng").read[Double] and
      (JsPath \ "lat").read[Double]
    ) (Address.apply _)

  def validateJson[A: Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def createAddress(address: Address) = addressDAO.insert(address)

  def getAddress(addressId: Long) = addressDAO.findById(addressId)

  def updateAddress(addressToUpdate: Address) = addressDAO.update(addressToUpdate)

  def deleteAddress(addressId: Long) = addressDAO.delete(addressId)
}
