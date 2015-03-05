package com.comcast.xbi.docutools

import org.json4s._
import org.json4s.native.JsonMethods._

object TableComment {
  case class TableComments(subjectArea: String, description: String, granularity: String, 
      notes: Seq[String], warnings: Seq[String], tips: Seq[String])
  implicit val formats = DefaultFormats // Brings in default date formats etc.

  def apply(owner: String, tableName: String, tableType: String, json: String): TableComment = {
    if (json != null) {
      val comments = parse(json).extract[TableComments]
      TableComment(owner, tableName, tableType, comments.subjectArea, comments.description, comments.granularity,
        comments.notes, comments.warnings, comments.tips)
    } else {
      TableComment(owner, tableName, tableType, "", "", "", 
          notes = Seq.empty[String], warnings = Seq.empty[String], tips = Seq.empty[String])
    }
  }
}

case class TableComment(owner: String, tableName: String, tableType: String, subjectArea: String,
  description: String, granularity: String, notes: Seq[String], warnings: Seq[String], tips: Seq[String]) {

  def header(): String = {
    val bufr = new StringBuilder("=== " + tableName)
    bufr ++= "\n\n"
    bufr ++= description
    bufr ++= "\n\n"
    bufr ++= "[horizontal]\n"
    bufr ++= "Object Type:: " + tableType + "\n"
    bufr ++= "Owner:: " + owner + "\n"
    bufr ++= "Granularity:: " + granularity + "\n\n"
    warnings.foreach { x => bufr ++= "WARNING: " + x + "\n\n" }
    bufr.toString()
  }

  def footer(): String = {
    val buft = new StringBuilder()
    notes.foreach { x => buft ++= "NOTE: " + x + "\n\n" }
    tips.foreach { x => buft ++= "TIP: " + x + "\n\n"}
    buft.toString()
  }
}