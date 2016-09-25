import com.earldouglas.xwp.JettyPlugin
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}
import NativePackagerHelper._
import sbt.Keys._
import sbt._

lazy val main = project.in(file(".")).
  enablePlugins(JettyPlugin).
  enablePlugins(JavaAppPackaging)


name := "Github Activity Monitor"
version := "0.1.0-SNAPSHOT"
scalaVersion := "2.11.8"

maintainer in Docker := "insano10 <jdommett@gmail.com>"
packageSummary in Docker := "A Github monitoring dashboard"
packageDescription := "Monitor the status of Github repositories"
dockerExposedPorts in Docker := Seq(8080)


val ScalatraVersion = "2.4.1"
val DispatchVersion = "0.11.3"

libraryDependencies ++= Seq(
  "org.kohsuke" % "github-api" % "1.77",
  "com.typesafe" % "config" % "1.2.1",
  "com.typesafe.scala-logging" % "scala-logging_2.11" % "3.4.0",
  "com.github.cb372" %% "scalacache-guava" % "0.9.1",
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
  "org.scalatra" %% "scalatra-json" % ScalatraVersion,
  "org.json4s" %% "json4s-jackson" % "3.3.0",
  "com.typesafe.akka" %% "akka-actor" % "2.4.9",
  "net.databinder.dispatch" %% "dispatch-core" % DispatchVersion,
  "net.databinder.dispatch" %% "dispatch-json4s-native" % DispatchVersion,
  "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.5" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "9.2.15.v20160210" % "container;compile",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided"
)

resolvers += Classpaths.typesafeReleases
resolvers += Resolver.jcenterRepo
resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"



//add config files to docker stage
mappings in Universal += file("nginx.conf") -> "nginx.conf"
mappings in Universal += file("docker-run.sh") -> "docker-run.sh"

//add webapp directory and all content into docker stage
mappings in Universal <++= baseDirectory map { baseDir =>
  val dir = baseDir / "src" / "main" / "webapp"
  (dir.*** --- dir) pair rebase(dir, "static")
}

dockerCommands := Seq(
  Cmd("FROM", "java:8"),
  Cmd("MAINTAINER", "insano10 <jdommett@gmail.com>"),
  Cmd("ENV", "APP_PORT", "8090"),
  Cmd("WORKDIR", "/opt/docker"),
  Cmd("ADD", "opt /opt"),
  Cmd("EXPOSE", "8080"),

  Cmd("RUN", "apt-get", "update"),
  Cmd("RUN", "apt-get", "install", "-y", "nginx"),
  Cmd("ADD", "opt/docker/nginx.conf", "/etc/nginx/nginx.conf"),
  Cmd("ADD", "opt/docker/docker-run.sh", "/opt/docker/bin/docker-run.sh"),

  Cmd("RUN", "[\"chown\", \"-R\", \"daemon:daemon\", \".\"]"),
  Cmd("ENTRYPOINT", "[\"bin/docker-run.sh\"]"),
  ExecCmd("CMD")
)
