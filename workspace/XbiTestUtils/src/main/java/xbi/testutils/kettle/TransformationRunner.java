package xbi.testutils.kettle;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleMissingPluginsException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformationRunner extends Runner {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TransformationRunner.class);

	private Trans trans;

	public TransformationRunner(File xmlFile) {
		super(xmlFile);
	}

	@Override
	public void setup() {
		File file = getXmlFile();
		String fileName = file.getAbsolutePath();
		/* Initialize the transformation */
		TransMeta transMeta = new TransMeta();
		try {
			transMeta = new TransMeta(fileName);
		} catch (KettleMissingPluginsException p) {
			LOGGER.error(
					"Missing plugins. Unable to create TransMeta object from XML file "
							+ fileName + ": " + p, p);
			return;
		} catch (KettleXMLException e) {
			LOGGER.error("Unable to create TransMeta object from XML file "
					+ fileName + ": " + e, e);
			return;
		}

		trans = new Trans(transMeta);
		trans.initializeVariablesFrom(null);
		trans.getTransMeta().setInternalKettleVariables(trans);
		trans.setSafeModeEnabled(true);
		setState(Status.READY, true);
	}

	@Override
	public void verify() {
		setState(Status.VERIFIED, true);

		List<CheckResultInterface> stepRemarks = getStepRemarks();
		trans.getTransMeta().checkSteps(stepRemarks, false, null);
		for (CheckResultInterface remark : stepRemarks) {
			if (remark.getType() == CheckResultInterface.TYPE_RESULT_ERROR) {
				setState(Status.VERIFIED, false);
				LOGGER.error(remark.toString());
			}
		}
	}

	@Override
	public void run() {
		if (!isReady() || !isVerified() || isComplete()) {
			LOGGER.warn("Aborting run.  Ready? " + isReady() + ". Verified? "
					+ isVerified() + ". Complete? " + isComplete());
			return;
		}

		// allocate & run the required sub-threads
		try {
			trans.execute(null);
			trans.waitUntilFinished();
			trans.stopAll();
			setState(Status.COMPLETE, true);
		} catch (KettleException e) {
			LOGGER.error(
					"Encountered error running " + getXmlFile() + ": " + e, e);
		}
	}

	@Override
	public void rerun(String property, String propertyValue) {
		setState(Status.READY, false);
		setState(Status.COMPLETE, false);
		setup(); // force the transaction to close by setting up a new
					// transformation
		setParameterValue(property, propertyValue);
		run();
	}

	@Override
	public void setParameterValue(String property, String propertyValue) {
		try {
			trans.setParameterValue(property, propertyValue);
		} catch (UnknownParamException e) {
			LOGGER.error("Unknown property, transformation will not run: "
					+ e.getMessage());
			setState(Status.READY, false);
		}
	}

	@Override
	public Set<String> getParameterNames() {
		return createSet((trans != null) ? trans.listParameters() : null);
	}

}
