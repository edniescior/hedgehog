package xbi.testutils.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to parse, store and validate the CLI options passed.
 */
public class Configurables {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Configurables.class);

	public static enum Mode {
		EXE, LOAD, DUMP, NONE;
	}

	private Mode mode = Mode.NONE;
	private File xmlFile = null;
	private List<File> inFiles = new ArrayList<File>();
	private List<File> outFiles = new ArrayList<File>();
	private List<String> targetTables = new ArrayList<String>();
	private String sql = null;

	public Mode getMode() {
		return mode;
	}

	/**
	 * Set the execution mode.
	 * 
	 * @param m
	 *            the execution mode flag - l, x or d
	 * @throws IllegalStateException
	 *             if more than one execution mode flag is passed on the CLI
	 */
	public void setMode(char m) {
		if (this.mode != Mode.NONE) {
			throw new IllegalStateException("setMode called more than once: "
					+ m);
		}
		switch (m) {
		case 'x':
			this.mode = Mode.EXE;
			break;
		case 'l':
			this.mode = Mode.LOAD;
			break;
		case 'd':
			this.mode = Mode.DUMP;
			break;
		default:
			this.mode = Mode.NONE;
		}
	}

	public File getXmlFile() {
		return xmlFile;
	}

	public void setXmlFile(File xmlFile) {
		this.xmlFile = xmlFile;
	}

	public List<File> getInFiles() {
		return inFiles;
	}

	public void setInFiles(String inFilesStr) {
		String[] result = inFilesStr.split(",");
		for (int x = 0; x < result.length; x++)
			this.inFiles.add(new File(result[x]));
	}

	public List<File> getOutFiles() {
		return outFiles;
	}

	public void setOutFiles(String outFilesStr) {
		String[] result = outFilesStr.split(",");
		for (int x = 0; x < result.length; x++)
			this.outFiles.add(new File(result[x]));
	}

	public List<String> getTargetTables() {
		return targetTables;
	}

	public void setTargetTables(String targetTablesStr) {
		String[] result = targetTablesStr.split(",");
		for (int x = 0; x < result.length; x++)
			this.targetTables.add(result[x]);
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	/**
	 * Make sure we have all the right parameters for the given mode.
	 * 
	 * @throws IllegalStateException
	 *             if this thing is not configured correctly
	 */
	public void validate() {
		StringBuffer buffer = new StringBuffer();
		// null mode
		if (this.mode == Mode.NONE) {
			buffer.append("No mode set. You must set a mode (-x (EXECUTE), -l (LOAD) or -d (DUMP)). ");
		}
		// load mode
		else if (this.mode == Mode.LOAD)
			// are there input files and do they exist?
			if (getInFiles().isEmpty()) {
				buffer.append("No input files passed in for LOAD. Use the -i flag to provide input files. ");
			} else {
				for (File f : getInFiles()) {
					if (!(f.exists() && f.canRead())) {
						buffer.append("The input file " + f.getAbsolutePath()
								+ " does not exist or is not readable. ");
					}
				}
			}
		// dump mode
		else if (this.mode == Mode.DUMP) {
			// are there output files provided
			if (getOutFiles().isEmpty()) {
				buffer.append("No output files passed in for DUMP. Use the -o flag to provide output files. ");
			}
			// is there a table name provided
			if (getTargetTables().isEmpty()) {
				buffer.append("No target table names passed in for DUMP. Use the -t flag to provide table names. ");
			}
			// is there a query provided
			if (getSql() == null) {
				buffer.append("No SQL query passed in for DUMP. Use the -s flag to provide a query. ");
			}
		}
		// execute mode
		else if (this.mode == Mode.EXE) {
			// is there a valid readable KJB/KTR
			if (!(getXmlFile().exists() && getXmlFile().canRead())) {
				buffer.append("The XML file " + getXmlFile().getAbsolutePath()
						+ " does not exist or is not readable. ");
			}
			// are there input files and do they exist
			if (getInFiles().isEmpty()) {
				buffer.append("No input files passed in for EXECUTE. Use the -i flag to provide input files. ");
			} else {
				for (File f : getInFiles()) {
					if (!(f.exists() && f.canRead())) {
						buffer.append("The input file " + f.getAbsolutePath()
								+ " does not exist or is not readable. ");
					}
				}
			}
			// are there expected output files and do they exist
			if (getOutFiles().isEmpty()) {
				buffer.append("No expected output files passed in for EXECUTE. Use the -o flag to provide expected output files. ");
			} else {
				for (File f : getOutFiles()) {
					if (!(f.exists() && f.canRead())) {
						buffer.append("The expected output file " + f.getAbsolutePath()
								+ " does not exist or is not readable. ");
					}
				}
			}
			// are there target tables provided
			if (getTargetTables().isEmpty()) {
				buffer.append("No target table names passed in for EXECUTE. Use the -t flag to provide table names. ");
			}
		}

		if (buffer.length() > 0) {
			throw new IllegalStateException(buffer.toString());
		}
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("Configurables ");
		buf.append(":mode=" + getMode());
		if (getXmlFile() != null) {
			buf.append(":xmlFile=" + getXmlFile().getAbsolutePath());
		}
		if (!getInFiles().isEmpty()) {
			buf.append(":inFiles=");
			for (File inFile : getInFiles()) {
				buf.append("," + inFile.getAbsolutePath());
			}
		}
		if (!getOutFiles().isEmpty()) {
			buf.append(":outFiles=");
			for (File outFile : getOutFiles()) {
				buf.append("," + outFile.getAbsolutePath());
			}
		}
		if (!getTargetTables().isEmpty()) {
			buf.append(":targetTables=");
			for (String targetTable : getTargetTables()) {
				buf.append("," + targetTable.toString());
			}
		}
		if (getSql() != null) {
			buf.append(":sql=" + getSql());
		}
		return buf.toString();

	}
}
