package xbi.testutils.client;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private Map<String, String> sqlMap = new HashMap<String, String>();

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
		for (int x = 0; x < result.length; x++) {
			String fName = result[x].trim();
			this.inFiles.add(new File(fName));
		}
	}

	public List<File> getOutFiles() {
		return outFiles;
	}

	public void setOutFiles(String outFilesStr) {
		String[] result = outFilesStr.split(",");
		for (int x = 0; x < result.length; x++) {
			String fName = result[x].trim();
			this.outFiles.add(new File(fName));
		}
	}

	public List<String> getTargetTables() {
		return targetTables;
	}

	public void setTargetTables(String targetTablesStr) {
		String[] result = targetTablesStr.split(",");
		for (int x = 0; x < result.length; x++) {
			String tName = result[x].trim();
			this.targetTables.add(tName);
		}
	}

	public Map<String, String> getSqlMap() {
		return sqlMap;
	}

	public void setSqlMap(String sqlMapStr) {
		Pattern pattern = Pattern.compile("\\{([\\w:\\s,*.]+)\\}",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(sqlMapStr);

		// pull all occurrences of the pattern
		int mappingCount = 0;
		while (matcher.find()) {
			String thisMapping = matcher.group(1);
			String[] result = thisMapping.split(":");
			if (result.length == 2) {
				addSqlMapEntry(result[0], result[1]);
				mappingCount++;
			}
		}

		if (mappingCount < 1) {
			LOGGER.warn("No SQL mappings were parsed out of SQL map string \""
					+ sqlMapStr + "\". Check the syntax.");
		}
	}

	public void addSqlMapEntry(String tableName, String query) {
		LOGGER.info("Adding SQL mapping for table \'" + tableName
				+ "\' with query \'" + query + "\'");
		sqlMap.put(tableName, query);
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
			// is there a query set provided
			if (getSqlMap().isEmpty()) {
				buffer.append("No SQL query set passed in for DUMP. Use the -s flag to provide a query set. ");
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
						buffer.append("The expected output file "
								+ f.getAbsolutePath()
								+ " does not exist or is not readable. ");
					}
				}
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
		if (!getSqlMap().isEmpty()) {
			StringBuffer map = new StringBuffer(":sqlMap=");
			for (Map.Entry<String, String> entry : getSqlMap().entrySet()) {
				map.append("[" + entry.getKey() + ":" + entry.getValue() + "]");
			}
			buf.append(map.toString());
		}
		return buf.toString();

	}
}
