name := """ravazzo-school"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

// Base deps
libraryDependencies ++= Seq(
  cache,
  ws,
  "org.webjars" % "swagger-ui" % "2.1.8-M1",
  "com.iheart" %% "play-swagger" % "0.2.0",
  specs2 % Test
)

// SQL deps
libraryDependencies ++= Seq(
  jdbc,
  "com.h2database" % "h2" % "1.4.191",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  "com.typesafe.play" %% "anorm" % "2.4.0"
)

// Swagger deps
libraryDependencies ++= Seq(
  "org.webjars" % "swagger-ui" % "2.1.8-M1",
  "com.iheart" %% "play-swagger" % "0.2.0"
)

// Test deps
libraryDependencies ++= Seq(
  specs2 % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += Resolver.jcenterRepo

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

