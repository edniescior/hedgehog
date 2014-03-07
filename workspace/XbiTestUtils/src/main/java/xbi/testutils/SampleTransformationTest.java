package xbi.testutils;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import xbi.testutils.dbunit.KettleTestCase;

public class SampleTransformationTest extends KettleTestCase {

	public SampleTransformationTest() throws IOException {
		super(new File("/Users/eniesc200/Work/Pentaho/tr_test_dbunit_file.ktr"));
		// TODO Auto-generated constructor stub
	}

	@Override
	public void registerObjectsForCleanup() {
		connector.registerTableForCleanup("test_table_out");
	}

	@Override
	protected void afterSetup() {
		connector.loadDataSet(new File(
				"/Users/eniesc200/Work/Pentaho/test_table_in_ab.xml"));
	}

	@Override
	protected String getPropertyPrefix() {
		return "xbi";
	}

	@Test
	public void sampleTransformation() throws Exception {
		assertComplete();
		compareDataSets("test_table_out", new File(
				"/Users/eniesc200/Work/Pentaho/test_table_out.xml"));
	}
}
