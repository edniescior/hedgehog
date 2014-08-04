/**
 * 
 */
package xbi.testutils.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This mode will load in DBUnit test data files.
 * 
 * @author eniesc200
 */
class LoadTestDataMode extends Mode {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(LoadTestDataMode.class);

	private boolean isValid = true;

	// list of input data files to be loaded
	private List<File> inFiles = new ArrayList<File>();

	/**
	 * 
	 */
	LoadTestDataMode() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xbi.testutils.client.Mode#execute()
	 */
	@Override
	void execute() {
		for (File f : this.inFiles) {
			LOGGER.info("Loading " + f.getAbsolutePath());
			if (f.exists() && f.isFile() && f.canRead()) {
				super.connector.loadDataSet(f);
			} else {
				LOGGER.error(f.getAbsolutePath()
						+ " is not a file or cannot be read.");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xbi.testutils.client.Mode#isValid()
	 */
	@Override
	boolean isValid() {
		if (inFiles.size() == 0) {
			LOGGER.warn("Nothing to load. Call setInFiles() first.");
			isValid = false;
		}
		return isValid;
	}

	/**
	 * Helper method to parse a list of test data files (comma-separated)
	 * 
	 * @param inFilesStr
	 *            comma-separated list of test data files
	 */
	void setInFiles(String inFilesStr) {
		String[] result = inFilesStr.split(",");
		for (int x = 0; x < result.length; x++) {
			String fName = result[x].trim();
			this.inFiles.add(new File(fName));
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer("LoadTestDataMode: Loading data files ... ");
		for (int i=0; i<inFiles.size(); i++) {
			buf.append(inFiles.get(i).toString());
			if (i < (inFiles.size() - 1)) {
				buf.append(',');
			}
		}
		return buf.toString();
	}
}
