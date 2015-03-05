package com.comcast.xbi.docutools

import java.sql.Connection
import java.sql.ResultSet

import oracle.jdbc.pool.OracleDataSource

case class MetadataCollector(conf: Config) {

  val ods = new OracleDataSource()
  ods.setUser(conf.username)
  ods.setPassword(conf.pwd)
  ods.setURL("jdbc:oracle:thin:@" + conf.hostname + ":" + conf.port + "/" + conf.servname)

  def getMetaData(): List[Table] = {
    val con = ods.getConnection()
    println("Connected")
    /* build a container to hold Table metadata */
    var allTables = List[Table]()

    /*
   * Pull comments and metadata from the DB
   */
    val s1 = con.prepareStatement("SELECT COLS.TABLE_NAME, COLS.COLUMN_ID, COLS.COLUMN_NAME, COLS.DATA_TYPE, COLS.DATA_LENGTH, COLS.DATA_PRECISION, COLS.NULLABLE, COMS.COMMENTS"
      + " FROM ALL_TAB_COLS COLS"
      + " JOIN ALL_COL_COMMENTS COMS ON COMS.OWNER = COLS.OWNER AND COMS.TABLE_NAME = COLS.TABLE_NAME AND COMS.COLUMN_NAME = COLS.COLUMN_NAME"
      + " WHERE COLS.OWNER = \'ENIESCIOR\'"
      + " AND COLS.TABLE_NAME = :tabname "
      + " ORDER BY COLS.TABLE_NAME, COLS.COLUMN_ID")

    val s2 = con.createStatement()
    val rs2 = s2.executeQuery("SELECT TABS.OWNER, TABS.TABLE_NAME, TABS.TABLE_TYPE, TABS.COMMENTS "
      + "FROM ALL_TAB_COMMENTS TABS "
      + "WHERE TABS.OWNER IN (\'ENIESCIOR\') "
      + "AND TABS.TABLE_NAME like (\'W_%_D\') "
      + "ORDER BY TABS.TABLE_NAME ")

    // Loop through the tables
    while (rs2.next()) {
      val owner = rs2.getString(1)
      val tableName = rs2.getString(2)
      val tableType = rs2.getString(3)
      val tableComments = rs2.getString(4)

      if (conf.verbose) println("Processing " + tableName)
      val tableComment = TableComment.apply(owner, tableName, tableType, tableComments)
      if (conf.verbose) println(tableComment)

      var theseComments = List[ColumnComment]()

      // Loop through the columns
      s1.setString(1, tableName)
      val rs = s1.executeQuery()
      while (rs.next()) {
        val tableName = rs.getString(1)
        val columnId = rs.getInt(2)
        val columnName = rs.getString(3)
        val dataType = rs.getString(4)
        val dataLength = rs.getInt(5)
        val dataPrecision = rs.getInt(6)
        val nullable = rs.getString(7)
        val comments = rs.getString(8)

        val columnComment = ColumnComment.apply(tableName, columnId, columnName, dataType, dataLength, dataPrecision, nullable, comments)
        if (conf.verbose) println(columnComment)

        theseComments = theseComments :+ columnComment
      }

      val thisTable = Table(tableComment, theseComments)
      if (conf.verbose) println(thisTable)

      allTables = allTables :+ thisTable
    }

    con.close()
    println("Closed connection")
    allTables
  }

}