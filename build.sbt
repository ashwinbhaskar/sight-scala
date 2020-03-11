val dottyVersion = "0.22.0-RC1"
val circeVersion = "0.13.0"
lazy val root = project
  .in(file("."))
  .settings(
    name := "dotty-simple",
    version := "0.1.0",

    scalaVersion := dottyVersion,

    libraryDependencies ++= Seq(
      "com.novocode" % "junit-interface" % "0.11" % "test",
      "com.softwaremill.sttp.client" % "core_2.13" % "2.0.2",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-jawn" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-numbers" % circeVersion,
      "org.scalameta" % "munit_0.22" % "0.5.2"
    ),
    testFrameworks += new TestFramework("munit.Framework")
 )
