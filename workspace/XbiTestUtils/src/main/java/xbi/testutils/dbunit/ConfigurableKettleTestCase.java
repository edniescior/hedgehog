package xbi.testutils.dbunit;

import java.io.File;

import org.junit.Test;

import xbi.testutils.client.Configurables;

/**
 * A dynamic KettleTestCase that is configured at run time.
 * 
 */
public class ConfigurableKettleTestCase extends KettleTestCase {

	private Configurables config = null;

	public ConfigurableKettleTestCase(Configurables c) {
		super(c.getXmlFile());
		this.config = c;
	}

	@Override
	public void registerObjectsForCleanup() {
		for (String table : config.getTargetTables()) {
			connector.registerTableForCleanup(table);
		}
	}

	@Override
	protected void afterSetup() {
		for (File inFile : config.getInFiles()) {
			connector.loadDataSet(inFile);
		}
	}

	@Test
	public void test() throws Exception {
		assertComplete();
		
		// at the moment this will only work with one target table and one test file
		// play around with DBUnit and see if we can mix and match
		String targetTable = config.getTargetTables().get(0);
		File expectedResult = config.getOutFiles().get(0);
		compareDataSets(targetTable, expectedResult);
	}
}
