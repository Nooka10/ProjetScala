package controllers

import dao.AddressDAO
import javax.inject.{Inject, Singleton}
import models.Address
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext.Implicits.global
import Utils.ImplicitsJson._

@Singleton
class AddressController @Inject()(cc: ControllerComponents, addressDAO: AddressDAO) extends AbstractController(cc) {

  def validateJson[A: Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def createAddress(address: Address) = addressDAO.insert(address)

  def getAddress(addressId: Long) = addressDAO.findById(addressId)

  def updateAddress(addressToUpdate: Address) = addressDAO.update(addressToUpdate)

  def deleteAddress(addressId: Long) = addressDAO.delete(addressId)
}
