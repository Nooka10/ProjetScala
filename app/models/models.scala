package models

case class Barmen(id: Option[Long], firstname: String, lastname: String, email: String, password: String, companyId: Long)

case class Client(id: Option[Long], firstname: String, lastname: String, email: String, password: String)

case class Offer(companyId: Long, clientId: Long, beerId: Option[Long])

case class Company(id: Option[Long], name: String, description: Option[String], scheduleId: Option[Long], addressId: Long, image: Option[String])

case class Schedule(id: Option[Long], day: String) // day: DayEnum

case class HOpenClose(id: Option[Long], hOpen: String, hClose: String)

case class Link_Schedule_HOpenClose(id: Option[Long], idSchedule: Long, idHOpenClose: Long)

case class Address(id: Option[Long], no: Option[String], road: String, city: String, postalCode: Int, country: String, lng: Double, lat: Double)

case class Beer(id: Option[Long], name: String, brand: String, degreeAlcohol: Option[Double], image: Option[String])

