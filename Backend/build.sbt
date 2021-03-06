name := "BeerPass"
 
version := "1.0"

lazy val `beerpass` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

libraryDependencies += "com.typesafe.play" %% "play-slick" % "4.0.0" // Slick

libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.15" // Connecteur MySQL

libraryDependencies += "com.zaxxer" % "HikariCP" % "3.3.1"

libraryDependencies += "com.github.t3hnar" %% "scala-bcrypt" % "4.0"

libraryDependencies ++= Seq(
  "com.pauldijou" %% "jwt-play-json" % "2.1.0"
)
