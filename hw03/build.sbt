name := "hw03"

version := "0.1"

scalaVersion := "2.13.6"

idePackagePrefix := Some("company.vk.data")

libraryDependencies  ++= Seq(
  // Last stable release
  "org.scalanlp" %% "breeze" % "1.2",

  // The visualization library is distributed separately as well.
  // It depends on LGPL code
  "org.scalanlp" %% "breeze-viz" % "1.2"
)
