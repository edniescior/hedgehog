package xbi.testutils.dbunit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xbi.testutils.client.Configurables;

/**
 * A dynamic KettleTestCase that is configured at run time. It uses the Parameterized API of JUnit. 
 * 
 */
@RunWith(Parameterized.class)
public class ConfigurableKettleTestCase extends KettleTestCase {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ConfigurableKettleTestCase.class);
	
	// the active Configurable object for a given test. This is what gets passed in as a param by JUnit
	private Configurables config;
	
	// JUnit needs all the Configurables in place before execution, hence this static collection to hold them
	private static Collection<Configurables[]> configurables = new ArrayList<Configurables[]>();

	/**
	 * Constructor. Called for each parameter configured by the client.
	 */
	public ConfigurableKettleTestCase(Configurables c) {
		super(c.getXmlFile());
		this.config = c;
	}
	
	/**
	 * This test class runs Parameterized so we need to prime it for each different configuration by adding
	 * said Configurables to the list.
	 * @param c a Configurables object to be tested.
	 */
	public static void addConfigurable(Configurables c) {
		configurables.add(new Configurables[] { c });
	}
	
	/**
	 * This test class runs Parameterized so we need to implement this method. It returns a collection of
	 * Configurables, which will each be executed in turn.
	 * @return
	 */
	@Parameterized.Parameters
	public static Collection<Configurables[]> getConfig() {	
		return configurables;
	}

	@Override
	public void registerObjectsForCleanup() {
		for (String table : config.getTargetTables()) {
			connector.registerTableForCleanup(table);
			LOGGER.info("Registered " + table + " for cleanup");
		}
	}

	@Override
	protected void afterSetup() {
		for (File inFile : config.getInFiles()) {
			connector.loadDataSet(inFile);
			LOGGER.info("Loading input test file: " + inFile.getAbsolutePath());
		}
	}

	@Test
	public void test() throws Exception {
		LOGGER.info("Test execution started");
		assertComplete();
		
		// at the moment this will only work with one target table and one test file
		// play around with DBUnit and see if we can mix and match
		String targetTable = config.getTargetTables().get(0);
		File expectedResult = config.getOutFiles().get(0);
		compareDataSets(targetTable, expectedResult);
		
		LOGGER.info("Test execution complete");
	}
}
