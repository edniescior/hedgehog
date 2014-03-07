package xbi.testutils.kettle;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Runner {

	private static final Logger LOGGER = LoggerFactory.getLogger(Runner.class);

	/**
	 * The name of the system property identifying the Kettle home directory.
	 */
	public static final String PROP_KETTLE_HOME = "KETTLE_HOME";

	// Instance vars
	private File xmlFile;
	private final List<CheckResultInterface> stepRemarks = new ArrayList<CheckResultInterface>();
	private Map<Status, Boolean> stateMap = new HashMap<Status, Boolean>(
			Status.values().length);

	/**
	 * READY indicates that the Job or Transformation has been setup and the
	 * environment initialized. VERIFIED indicates that the Job or
	 * Transformation has passed Kettle builtin verification. COMPLETE indicates
	 * that the Job or Transformation has run to completion without error.
	 */
	protected enum Status {
		READY, VERIFIED, COMPLETE
	}

	public Runner(File xmlFile) {
		setKettleHome();
		setXmlFile(xmlFile);
		setState(Status.READY, false);
		setState(Status.VERIFIED, false);
		setState(Status.COMPLETE, false);

		File kettlePropertiesFile = new File(Const.getKettleDirectory()
				+ Const.FILE_SEPARATOR + Const.KETTLE_PROPERTIES);

		if (!kettlePropertiesFile.exists()) {
			throw new RuntimeException(
					"Unable to find kettle.properties.  Please run Ant first and set "
							+ PROP_KETTLE_HOME
							+ " Java system property to the parent directory of the created .kettle directory.");
		}

		try {
			KettleEnvironment.init();
		} catch (KettleException e) {
			LOGGER.error(e.getMessage());
		}
	}

	// For running in IDEs - check to see if KETTLE_HOME system property is set,
	// if not set it programmatically.
	private void setKettleHome() {
		if (System.getProperty(PROP_KETTLE_HOME) == null) {
			try {
				String propFile = System.getProperty("user.home")
						+ "/.kettle/kettle.properties";
				// File kettlePropertiesFile = new
				// File(Runner.class.getResource(propFile).toURI());
				File kettlePropertiesFile = new File(propFile);
				String kettleHome = kettlePropertiesFile.getParentFile()
						.getParent();
				System.setProperty(PROP_KETTLE_HOME, kettleHome);
			} catch (Exception e) {
				// The original code just caught URISyntaxException but would
				// throw NullPointerException
				// if KETTLE_HOME was not defined in a standalone (non-IDE)
				// environment. Better to catch
				// all exceptions and also log the full stack trace.

				LOGGER.warn(
						PROP_KETTLE_HOME
								+ " not defined, caught error trying to set programmatically: "
								+ e, e);
			}
		}
	}

	/**
	 * @return The Job or Transformation file
	 */
	public File getXmlFile() {
		return xmlFile;
	}

	/**
	 * @param xmlFile
	 *            The Job or Transformation file
	 */
	public void setXmlFile(File xmlFile) {
		this.xmlFile = xmlFile;
	}

	/**
	 * @return The List of CheckResultInterface populated by verify().
	 */
	@Nonnull
	public List<CheckResultInterface> getStepRemarks() {
		return stepRemarks;
	}

	/**
	 * Subclasses call this to set the state for any given status.
	 * 
	 * @param status
	 *            the status to set.
	 * @param state
	 */
	protected void setState(@Nonnull Status status, boolean state) {
		stateMap.put(status, state);
	}

	/**
	 * @return true if the setup method has completed successfully, false
	 *         otherwise.
	 */
	protected boolean isReady() {
		return stateMap.get(Status.READY);
	}

	/**
	 * @return true if the Job or Transformation has passed Kettle verification,
	 *         false otherwise.
	 */
	public boolean isVerified() {
		return stateMap.get(Status.VERIFIED);
	}

	/**
	 * @return true if the Job or Transformation has run to completion
	 *         successfully, false otherwise.
	 */
	public boolean isComplete() {
		return stateMap.get(Status.COMPLETE);
	}

	/**
	 * Subclasses should implement this so that it sets up the Job or
	 * Transformation to be run. This method should be called prior to verify()
	 * or run(). Test success of this step with isReady().
	 */
	public abstract void setup();

	/**
	 * Subclasses should implement this so that it verifies the Job or
	 * Transformation to be run. This method should be called prior to run();
	 * Test success of this step with isVerified().
	 */
	public abstract void verify();

	/**
	 * Subclasses should implement this so that it runs the Job or
	 * Transformation. Test success of this step with isComplete().
	 */
	public abstract void run();

	/**
	 * Subclasses should implement this so that it reruns the Job or
	 * Transformation with the new parameter. Test success of this step with
	 * isComplete().
	 * 
	 * @param property
	 * @param propertyValue
	 */
	public abstract void rerun(String property, String propertyValue);

	/**
	 * Set a parameter in the Job or Transformation. If the parameter is
	 * unknown, the Runner will be in an unready state.
	 * 
	 * @param property
	 * @param propertyValue
	 */
	public abstract void setParameterValue(String property, String propertyValue);

	/**
	 * Subclasses can
	 * 
	 * @return a non-null set of parameter names that exist on this Kettle
	 *         thread (job, transformation).
	 */
	@Nonnull
	public abstract Set<String> getParameterNames();

	/**
	 * Helper method for {@link #getParameterNames()} implementations.
	 * 
	 * @param items
	 *            items to add to a set.
	 * 
	 * @return a non-null set containing the non-null items from the array.
	 */
	@Nonnull
	protected Set<String> createSet(@CheckForNull String[] items) {
		Set<String> set = new HashSet<String>();

		if (items != null) {
			for (String item : items) {
				if (item != null) {
					set.add(item);
				}
			}
		}

		return set;
	}

}
