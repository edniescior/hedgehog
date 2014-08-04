package xbi.testutils.dbunit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A dynamic KettleTestCase that is configured at run time. It uses the
 * Parameterized API of JUnit.
 * 
 */
@RunWith(Parameterized.class)
public class ConfigurableKettleTestCase extends KettleTestCase {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ConfigurableKettleTestCase.class);

	// the active Configurable object for a given test. This is what gets passed
	// in as a param by JUnit
	private KettleTestCaseConfiguration config;

	// JUnit needs all the Configurables in place before execution, hence this
	// static collection to hold them
	private static Collection<KettleTestCaseConfiguration[]> configurables = new ArrayList<KettleTestCaseConfiguration[]>();

	/**
	 * Constructor. Called for each parameter configured by the client.
	 */
	public ConfigurableKettleTestCase(KettleTestCaseConfiguration c) {
		super(c.getExecutableFile());
		this.config = c;
	}

	/**
	 * This test class runs Parameterized so we need to prime it for each
	 * different configuration by adding said Configurables to the list.
	 * 
	 * @param c
	 *            a Configurables object to be tested.
	 */
	public static void addConfiguration(KettleTestCaseConfiguration c) {
		configurables.add(new KettleTestCaseConfiguration[] { c });
	}

	/**
	 * This test class runs Parameterized so we need to implement this method.
	 * It returns a collection of Configurables, which will each be executed in
	 * turn.
	 * 
	 * @return a collection of Configurables, one for each test
	 * @throws IllegalStateException
	 *             if a call is made prior to any Configurables being added.
	 */
	@Parameterized.Parameters
	public static Collection<KettleTestCaseConfiguration[]> getConfig() {
		if (configurables.isEmpty()) {
			throw new IllegalStateException(
					"No Configurable objects have been defined for this test case. Call addConfigurable() first.");
		}
		return configurables;
	}

	@Override
	public void registerObjectsForCleanup() {
		for (String table : config.getTargetTables().keySet()) {
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
		
		// set parameters, if any
		for (Map.Entry<String, String> entry : config.getParams().entrySet()) {
			runner.setParameterValue(entry.getKey(), entry.getValue());
		}
		
	}

	@Test
	public void test() throws Exception {
		LOGGER.info("Test execution started");
		assertComplete();

		// figure out which target tables go with each expected result file
		// we call a compare on each target table individually.
		// if there is one expected results file then we assume it is a combined
		// data set
		// and we use that for all comparisons.
		// if there are multiple and their number matches the number of target
		// tables,
		// pass each one in in turn in the order they were given.
		// if there is a mismatch, then throw an exception
		boolean combinedResultFile = false;
		int index = 0;
		if (config.getOutFiles().size() == 1) {
			combinedResultFile = true;
		}
		if (!combinedResultFile
				&& (config.getTargetTables().size() != config.getOutFiles()
						.size())) {
			throw new IllegalStateException(
					"Unable to run comparisons. There are more expected result files than there are target tables.");
		}
		for (String targetTable : config.getTargetTables().keySet()) {
			LOGGER.info("Comparing table " + targetTable + " against expected result set "
					+ config.getOutFiles().get(index).getName());
			String[] orderCols = config.getTargetTables().get(targetTable);
			if (orderCols == null || orderCols.length == 0) {
				compareDataSets(targetTable, config.getOutFiles().get(index));  // natural ordering
			} else {
				compareDataSets(targetTable, config.getOutFiles().get(index), orderCols); // order by column names
			}
			if (!combinedResultFile) {
				index++;
			}
		}

		LOGGER.info("Test execution complete");
	}
}
