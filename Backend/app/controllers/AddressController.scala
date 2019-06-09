package controllers

import dao.AddressDAO
import javax.inject.{Inject, Singleton}
import models.Address
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class AddressController @Inject()(cc: ControllerComponents, addressDAO: AddressDAO) extends AbstractController(cc) {

  /**
    * Enregistre la nouvelle addresse reçue en paramètre.
    *
    * @param address , l'addresse à enregistrer dans la BDD.
    *
    * @return l'addresse enregistrée
    */
  def createAddress(address: Address) = addressDAO.insert(address)

  /**
    * Retourne l'adresse correspondant à l'id reçu en paramètre.
    *
    * @param addressId , l'id ed l'addresse à retourner.
    *
    * @return l'adresse correspondant à l'id reçu en paramètre.
    */
  def getAddress(addressId: Long) = addressDAO.findById(addressId)

  /**
    * Met à jour l'addresse reçue en paramètre.
    *
    * @param addressToUpdate , l'addresse à mettre à jour.
    *
    * @return l'addresse mise à jour.
    */
  def updateAddress(addressToUpdate: Address) = addressDAO.update(addressToUpdate)

  /**
    * Supprime l'addresse correspondant à l'id reçu en paramètre.
    *
    * @param addressId , l'id de l'addresse à supprimer.
    *
    * @return 1 si l'addresse a bien été supprimée, 0 si l'id ne correspond à aucune addresse dans la BDD.
    */
  def deleteAddress(addressId: Long) = addressDAO.delete(addressId)
}
