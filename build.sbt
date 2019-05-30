name := "BeerPass"
 
version := "1.0"

lazy val `beerpass` = (project in file(".")).enablePlugins(PlayScala)

enablePlugins(sbtdocker.DockerPlugin)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

libraryDependencies += "com.typesafe.play" %% "play-slick" % "4.0.0" // Slick

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.24" // Connecteur MySQL

libraryDependencies += "com.zaxxer" % "HikariCP" % "3.3.1"

libraryDependencies += "com.github.t3hnar" %% "scala-bcrypt" % "4.0"

libraryDependencies ++= Seq(
  "com.pauldijou" %% "jwt-play-json" % "2.1.0"
)

/*
dockerfile in docker := {
  val jarFile = sbt.Keys.`package`.in(Compile, packageBin).value
  val classpath = (managedClasspath in Compile).value
  val mainclass = mainClass.in(Compile, packageBin).value.getOrElse(sys.error("Expected exactly one main class"))
  val jarTarget = s"/app/${jarFile.getName}"
  // Make a colon separated classpath with the JAR file
  val classpathString = classpath.files.map("/app/" + _.getName).mkString(":") + ":" + jarTarget
  new Dockerfile {
    // Base image
    from("openjdk:8-jre")
    // Add all files on the classpath
    add(classpath.files, "/app/")
    // Add the JAR file
    add(jarFile, jarTarget)
    // On launch run Java with the classpath and the main class
    entryPoint("java", "-cp", classpathString, mainclass)
  }
}
 */

dockerAutoPackageJavaApplication()

// Set names for the image
imageNames in docker := Seq(
  ImageName("beerpass"),
  ImageName(namespace = Some("beerPass"),
    repository = name.value,
    tag = Some("v" + version.value))
)
