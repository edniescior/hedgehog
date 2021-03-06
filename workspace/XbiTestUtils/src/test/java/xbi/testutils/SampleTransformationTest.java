package xbi.testutils;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import xbi.testutils.dbunit.KettleTestCase;

public class SampleTransformationTest extends KettleTestCase {

	public SampleTransformationTest() throws IOException {
		super(new File("/Users/eniesc200/Work/hedgehog/workspace/XbiTestUtils/src/test/pentaho/tr_test_dbunit_file.ktr"));
		// TODO Auto-generated constructor stub
	}

	@Override
	public void registerObjectsForCleanup() {
		connector.registerTableForCleanup("test_table_out");
		connector.registerTableForCleanup("test_table_out2");
	}

	@Override
	protected void afterSetup() {
		connector.loadDataSet(new File(
				"/Users/eniesc200/Work/hedgehog/workspace/XbiTestUtils/src/test/data/test_table_in_ab.xml"));
		
		runner.setParameterValue("property", "propertyValue");
	}

	@Test
	public void sampleTransformation() throws Exception {
		assertComplete();
		compareDataSets("test_table_out", new File(
				"/Users/eniesc200/Work/hedgehog/workspace/XbiTestUtils/src/test/data/test_table_out.xml"), new String[]{"CODE", "SOME_DATE"});
		compareDataSets("test_table_out2", new File(
				"/Users/eniesc200/Work/hedgehog/workspace/XbiTestUtils/src/test/data/test_table_out.xml"));
	}
}
