organization := "io.github.ashwinbhaskar"
homepage := Some(url("https://github.com/ashwinbhaskar/sight-dotty"))
developers := List(Developer("ashwinbhaskar",
                             "Ashwin Bhaskar",
                             "ashwinbhskr@gmail.com",
                             url("https://github.com/ashwinbhaskar")))
scmInfo := Some(
  ScmInfo(url("https://github.com/ashwinbhaskar/sight-dotty"),
    "scm:git@github.com:ashwinbhaskar/sight-dotty.git"))
licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
publishMavenStyle := true
// Add sonatype repository settings
publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)
val dottyVersion = "0.22.0"
val circeVersion = "0.13.0"
lazy val root = project
  .in(file("."))
  .settings(
    name := "sight-client",
    version := "0.1.0",

    scalaVersion := dottyVersion,
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.5.2",
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
      "*Payload*",
      "*SightClient$*"
      )
 )
