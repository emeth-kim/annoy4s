import com.sun.jna.Platform

val compileNative = taskKey[Unit]("Compile cpp into shared library.")

lazy val root = (project in file(".")).settings(
  name := "annoy4s",
  version := "0.3.0-SNAPSHOT",
  scalaVersion := "2.10.6",
  libraryDependencies ++= Seq(
    "net.java.dev.jna" % "jna" % "4.2.2",
    "com.github.fommil" % "jniloader" % "1.1",
    "org.slf4s" %% "slf4s-api" % "1.7.12",
    //for test
    "org.scalatest" %% "scalatest" % "2.2.6" % "test",
    "org.slf4j" % "slf4j-simple" % "1.7.14" % "test"
  ),
  fork := true,
  compileNative := {
    val libDir = file(s"src/main/resources/${Platform.RESOURCE_PREFIX}")
    if (!libDir.exists) {
      libDir.mkdirs()
    }
    val lib = if (Platform.RESOURCE_PREFIX == "darwin") {
      libDir / "libannoy.dylib"
    } else {
      libDir / "libannoy.so"
    }
    val sources = Seq("src/main/cpp/annoyjava.cpp", "src/main/cpp/annoyjni.cpp").map(file)
    val javaHome = sys.props.get("java.home").get.split("/").dropRight(1).mkString("/")
    val cmd = s"g++ -o ${lib.getAbsolutePath} -I${javaHome}/include -I${javaHome}/include/${Platform.RESOURCE_PREFIX} -shared -fPIC ${sources.map(_.getAbsolutePath).mkString(" ")}"
    println(cmd)
    import scala.sys.process._
    cmd.!
  }
)
