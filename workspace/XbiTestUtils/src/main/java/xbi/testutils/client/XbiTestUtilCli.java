package xbi.testutils.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.xalan.xsltc.cmdline.getopt.GetOpt;
import org.pentaho.di.core.encryption.Encr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xbi.testutils.dbunit.Connector;
import xbi.testutils.kettle.Runner;
import xbi.testutils.kettle.RunnerFactory;

public class XbiTestUtilCli {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(XbiTestUtilCli.class);

	private static enum Mode {
		EXE, LOAD, DUMP, NONE;
	}

	// CLI options for this run.
	private Configurables configurables = new Configurables();

	/**
	 * Helper class to parse, store and validate the CLI options passed.
	 * 
	 */
	private class Configurables {

		private Mode mode = Mode.NONE;
		private File xmlFile = null;
		private List<File> inFiles = new ArrayList<File>();
		private List<File> outFiles = new ArrayList<File>();
		private List<String> targetTables = new ArrayList<String>();
		private String sql = null;

		Mode getMode() {
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
		void setMode(char m) {
			if (this.mode != Mode.NONE) {
				throw new IllegalStateException(
						"setMode called more than once: " + m);
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

		File getXmlFile() {
			return xmlFile;
		}

		void setXmlFile(File xmlFile) {
			this.xmlFile = xmlFile;
		}

		List<File> getInFiles() {
			return inFiles;
		}

		void setInFiles(String inFilesStr) {
			String[] result = inFilesStr.split(",");
			for (int x = 0; x < result.length; x++)
				this.inFiles.add(new File(result[x]));
		}

		List<File> getOutFiles() {
			return outFiles;
		}

		void setOutFiles(String outFilesStr) {
			String[] result = outFilesStr.split(",");
			for (int x = 0; x < result.length; x++)
				this.outFiles.add(new File(result[x]));
		}

		List<String> getTargetTables() {
			return targetTables;
		}

		void setTargetTables(String targetTablesStr) {
			String[] result = targetTablesStr.split(",");
			for (int x = 0; x < result.length; x++)
				this.targetTables.add(result[x]);
		}

		String getSql() {
			return sql;
		}

		void setSql(String sql) {
			this.sql = sql;
		}

		/**
		 * Make sure we have all the right parameters for the given mode.
		 * 
		 * @throws IllegalStateException
		 *             if this thing is not configured correctly
		 */
		void validate() {

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

	/**
	 * Execute what needs to be run. What actually runs will depend on the mode.
	 */
	void run() throws Exception {

		// this will init the kettle environment and check for kettle.properties
		Runner runner = RunnerFactory.createRunner(new File(""));

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
		Connector connector = new Connector(url, username, password, schema);

		// dump table(s) contents to XML file
		if (this.configurables.getMode() == Mode.DUMP) {
			/*
			 * For now, at least ... for dumps, there will only be one target
			 * table, so pop index 0. the same goes for the output files.
			 */
			String target = this.configurables.getTargetTables().get(0);
			String sql = this.configurables.getSql();
			File outfile = this.configurables.getOutFiles().get(0);
			if (outfile.exists()) {
				LOGGER.info("Warning: " + outfile.getAbsolutePath()
						+ " is about to be overwritten.");
				outfile.delete(); // get rid of the existing one to be sure
			}
			LOGGER.info("Dumping " + target + " as \"" + sql + "\" to "
					+ outfile.getAbsolutePath());
			connector.dumpXml(target, sql, outfile.getAbsolutePath());
			if (outfile.exists()) {
				LOGGER.info("Created " + outfile.getAbsolutePath());
			}
		} else if (this.configurables.getMode() == Mode.LOAD) {
			for (File infile : this.configurables.getInFiles()) {
				LOGGER.info("Loading " + infile.getAbsolutePath());
				connector.loadDataSet(infile);
			}
		}
	}

	public static void printUsage() {
		String exe = "java -jar XbiTestUtils-1.0-SNAPSHOT.jar";
		StringBuffer usage = new StringBuffer();
		usage.append("\nUsage: " + exe + " <options>\n");
		usage.append("\n");
		usage.append("This utility provides a method to run JUnit tests against Kettle artifacts.\n");
		usage.append("The artifact, along with input and output test files, are passed in on the command line.\n");
		usage.append("The input data will be loaded during set up. The artifact will then be run.\n");
		usage.append("Finally, the output produced by running the artifact will be compared to the output test\n");
		usage.append("data passed in on the command line. The results are printed to STDOUT.\n");
		usage.append("\n");
		usage.append("The utility also provides methods to a) load input data (i.e. just execute the set up)\n");
		usage.append("and b) write out data from database tables to XML format.\n");
		usage.append("\n");
		usage.append("Warning: When loading multiple files be sure that there is no overlap in table names.\n");
		usage.append("DBUnit executes a truncate for each table it finds in a data file. For example, if you have .\n");
		usage.append("data for TABLE_A in both FILE_A and FILE_B, FILE_A.TABLE_A data will be deleted \n");
		usage.append("when FILE_B is loaded! \n");
		usage.append("\n");
		usage.append("Note: This utility relies on kettle.properties in KETTLE_HOME for configuration. It uses the \n");
		usage.append("following entries to connect to the DB: XBIS_DBNAME, XBIS_STG_USER, XBIS_STG_PASSWORD \n");
		usage.append("(encrypted) and XBIS_STG_SCHEMA. It is hard-coded to only work with Oracle.\n");
		usage.append("\n");
		usage.append("Options:\n");
		usage.append("  -h\t\t\tshow this help message and exit\n");
		usage.append("  -x XML_FILE\t\texecute the Kettle artifact XML_FILE\n");
		usage.append("  -i IN_FILE\t\tpath to the input (set-up test data) XML file(s). Comma-delimited for multiple files\n");
		usage.append("  -o OUT_FILE\t\tpath to the output (expected results) XML file(s). Comma-delimited for multiple files\n");
		usage.append("  -t TARGET\t\ttarget table(s) to verify expected results. Comma-delimited for multiple files. Will be added to the clean list\n");
		usage.append("  -l\t\t\tload input XML file(s).  Use with -i flag to define input files\n");
		usage.append("  -d\t\t\tdump table contents to XML file. Use with -o flag to define (a single) output file, -t to define target table and -s to define the SQL\n");
		usage.append("  -s SQL\t\tSQL query to execute against target table for dump. It must be in single or double quotes.\n");
		usage.append("\n");
		usage.append("Examples:\n");
		usage.append("  Loading data from XML:$ " + exe
				+ " -l -i /Users/xbi/input.xml,/Users/xbi/input2.xml\n");
		usage.append("  Dumping data to XML:\t$ "
				+ exe
				+ " -d -o /Users/xbi/output.xml -t target_table -s \'Select * from target_table\'\n");
		usage.append("  Executing a test:\t$ "
				+ exe
				+ " -x /Users/xbi/test.ktr -i /Users/xbi/input.xml -o /Users/xbi/output.xml -t target_table1");
		usage.append("\n");
		System.out.println(usage);
		System.exit(0);
	}

	public static void main(String[] args) {
		final GetOpt getopt = new GetOpt(args, "hx:ldi:o:t:s:");
		if (args.length < 1)
			printUsage();

		int c;
		StringBuffer optsStr = new StringBuffer("CLI options: ");
		XbiTestUtilCli client = new XbiTestUtilCli();
		try {
			ROOT: while ((c = getopt.getNextOption()) != -1) {
				String optArg = getopt.getOptionArg();
				switch (c) {
				case 'h':
					optsStr.append("h");
					printUsage();
					break ROOT;
				case 'x':
					optsStr.append("x");
					client.configurables.setMode('x');
					client.configurables.setXmlFile(new File(optArg));
					break;
				case 'l':
					optsStr.append("l");
					client.configurables.setMode('l');
					break;
				case 'd':
					optsStr.append("d");
					client.configurables.setMode('d');
					break;
				case 'i':
					optsStr.append("i");
					client.configurables.setInFiles(optArg);
					break;
				case 'o':
					optsStr.append("o");
					client.configurables.setOutFiles(optArg);
					break;
				case 't':
					optsStr.append("t");
					client.configurables.setTargetTables(optArg);
					break;
				case 's':
					optsStr.append("s");
					client.configurables.setSql(optArg);
					break;
				default:
					optsStr.append("?");
					printUsage();
					break;
				}
				if (optArg != null) {
					optsStr.append("=" + optArg);
				}
				optsStr.append(" ");
			}
		} catch (Exception e) {
			LOGGER.error(
					"Could not parse command line options: " + e.getMessage(),
					e);
			printUsage();
		}

		LOGGER.info(optsStr.toString());
		LOGGER.info(client.configurables.toString());

		client.configurables.validate();

		try {
			client.run();
		} catch (Exception e) {
			LOGGER.error(
					"Something went wrong trying to run: " + e.getMessage(), e);
		}
		LOGGER.info("Done.");
	}

}
