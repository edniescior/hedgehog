package com.comcast.xbi.docutools

import java.io._

/**
 * Generates the include files for each subject area
 */
case class MetadataWriter(conf: Config) {

  /* a mapping of subject areas to the include files written for each */
  val subjectAreas = Map("Core Dims" -> "core.asciidoc", "Account" -> "account.asciidoc", "Device" -> "device.asciidoc",
    "Activations" -> "activations.asciidoc", "CPE" -> "cpe.asciidoc", "DVR" -> "dvr.asciidoc", "Care" -> "care.asciidoc",
    "IP Video" -> "ipvideo.asciidoc", "Miscellaneous DW" -> "miscdw.asciidoc", "Usage" -> "usage.asciidoc")

  /* a copy of the master asciidoc file that is copied to the output target directory */
  val masterCopy = new File(conf.outputDir + File.separator + conf.masterDoc.getName)

  /**
   * Remove any asciidoc files currently in the output target directory
   */
  def clean(): Unit = {
    masterCopy.getCanonicalFile.getParentFile.mkdirs() // create the target dir path if it does not exist
    for {
      files <- Option(masterCopy.getCanonicalFile.getParentFile.listFiles())
      file <- files if file.getName.endsWith(".asciidoc") 
    }  file.delete()
  }

  /**
   *  Copy the master asciidoc file to the output target directory
   */
  def copyMaster(): Unit = {
    masterCopy.createNewFile()
    new FileOutputStream(masterCopy) getChannel () transferFrom (
      new FileInputStream(conf.masterDoc) getChannel, 0, Long.MaxValue)
  }

  /**
   * Write out asciidoc files for each subject area in the output target directory.
   */
  def write(tables: List[Table]): Unit = {
    println("Writing to " + conf.outputDir)

    // loop through each subject area and output the text to the appropriate file
    for ((k, v) <- subjectAreas) {
      val outFile = conf.outputDir + File.separator + v
      if (conf.verbose) println("Printing " + k + " to " + outFile)

      val subjectAreaTables = tables.filter(x => x.tableComment.subjectArea == k)

      val pw = new PrintWriter(new File(outFile))
      pw.write("== " + k + "\n\n")
      subjectAreaTables.foreach(table => pw.write(table.toAsciidoc()))
      pw.close()
    }
  }
}