package com.comcast.xbi.docutools

import java.sql.Connection
import java.sql.ResultSet
import java.io.File

import oracle.jdbc.pool.OracleDataSource

case class Config(username: String = "", pwd: String = "", hostname: String = "", port: String = "1521", servname: String = "",
  masterDoc: File = new File("."), outputDir: File = new File("."), verbose: Boolean = false)

object XbiDocuTool extends App {

  val parser = new scopt.OptionParser[Config]("XbiDocuTool") {
    head("XbiDocuTool", "1.0")
    opt[String]('u', "username") required () valueName ("<username>") action { (x, c) => c.copy(username = x) } text ("username for database access")
    opt[String]('p', "pwd") required () valueName ("<pwd>") action { (x, c) => c.copy(pwd = x) } text ("password for database access")
    opt[String]('h', "hostname") action { (x, c) => c.copy(hostname = x) } text ("the database host name")
    opt[String]('p', "port") action { (x, c) => c.copy(port = x) } text ("the database port")
    opt[String]('s', "servname") action { (x, c) => c.copy(servname = x) } text ("the database service name")
    opt[File]('m', "masterDoc") required () valueName ("<masterDoc>") action { (x, c) => c.copy(masterDoc = x) } validate { x => 
      if (x.exists() && x.canRead()) success else failure("Option --masterDoc File " + x + " does not exist or is not readable")} text ("the full path to the top-level master Asciidoc file")
    opt[File]('o', "outputDir") action { (x, c) => c.copy(outputDir = x) } text ("the target directory to write to.")
    opt[Unit]("verbose") action {(_, c) => c.copy(verbose = true)} text ("verbose logging")
    note("some notes.\n")
    help("help") text ("prints this usage text")
    
  }

  parser.parse(args, Config()) match {
    case Some(config) =>
      
      // pull the metadata out of the DB
      val metadata = MetadataCollector(config).getMetaData()
      
      // write the files to the target directory
      val writer = MetadataWriter(config)
      writer.clean()
      writer.copyMaster()
      writer.write(metadata)
      
      println("Done")
      System.exit(0)
    case None =>
      System.exit(1)
  }

} 
