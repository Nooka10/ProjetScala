package models

case class Address(id: Option[Long], no: Option[String], road: String, city: String, postalCode: Int, country: String, lng: Double, lat: Double)

case class Beer(id: Option[Long], name: String, brand: String, degreeAlcohol: Option[Double], image: Option[String])

case class Company(id: Option[Long], name: String, description: Option[String], scheduleId: Option[Long], addressId: Long, image: Option[String])

case class HOpenClose(id: Option[Long], hOpen: String, hClose: String)

case class Link_Schedule_HOpenClose(id: Option[Long], scheduleId: Long, hOpenCloseId: Long)

case class Offer(companyId: Long, userId: Long, beerId: Option[Long])

case class Schedule(id: Option[Long], day: String) // day: DayEnum

case class User(id: Option[Long], firstname: String, lastname: String, email: String, password: String, userType: String, companyId: Option[Long])

// case class UserType(userType: String)

case class UserLogin(email: String, password: String)
