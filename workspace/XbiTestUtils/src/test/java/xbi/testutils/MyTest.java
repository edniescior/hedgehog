package xbi.testutils;

import java.io.File;

import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;

import xbi.testutils.dbunit.Connector;
import xbi.testutils.kettle.Runner;
import xbi.testutils.kettle.TransformationRunner;

public class MyTest {

	public static void main(String[] args) {
		String url = "jdbc:oracle:thin:@(DESCRIPTION= (ADDRESS_LIST= (LOAD_BALANCE=yes) (ADDRESS= (PROTOCOL=TCP) (HOST=ccpscn-dt-a-dqi.dt.ccp.cable.comcast.com) (PORT=1521) ) ) (CONNECT_DATA= (FAILOVER_MODE= (TYPE=select) (METHOD=basic) (RETRIES=180) (DELAY=5) ) (SERVER=dedicated) (SERVICE_NAME=DXBID_SERV.dt.ccp.cable.comcast.com) ) )";
		String user = "eniescior";
		String pwd = "k8Zj6l";
		String schema = "eniescior";

		Connector connector = new Connector(url, user, pwd, schema);

		try {

			connector.dumpXml("test_table_in_a",
					"select * from test_table_in_a",
					"/Users/eniesc200/Work/Pentaho/test_table_in_a.xml");
			connector.dumpXml("test_table_in_b",
					"select * from test_table_in_b",
					"/Users/eniesc200/Work/Pentaho/test_table_in_b.xml");

			// connector.loadDataSet(new File(
			// "/Users/eniesc200/Work/Pentaho/test_table_in_ab.xml"));

			// connector.dumpXml("test_table_out",
			// "select * from test_table_out",
			// "/Users/eniesc200/Work/Pentaho/test_table_out.xml");

//			connector.dumpXml("ws_xbo_devices",
//					"select * from ws_xbo_devices",
//					"/Users/eniesc200/Work/XBI/trunk/Pentaho/Projects/xbo/data/test/input/tr_xbo_stg_activdisco.in.xml");
//			connector.dumpXml("ws_xbo_devices_sus",
//					"select * from ws_xbo_devices_sus",
//					"/Users/eniesc200/Work/XBI/trunk/Pentaho/Projects/xbo/data/test/output/tr_xbo_stg_activdisco.out.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
