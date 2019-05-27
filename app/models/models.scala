package models

case class Address(id: Option[Long], no: Option[String], road: String, city: String, postalCode: Int, country: String, lng: Double, lat: Double)

case class Beer(id: Option[Long], name: String, brand: String, degreeAlcohol: Option[Double], image: Option[String])

case class Company(id: Option[Long], name: String, description: Option[String], addressId: Long, image: Option[String])

case class CompanyWithObjects(id: Option[Long], name: String, description: Option[String], schedule: Option[Seq[Schedule]], address: Address, image: Option[String])

case class Link_Schedule_Company(id: Option[Long], companyId: Long, scheduleId: Long)

case class Offer(companyId: Long, userId: Long, beerId: Option[Long])

case class OfferWithObjects(company: Company, user: User, beer: Option[Beer])

case class Schedule(id: Option[Long], day: String, hOpenAM: String, hCloseAM: Option[String], hOpenPM: Option[String], hClosePM: String)

case class User(id: Option[Long], firstname: String, lastname: String, email: String, password: String, userType: String, companyId: Option[Long])

// case class UserType(userType: String)

case class UserLogin(email: String, password: String)

object CompanyWithObjects {
  def fromCompany(company: Company, address: Address, schedule: Option[Seq[Schedule]]): CompanyWithObjects = {
    CompanyWithObjects(company.id, company.name, company.description, schedule, address, company.image)
  }
}

object Company {
  def fromCompanyWithObjects(company: CompanyWithObjects): Company = {
    Company(company.id, company.name, company.description, company.address.id.get, company.image)
  }
}
