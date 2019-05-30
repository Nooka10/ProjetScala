logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.7.2")

addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "1.5.0")