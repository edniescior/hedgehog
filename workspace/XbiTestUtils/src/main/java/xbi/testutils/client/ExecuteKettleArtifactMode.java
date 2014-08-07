/**
 * 
 */
package xbi.testutils.client;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xbi.testutils.dbunit.ConfigurableKettleTestCase;
import xbi.testutils.dbunit.KettleTestCaseConfiguration;


/**
 * This mode will execute a Kettle DBUnit test for a single Kettle artifact (as
 * defined in an XML file - KTR/KJB).
 * 
 * @author eniesc200
 * 
 */
class ExecuteKettleArtifactMode extends Mode {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ExecuteKettleArtifactMode.class);

	// configuration for a single test case run
	private KettleTestCaseConfiguration configuration;

	private boolean isValid = false;

	/**
	 * 
	 */
	ExecuteKettleArtifactMode() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xbi.testutils.client.Mode#execute()
	 */
	@Override
	void execute() {
		ConfigurableKettleTestCase.addConfiguration(this.configuration);
		Result result = JUnitCore.runClasses(ConfigurableKettleTestCase.class);
		for (Failure failure : result.getFailures()) {
			LOGGER.error(failure.toString());
		}
		LOGGER.info("Test success?  " + result.wasSuccessful());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xbi.testutils.client.Mode#isValid()
	 */
	@Override
	boolean isValid() {
		return isValid;
	}

	/**
	 * Helper method to initialize a DBUnit Kettle test case from an XML configuration file.
	 * 
	 * @param config
	 *            XML test case configuration
	 * @throws IllegalStateException
	 *             if the initialization fails
	 */
	public void loadFromXMLConfig(File config) throws IllegalStateException {
		if (config == null) {
			throw new NullPointerException("xmlConfigFile");
		}
		if (!config.exists() || !config.canRead()) {
			throw new IllegalStateException("Config file "
					+ config.getAbsolutePath()
					+ " does not exist or cannot be read.");
		}
		LOGGER.debug("Parsing config file: " + config.getAbsolutePath());

		KettleTestCaseConfiguration.Builder builder = null;
		SAXBuilder saxBuilder = new SAXBuilder();

		Document doc = null;
		try {
			doc = saxBuilder.build(config);
		} catch (JDOMException e) {
			throw new IllegalStateException(
					"Failure to parse XML configuration file: "
							+ e.getMessage());
		} catch (IOException e) {
			throw new IllegalStateException(
					"I/O error reading XML configuration file: "
							+ e.getMessage());
		}
		Element root = doc.getRootElement();

		List<Element> tests = root.getChildren("Test");
		LOGGER.debug("Num tests found: " + tests.size());
		for (Element test : tests) {
			LOGGER.debug("Set Config: Test = " + test.getAttributeValue("name"));

			// executable Pentaho artifact
			LOGGER.debug("Set Config: Executable = "
					+ test.getChild("Executable").getText());
			builder = new KettleTestCaseConfiguration.Builder(new File(test
					.getChild("Executable").getText()));

			// test data input files
			Element testData = test.getChild("TestData");
			List<Element> inputFiles = testData.getChildren("InputFile");
			for (Element inputFile : inputFiles) {
				LOGGER.debug("Set Config: Input Test data file = "
						+ inputFile.getText());
				builder.addInFile(new File(inputFile.getText()));
			}

			// expected result files
			Element expectedResults = test.getChild("ExpectedResults");
			List<Element> outputFiles = expectedResults
					.getChildren("OutputFile");
			for (Element outputFile : outputFiles) {
				LOGGER.debug("Set Config: Expected Results data file = "
						+ outputFile.getText());
				builder.addOutFile(new File(outputFile.getText()));
			}

			// target tables
			Element targetTables = test.getChild("TargetTables");
			List<Element> targets = targetTables.getChildren("TargetTable");
			for (Element targetTable : targets) {
				LOGGER.debug("Set Config: Target Table = "
						+ targetTable.getAttributeValue("name"));
				builder.addTargetTable(targetTable.getAttributeValue("name"));

				// order bys for each table
				List<Element> orderBys = targetTable.getChildren("OrderBy");
				for (Element orderBy : orderBys) {
					LOGGER.debug("Set Config: \tOrder By =  "
							+ orderBy.getText() + " on table "
							+ targetTable.getAttributeValue("name"));

					builder.addOrderBy(targetTable.getAttributeValue("name"),
							orderBy.getText());
				}
			}

			// parameters
			Element params = test.getChild("Parameters");
			List<Element> parameters = params.getChildren("Param");
			for (Element parameter : parameters) {
				LOGGER.debug("Set Config: Parameter = "
						+ parameter.getAttributeValue("name") + " with value "
						+ parameter.getText());
				builder.addParameter(parameter.getAttributeValue("name"),
						parameter.getText());
			}

		}
		configuration = builder.build();
		isValid = true; // the build will have thrown an IllegalStateException
						// if not valid
		LOGGER.info("Loaded XML configuration: " + configuration.toString());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer("ExecuteKettleArtifactMode: ");
		buf.append(configuration.getExecutableFile().getName());
		return buf.toString();
	}
}
