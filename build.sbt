import com.sun.jna.Platform

val compileNative = taskKey[Unit]("Compile cpp into shared library.")

lazy val root = (project in file(".")).settings(
  name := "spark-annoy",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.10.6",
  libraryDependencies ++= Seq(
    "net.java.dev.jna" % "jna" % "4.2.2",
    "org.apache.spark" %% "spark-core" % "1.6.2" % "provided",
    "org.apache.spark" %% "spark-mllib" % "1.6.2" % "provided",
    "com.github.scopt" %% "scopt" % "3.2.0",
    "com.github.fommil.netlib" % "all" % "1.1.2" pomOnly(),
    //for test
    "org.scalatest" %% "scalatest" % "2.2.6" % "test"

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
    val source = file("src/main/cpp/annoyjava.cpp")
    val cmd = s"g++ -O3 -march=native -ffast-math -o ${lib.getAbsolutePath} -shared -fPIC ${source.getAbsolutePath}"
    println(cmd)
    import scala.sys.process._
    cmd.!
  }
)
