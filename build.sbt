val dottyVersion = "0.22.0-RC1"
val circeVersion = "0.13.0"
lazy val root = project
  .in(file("."))
  .settings(
    name := "sight",
    version := "0.1.0",

    scalaVersion := dottyVersion,

    libraryDependencies ++= Seq(
      "org.scalameta" % "munit_0.22" % "0.5.2",
      "com.softwaremill.sttp.client" % "core_2.13" % "2.0.4",
      "com.softwaremill.sttp.client" % "circe_2.13" % "2.0.4",
      "org.typelevel" % "cats-core_2.13" % "2.0.0",
      "commons-codec" % "commons-codec" % "1.9"
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    jacocoReportSettings := JacocoReportSettings(
      "Test Coverate Report",
      None,
      JacocoThresholds(line = 80),
      Seq(JacocoReportFormats.ScalaHTML, JacocoReportFormats.XML),
      "utf-8"
    ),
    jacocoExcludes := Seq(
      "*Error*",
      "*RecognizedText*",
      "*RecognizedTexts*",
      "*FileContent*",
      "*Payload*"
      )
 )
