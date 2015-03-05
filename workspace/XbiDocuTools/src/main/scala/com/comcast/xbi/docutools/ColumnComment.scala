package com.comcast.xbi.docutools

import org.json4s._
import org.json4s.native.JsonMethods._

object ColumnComment {
  case class ColumnComments(description: String, format: String, examples: Seq[String])
  implicit val formats = DefaultFormats // Brings in default date formats etc.

  def apply(tableName: String, columnId: Int, columnName: String, dataType: String, dataLength: Int,
    dataPrecision: Int, nullable: String, json: String): ColumnComment = {
    val isNull = nullable match { case "Y" => true case "N" => false case _ => false }
    if (json != null) {
      val comments = parse(json).extract[ColumnComments]
      ColumnComment(tableName, columnId, columnName, dataType, dataLength, dataPrecision, isNull,
        comments.description, comments.format, comments.examples)
    } else {
      ColumnComment(tableName, columnId, columnName, dataType, dataLength, dataPrecision, isNull,
        "", "", Seq.empty[String])
    }
  }
  
  def getHeaders() : Seq[String] = {
    Seq("Id", "Column Name", "Data Type", "Data Length", "Data Precision", 
        "Nullable", "Description", "Format", "Examples")
  }
}

case class ColumnComment(tableName: String, columnId: Int, columnName: String, dataType: String, dataLength: Int,
  dataPrecision: Int, nullable: Boolean, description: String, format: String, examples: Seq[String]) {

	def toAsciidoc() : String = {
    val bufc = new StringBuilder()
	  bufc ++= "|" + columnId + "\n" 
	  bufc ++= "|" + columnName + "\n"
	  bufc ++= "|" + dataType + "\n"
	  bufc ++= "|" + dataLength + "\n"
	  bufc ++= "|" + dataPrecision + "\n"
	  bufc ++= "|" + nullable + "\n"
	  bufc ++= "|" + description + "\n"
	  bufc ++= "|" + format + "\n"
    
    if (examples.length > 0 && examples(0) != "") {
      bufc ++= "a|[circle]\n"
      examples.foreach { x => bufc ++= "* " + x + "\n" }
    } else {
      bufc ++= "|\n"
    }
    
    bufc ++= "\n\n"
    bufc.toString()
	}
}