package com.comcast.xbi.docutools

object Table {

}

case class Table(tableComment: TableComment, columnComments: Seq[ColumnComment]) {

  def toAsciidoc(): String = {
    // create the table-level text
    val buf = new StringBuilder()
   
    buf ++= tableComment.header()

    // create the column-level text table
    buf ++= ".Table " + tableComment.tableName + " Column Definitions\n"
    buf ++= "|===\n"
    buf ++= ColumnComment.getHeaders().mkString("|", "|", " ")
    buf ++= "\n\n"
    columnComments.foreach(col => buf ++= col.toAsciidoc)
    buf ++= "|===\n"
    
    buf ++= tableComment.footer()
    
    buf.toString()
  }
}