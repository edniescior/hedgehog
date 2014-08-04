package xbi.testutils.dbunit;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Object to store and validate Kettle test case configuration options.
 * 
 * @author eniesc200
 */
public class KettleTestCaseConfiguration {

	/* the Pentaho executable */
	private final File executableFile;

	/* the input test data files */
	private final List<File> inFiles;

	/* the expected results output files */
	private final List<File> outFiles;

	/* map of target tables, name -> string array of column names for ordering */
	private final Map<String, List<String>> targetTables;

	/* map of parameters, parameter name -> value */
	private final Map<String, String> params;

	/**
	 * Gathers all configuration values and validates before building the
	 * configuration object itself.
	 */
	public static class Builder {
		// required parameter
		private final File executableFile;

		// optional parameters - initialized as empty
		private final List<File> inFiles = new ArrayList<File>();
		private final List<File> outFiles = new ArrayList<File>();
		private final Map<String, List<String>> targetTables = new HashMap<String, List<String>>();
		private final Map<String, String> params = new HashMap<String, String>();

		// store any validation errors
		private List<String> validationErrors = new ArrayList<String>();

		/**
		 * @param executableFile
		 *            the Pentaho executable artifact is required
		 */
		public Builder(File executableFile) {
			if (executableFile == null) {
				throw new NullPointerException();
			}
			this.executableFile = executableFile;
		}

		// setters
		/**
		 * Adds a test data input file to the list. It will ignore anything that
		 * already exists in the list.
		 * 
		 * @param inFile
		 *            the test data input file to add
		 * @return the Builder object
		 */
		public Builder addInFile(File inFile) {
			if (inFile == null) {
				throw new NullPointerException();
			}
			File t = inFile.getAbsoluteFile();
			if (!inFiles.contains(t)) {
				inFiles.add(t);
			}
			return this;
		}

		/**
		 * Adds a test data expected results output file to the list. It will
		 * ignore anything that already exists in the list.
		 * 
		 * @param outFile
		 *            the expected results data output file to add
		 * @return the Builder object
		 */
		public Builder addOutFile(File outFile) {
			if (outFile == null) {
				throw new NullPointerException();
			}
			File t = outFile.getAbsoluteFile();
			if (!outFiles.contains(t)) {
				outFiles.add(t);
			}
			return this;
		}

		/**
		 * Adds a target table for testing against. It will ignore anything that
		 * already exists in the list.
		 * 
		 * @param targetTable
		 *            the table name to add to the list
		 * @return the Builder object
		 */
		public Builder addTargetTable(String targetTable) {
			if (targetTable == null) {
				throw new NullPointerException();
			}
			if (!targetTables.containsKey(targetTable)) {
				this.targetTables.put(targetTable, new ArrayList<String>());
			}
			return this;
		}

		/**
		 * If the final unit test comparison requires sorted data, add each
		 * column name to order by to a given table. The order in which column
		 * names are added dictates the order of the columns in the final order
		 * by clause. The table in question should have been added to the list
		 * of target tables (addTargetTable()) prior to adding order by columns;
		 * if not, the validation step will fail. It will ignore it if the
		 * column already exists in the list.
		 * 
		 * @param targetTable
		 *            the target table to apply the order by to
		 * @param columnName
		 *            the column name to order by
		 * @return the Builder object
		 */
		public Builder addOrderBy(String targetTable, String columnName) {
			if (targetTable == null || columnName == null) {
				throw new NullPointerException();
			}
			if (!targetTables.containsKey(targetTable)) {
				validationErrors
						.add("Cannot order "
								+ targetTable
								+ " by "
								+ columnName
								+ " as "
								+ targetTable
								+ " is not listed as a target table. Call addTargetTable() first.");
			} else {
				if (!targetTables.get(targetTable).contains(columnName)) {
					targetTables.get(targetTable).add(columnName);
				}
			}
			return this;
		}

		/**
		 * Add a parameter that will be passed to the Pentaho job. The value
		 * will be overwritten each time if passed multiple times for a given
		 * parameter name.
		 * 
		 * @param pName
		 *            the parameter name.
		 * @param pValue
		 *            the parameter value. Will be overwritten in the case of
		 *            multiple calls.
		 * @return the Builder object
		 */
		public Builder addParameter(String pName, String pValue) {
			if (pName == null || pValue == null) {
				throw new NullPointerException();
			}
			params.put(pName, pValue);
			return this;
		}

		/**
		 * Initialize/populate a configuration object and validate it.
		 * 
		 * @return a validated and initialized configuration object
		 * @throws IllegalStateException
		 *             if any of the configuration parameters failed validation.
		 */
		public KettleTestCaseConfiguration build() throws IllegalStateException {
			if (validationErrors.size() > 0) {
				StringBuffer b = new StringBuffer(
						"KettleTestCaseConfiguration validation errors found: ");
				for (int i = 0; i < validationErrors.size(); i++) {
					b.append("(" + (i + 1) + ") " + validationErrors.get(i)
							+ "; ");
				}
				throw new IllegalStateException(b.toString());
			}
			return new KettleTestCaseConfiguration(this);
		}
	}

	private KettleTestCaseConfiguration(Builder builder) {
		executableFile = builder.executableFile;
		inFiles = builder.inFiles;
		outFiles = builder.outFiles;
		targetTables = builder.targetTables;
		params = builder.params;
	}

	/**
	 * @return the executableFile
	 */
	public File getExecutableFile() {
		return executableFile;
	}

	/**
	 * @return a list of test data input files
	 */
	public List<File> getInFiles() {
		List<File> copy = new ArrayList<File>();
		for (File f : inFiles) {
			copy.add(f.getAbsoluteFile());
		}
		return copy;
	}

	/**
	 * @return the outFiles
	 */
	public List<File> getOutFiles() {
		List<File> copy = new ArrayList<File>();
		for (File f : outFiles) {
			copy.add(f.getAbsoluteFile());
		}
		return copy;
	}

	/**
	 * @return a map of target tables with an array of column names to order by
	 */
	public Map<String, String[]> getTargetTables() {
		Map<String, String[]> copy = new HashMap<String, String[]>();
		for (Map.Entry<String, List<String>> entry : targetTables.entrySet()) {
			String[] cpAry = entry.getValue().toArray(
					new String[entry.getValue().size()]);
			copy.put(entry.getKey(), cpAry);
		}
		return copy;
	}

	/**
	 * @return a map of parameter names and values
	 */
	public Map<String, String> getParams() {
		Map<String, String> copy = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			copy.put(entry.getKey(), entry.getValue());
		}
		return copy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer b = new StringBuffer("KettleTestCaseConfiguration: ");
		b.append("executable=");
		b.append(getExecutableFile().getAbsolutePath());
		b.append("; infiles=");
		for (File i : getInFiles()) {
			b.append(i.getAbsolutePath());
			b.append(",");
		}
		b.append("; outFiles=");
		for (File o : getOutFiles()) {
			b.append(o.getAbsolutePath());
			b.append(",");
		}
		b.append("; targetTables=");
		for (Map.Entry<String, List<String>> entry : targetTables.entrySet()) {
			b.append(entry.getKey());
			b.append("[");
			for (String s : entry.getValue()) {
				b.append(s);
				b.append(",");
			}
			b.append("]");
		}
		b.append("; parameters=");
		for (Map.Entry<String, String> prms : params.entrySet()) {
			b.append(prms.getKey());
			b.append("==");
			b.append(prms.getValue());
			b.append(",");
		}
		return b.toString();
	}
}
