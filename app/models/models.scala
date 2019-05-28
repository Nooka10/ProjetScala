package models

case class Address(id: Option[Long], no: Option[String], road: String, city: String, postalCode: Int, country: String, lng: Double, lat: Double)

case class Beer(id: Option[Long], name: String, brand: String, degreeAlcohol: Option[Double], image: Option[String])

case class Company(id: Option[Long], name: String, description: Option[String], addressId: Long, image: Option[String])

case class CompanyWithObjects(id: Option[Long], name: String, description: Option[String], schedules: Option[Seq[DailySchedule]], address: Address, image: Option[String])

case class DailySchedule(id: Option[Long], day: String, hOpenAM: String, hCloseAM: Option[String], hOpenPM: Option[String], hClosePM: String)

case class Link_DailySchedule_Company(id: Option[Long], companyId: Long, dailyScheduleId: Long)

case class Offer(companyId: Long, userId: Long, beerId: Option[Long])

case class OfferWithObjects(company: Company, user: User, beer: Option[Beer])

case class User(id: Option[Long], firstname: String, lastname: String, email: String, password: String, userType: UserTypeEnum.Value, companyId: Option[Long])

case class UserLogin(email: String, password: String)

object CompanyWithObjects {
  def fromCompany(company: Company, address: Address, schedule: Option[Seq[DailySchedule]]): CompanyWithObjects = {
    CompanyWithObjects(company.id, company.name, company.description, schedule, address, company.image)
  }
}

object CompanyWithoutObjects {
  def fromCompanyWithObjects(company: CompanyWithObjects): Company = {
    Company(company.id, company.name, company.description, company.address.id.get, company.image)
  }
}

object UserTypeEnum extends Enumeration {
  type userType = Value
  val CLIENT = Value("CLIENT")
  val EMPLOYEE = Value("EMPLOYEE")
}
