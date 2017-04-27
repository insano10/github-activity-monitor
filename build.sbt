name := "observation-deck"

organization in ThisBuild := "com.insano10"

version := "0.1-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.8"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)

// Dependencies

val DispatchVersion = "0.11.3"

libraryDependencies ++= Seq(
  filters,
  cache,
  "org.kohsuke" % "github-api" % "1.77",
  "net.databinder.dispatch" %% "dispatch-core" % DispatchVersion,
  "net.databinder.dispatch" %% "dispatch-json4s-native" % DispatchVersion,
  // WebJars (i.e. client-side) dependencies
  "org.webjars" % "requirejs" % "2.3.2",
  "org.webjars" % "underscorejs" % "1.8.3",
  "org.webjars.bower" % "tether" % "1.3.3",
  "org.webjars" % "jquery" % "2.2.0",
  "org.webjars" % "bootstrap" % "4.0.0-alpha.6-1" exclude("org.webjars", "jquery"),
  "org.webjars" % "angularjs" % "1.5.0-rc.2" exclude("org.webjars", "jquery"),
  "org.webjars" % "octicons" % "4.2.0"
)

// Scala Compiler Options
scalacOptions in ThisBuild ++= Seq(
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-deprecation", // warning and location for usages of deprecated APIs
  "-feature", // warning and location for usages of features that should be imported explicitly
  "-unchecked", // additional warnings where generated code depends on assumptions
  "-Xlint", // recommended additional warnings
  "-Xcheckinit", // runtime error when a val is not initialized due to trait hierarchies (instead of NPE somewhere else)
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
  //"-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver
  "-Ywarn-value-discard", // Warn when non-Unit expression results are unused
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures
  "-Ywarn-dead-code", // Warn when dead code is identified
  "-Ywarn-unused", // Warn when local and private vals, vars, defs, and types are unused
  "-Ywarn-unused-import", //  Warn when imports are unused (don't want IntelliJ to do it automatically)
  "-Ywarn-numeric-widen" // Warn when numerics are widened
)

//
// sbt-web configuration
// https://github.com/sbt/sbt-web
//

// Configure the steps of the asset pipeline (used in stage and dist tasks)
// rjs = RequireJS, uglifies, shrinks to one file, replaces WebJars with CDN
// digest = Adds hash to filename
// gzip = Zips all assets, Asset controller serves them automatically when client accepts them
pipelineStages := Seq(rjs, digest, gzip)

// RequireJS with sbt-rjs (https://github.com/sbt/sbt-rjs#sbt-rjs)
// ~~~
RjsKeys.paths += ("jsRoutes" -> ("/jsroutes" -> "empty:"))

//RjsKeys.mainModule := "main"

// Asset hashing with sbt-digest (https://github.com/sbt/sbt-digest)
// ~~~
// md5 | sha1
//DigestKeys.algorithms := "md5"
//includeFilter in digest := "..."
//excludeFilter in digest := "..."

// HTTP compression with sbt-gzip (https://github.com/sbt/sbt-gzip)
// ~~~
// includeFilter in GzipKeys.compress := "*.html" || "*.css" || "*.js"
// excludeFilter in GzipKeys.compress := "..."

// JavaScript linting with sbt-jshint (https://github.com/sbt/sbt-jshint)
// ~~~
// JshintKeys.config := ".jshintrc"

// All work and no play...
emojiLogs
