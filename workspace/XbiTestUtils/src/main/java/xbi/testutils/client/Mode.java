/**
 * 
 */
package xbi.testutils.client;

import java.io.File;

import org.pentaho.di.core.encryption.Encr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xbi.testutils.dbunit.Connector;
import xbi.testutils.kettle.RunnerFactory;

/**
 * The CLI client has different behavior - modes - depending on the CLI
 * parameters.
 * 
 * @author eniesc200
 * 
 */
abstract class Mode {

	private static final Logger LOGGER = LoggerFactory.getLogger(Mode.class);
	
	// connection to the DBUnit DB
	protected Connector connector;

	Mode() {
		init();
	}
	
	private void init() {
		// this will init the kettle environment and check for kettle.properties
		RunnerFactory.createRunner(new File(""));

		// Note that the following system properties are set via the
		// kettle.properties file under KETTLE_HOME
		// Hard-coded to use Oracle, but that could be made configurable.
		String url = "jdbc:oracle:thin:@" + System.getProperty("XBIS_DBNAME");
		String username = System.getProperty("XBIS_STG_USER");
		String password = Encr.decryptPasswordOptionallyEncrypted(System
				.getProperty("XBIS_STG_PASSWORD"));
		String schema = System.getProperty("XBIS_STG_SCHEMA");

		LOGGER.info("Connector: " + username + "@" + url);
		// LOGGER.info(password);
		connector = new Connector(url, username, password, schema);
	}

	/**
	 * Run whatever task this mode dictates.
	 */
	abstract void execute();

	/**
	 * @return true if this mode has been configured appropriately and is ready
	 *         to be executed.
	 */
	abstract boolean isValid();
}
