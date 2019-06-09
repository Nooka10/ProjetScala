package models

case class Address(id: Option[Long], no: Option[String], road: String, city: String, postalCode: Int, country: String, lng: Double, lat: Double)

case class Beer(id: Option[Long], name: String, brand: String, degreeAlcohol: Option[Double], image: Option[String])

// Défini une Company tel qu'elle est réellement enregistrée dans la base de données
case class Company(id: Option[Long], name: String, description: Option[String], addressId: Long, image: Option[String])

// Défini une company avec tous les objets qu'elle contient (schedules, address)
case class CompanyWithObjects(id: Option[Long], name: String, description: Option[String], schedules: Option[Seq[DailySchedule]], address: Address, image: Option[String])

case class DailySchedule(id: Option[Long], day: DaysEnum.Value, hOpenAM: String, hCloseAM: Option[String], hOpenPM: Option[String], hClosePM: String)

case class Link_Company_Beer(id: Option[Long], companyId: Long, beerId: Long)

case class Link_DailySchedule_Company(id: Option[Long], companyId: Long, dailyScheduleId: Long)

case class Offer(companyId: Long, clientId: Long, beerId: Option[Long] = null)

// Défini une Offer tel qu'elle est réellement enregistrée dans la base de données. L'id est inutile mais obligatoire à cause de slick qui nous oblige à avoir une colonne en autoInc...!
case class OfferWithID(companyId: Long, clientId: Long, beerId: Option[Long] = null, id: Option[Long] = null)

// Défini une Offer avec tous les objets qu'elle contient (company, User, Beer)
case class OfferWithObjects(company: Company, client: User, beer: Option[Beer])

case class User(id: Option[Long], firstname: String, lastname: String, email: String, password: String, userType: UserTypeEnum.Value, companyId: Option[Long])

case class UserLogin(email: String, password: String)

object OfferWithIDToOffer {
  // permet de passer facilement d'une Offer à une OfferWithID
  def fromOffer(offer: Offer) = OfferWithID(offer.companyId, offer.clientId, offer.beerId)
}

object CompanyWithObjects {
  // permet de passer facilement d'une Company à une CompanyWithObjects
  def fromCompany(company: Company, address: Address, schedule: Option[Seq[DailySchedule]]): CompanyWithObjects = {
    CompanyWithObjects(company.id, company.name, company.description, schedule, address, company.image)
  }
}

// Enum définissant les 2 types d'utilisateurs possibles (CLIENT ou EMPLOYEE)
object UserTypeEnum extends Enumeration {
  type userType = Value
  val CLIENT = Value("CLIENT")
  val EMPLOYEE = Value("EMPLOYEE")
}

// Enum définissant les 7 jours possibles
object DaysEnum extends Enumeration {
  type userType = Value
  val MONDAY = Value("MONDAY")
  val TUESDAY = Value("TUESDAY")
  val WEDNESDAY = Value("WEDNESDAY")
  val THURSDAY = Value("THURSDAY")
  val FRIDAY = Value("FRIDAY")
  val SATURDAY = Value("SATURDAY")
  val SUNDAY = Value("SUNDAY")
}
