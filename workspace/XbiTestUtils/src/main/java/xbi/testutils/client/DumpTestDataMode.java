/**
 * 
 */
package xbi.testutils.client;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This mode will dump data into in a single DBUnit format XML data file. What
 * to dump depends on the SQL queries passed in. Multiple queries (and tables)
 * can be dumped into a single output file.
 * 
 * @author eniesc200
 */
class DumpTestDataMode extends Mode {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DumpTestDataMode.class);

	private boolean isValid = true;

	// the output dump file. there can be only one.
	private File dumpFile = null;

	// a map of table names to queries
	private Map<String, String> sqlMap = new HashMap<String, String>();

	// regex for parsing parameter formats that use {:} formatting
	private final static String CURLY_BRACE_FORMAT_REGEX = "\\{([\\w:\\s,*.]+)\\}";

	/**
	 * @param outputFile
	 *            the file to dump the output data to.
	 */
	DumpTestDataMode(File outputFile) {
		super();
		if (outputFile == null) {
			throw new NullPointerException();
		}
		this.dumpFile = outputFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xbi.testutils.client.Mode#execute()
	 */
	@Override
	void execute() {
		/*
		 * For dumps there will only be one output file to write to
		 */
		if (dumpFile.exists()) {
			LOGGER.info("Warning: " + dumpFile.getAbsolutePath()
					+ " is about to be overwritten.");
			dumpFile.delete(); // get rid of the existing one to be sure
		}

		try {
			LOGGER.info("Creating " + dumpFile.getAbsolutePath());
			super.connector.dumpXml(this.sqlMap, dumpFile.getAbsolutePath());
		} catch (Exception e) {
			LOGGER.error("Failed to create dump file: " + e.getMessage());
		}

		if (dumpFile.exists()) {
			LOGGER.info("Created " + dumpFile.getAbsolutePath());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xbi.testutils.client.Mode#isValid()
	 */
	@Override
	boolean isValid() {
		if (sqlMap.size() == 0) {
			LOGGER.warn("Nothing to query. Call setSqlMap() first.");
			isValid = false;
		}
		return isValid;
	}

	/**
	 * Helper method to parse a dump query string
	 * 
	 * @param sqlMapStr
	 *            Table name and SQL query pairing to execute for dump. The
	 *            query set must take the form {Table Name:'SQL query'}. Table
	 *            Name is the name of the table as it will be output in the
	 *            file. The table name does not necessarily have to match the
	 *            table name in the query. The query itself must be in single or
	 *            double quotes. Comma-delimited (no spaces) for multiple query
	 *            sets.
	 * 
	 *            Example: {table_name1:\'select * from
	 *            some_table_name\'},{table_name2:'select col1, col2 from
	 *            another_table\'}
	 */
	void setSqlMap(String sqlMapStr) {
		if (sqlMapStr == null) {
			throw new NullPointerException();
		}
		Pattern pattern = Pattern.compile(CURLY_BRACE_FORMAT_REGEX,
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

	/*
	 * Add each query to the sql map
	 */
	private void addSqlMapEntry(String tableName, String query) {
		LOGGER.info("Adding SQL mapping for table \'" + tableName
				+ "\' with query \'" + query + "\'");
		sqlMap.put(tableName, query);
	}
	
	/**
	 * Get the current SQL map.
	 * @return the current SQL map.
	 */
	Map<String, String> getSqlMap() {
		Map<String, String> copy = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : sqlMap.entrySet()) {
			copy.put(entry.getKey(), entry.getValue());
		}
		return copy;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer("DumpTestDataMode: Dumping to ");
		buf.append(dumpFile.getAbsolutePath());
		return buf.toString();
	}
}
