package Utils

import models._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

/**
  * Object contenant tout les implicits permettant de convertir un objet JSON en un objet scala correspondant aux modèles et inversément.
  */
object ImplicitsJson {
  implicit val jsonToUserTypeEnum: Reads[UserTypeEnum.Value] = Reads.enumNameReads(UserTypeEnum)
  implicit val jsonToDaysEnum: Reads[DaysEnum.Value] = Reads.enumNameReads(DaysEnum)

  implicit val jsonToLogin: Reads[UserLogin] = (
    (JsPath \ "email").read[String](minLength[String](3) keepAnd maxLength[String](30)) and
      (JsPath \ "password").read[String](minLength[String](6))
    ) (UserLogin.apply _)

  // ------------------------------------------------------------------------------ ADDRESS ------------------------------------------------------------------------------
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
      (JsPath \ "lng").read[Double](min(-180.0) keepAnd max(180.0)) and
      (JsPath \ "lat").read[Double](min(-90.0) keepAnd max(90.0))
    ) (Address.apply _)

  // -------------------------------------------------------------------------------- BEER -------------------------------------------------------------------------------
  implicit val beerToJson: Writes[Beer] = (
    (JsPath \ "id").writeNullable[Long] and
      (JsPath \ "name").write[String] and
      (JsPath \ "brand").write[String] and
      (JsPath \ "degreeAlcohol").writeNullable[Double] and
      (JsPath \ "image").writeNullable[String]
    ) (unlift(Beer.unapply))

  implicit val jsonToBeer: Reads[Beer] = (
    (JsPath \ "id").readNullable[Long] and
      (JsPath \ "name").read[String] and
      (JsPath \ "brand").read[String] and
      (JsPath \ "degreeAlcohol").readNullable[Double](min(0.0) keepAnd max(100.0)) and
      (JsPath \ "image").readNullable[String]
    ) (Beer.apply _)

  // ------------------------------------------------------------------------------ COMPANY ------------------------------------------------------------------------------
  implicit val companyToJson: Writes[Company] = (
    (JsPath \ "id").writeNullable[Long] and
      (JsPath \ "name").write[String] and
      (JsPath \ "description").writeNullable[String] and
      (JsPath \ "addressId").write[Long] and
      (JsPath \ "image").writeNullable[String]
    ) (unlift(Company.unapply))

  implicit val jsonToCompany: Reads[Company] = (
    (JsPath \ "id").readNullable[Long] and
      (JsPath \ "name").read[String](minLength[String](3) keepAnd maxLength[String](30)) and
      (JsPath \ "description").readNullable[String](maxLength[String](300)) and
      (JsPath \ "addressId").read[Long] and
      (JsPath \ "image").readNullable[String]
    ) (Company.apply _)

  // --------------------------------------------------------------------------- DailySchedule ---------------------------------------------------------------------------
  implicit val dailyDailyScheduleToJson: Writes[DailySchedule] = (
    (JsPath \ "id").writeNullable[Long] and
      (JsPath \ "day").write[DaysEnum.Value] and
      (JsPath \ "hOpenAM").write[String] and
      (JsPath \ "hCloseAM").writeNullable[String] and
      (JsPath \ "hOpenPM").writeNullable[String] and
      (JsPath \ "hClosePM").write[String]
    ) (unlift(DailySchedule.unapply))

  implicit val jsonToDailySchedule: Reads[DailySchedule] = (
    (JsPath \ "id").readNullable[Long] and
      (JsPath \ "day").read[DaysEnum.Value] and
      (JsPath \ "hOpenAM").read[String] and
      (JsPath \ "hCloseAM").readNullable[String] and
      (JsPath \ "hOpenPM").readNullable[String] and
      (JsPath \ "hClosePM").read[String]
    ) (DailySchedule.apply _)

  // ------------------------------------------------------------------------------- Offer -------------------------------------------------------------------------------
  implicit val offerToJson: Writes[Offer] = (
    (JsPath \ "companyId").write[Long] and
      (JsPath \ "clientId").write[Long] and
      (JsPath \ "beerId").writeNullable[Long]
    ) (unlift(Offer.unapply))

  implicit val jsonToOffer: Reads[Offer] = (
    (JsPath \ "companyId").read[Long] and
      (JsPath \ "clientId").read[Long] and
      (JsPath \ "beerId").readNullable[Long]
    ) (Offer.apply _)

  // ------------------------------------------------------------------------------- USER --------------------------------------------------------------------------------
  implicit val userToJson: Writes[User] = (
    (JsPath \ "id").writeNullable[Long] and
      (JsPath \ "firstname").write[String] and
      (JsPath \ "lastname").write[String] and
      (JsPath \ "email").write[String] and
      (JsPath \ "password").write[String] and
      (JsPath \ "userType").write[UserTypeEnum.Value] and
      (JsPath \ "companyId").writeNullable[Long]
    ) (unlift(User.unapply))

  implicit val jsonToUser: Reads[User] = (
    (JsPath \ "id").readNullable[Long] and
      (JsPath \ "firstname").read[String](minLength[String](3) keepAnd maxLength[String](30)) and
      (JsPath \ "lastname").read[String](minLength[String](3) keepAnd maxLength[String](30)) and
      (JsPath \ "email").read[String](email) and
      (JsPath \ "password").read[String](minLength[String](6)) and
      (JsPath \ "userType").read[UserTypeEnum.Value] and
      (JsPath \ "companyId").readNullable[Long]
    ) (User.apply _)

  // ----------------------------------------------------------------------- COMPANY WITH OBJECTS ------------------------------------------------------------------------
  implicit val companyWithObjectsToJson: Writes[CompanyWithObjects] = (
    (JsPath \ "id").writeNullable[Long] and
      (JsPath \ "name").write[String] and
      (JsPath \ "description").writeNullable[String] and
      (JsPath \ "schedules").writeNullable[Seq[DailySchedule]] and
      (JsPath \ "address").write[Address] and
      (JsPath \ "image").writeNullable[String]
    ) (unlift(CompanyWithObjects.unapply))

  implicit val jsonToCompanyWithObjects: Reads[CompanyWithObjects] = (
    (JsPath \ "id").readNullable[Long] and
      (JsPath \ "name").read[String](minLength[String](3) keepAnd maxLength[String](30)) and
      (JsPath \ "description").readNullable[String](maxLength[String](300)) and
      (JsPath \ "schedules").readNullable[Seq[DailySchedule]] and
      (JsPath \ "address").read[Address] and
      (JsPath \ "image").readNullable[String]
    ) (CompanyWithObjects.apply _)

  // ------------------------------------------------------------------------ OFFER WITH OBJECTS -------------------------------------------------------------------------
  implicit val offerWithObjectsToJson: Writes[OfferWithObjects] = (
    (JsPath \ "company").write[Company] and
      (JsPath \ "user").write[User] and
      (JsPath \ "beer").writeNullable[Beer]
    ) (unlift(OfferWithObjects.unapply))

  implicit val jsonToOfferWithObjects: Reads[OfferWithObjects] = (
    (JsPath \ "companyId").read[Company] and
      (JsPath \ "userId").read[User] and
      (JsPath \ "beerId").readNullable[Beer]
    ) (OfferWithObjects.apply _)
}
