package xbi.testutils;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.pool.OracleDataSource;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.util.Properties;
 
 
public class JDBCVersionInfo {
   
  public static void main(String[] args) {
    try {
      // Set up user connection properties
      Properties prop = new Properties();
      prop.setProperty("user","eniescior");
      prop.setProperty("password","xxxx");
       
      // open the connection
      OracleDataSource ods = new OracleDataSource();
      ods.setConnectionProperties(prop);
      ods.setURL("jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = ora-br-c012g.br.ccp.cable.comcast.com)(PORT = 1521)) (LOAD_BALANCE = YES) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = BXBIS_SERV) (FAILOVER_MODE = (TYPE = SELECT) (METHOD = BASIC) (RETRIES = 180) (DELAY = 5) ) ) )");
      OracleConnection ocon = (OracleConnection)ods.getConnection();
       
      DatabaseMetaData dbmd = ocon.getMetaData();
       
      System.out.println("Driver Name: " + dbmd.getDriverName());
      System.out.println("Driver Version: " + dbmd.getDriverVersion());
     
       
    } catch(SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}

