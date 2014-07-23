package xbi.testutils.client;

import java.io.File;

import org.apache.xalan.xsltc.cmdline.getopt.GetOpt;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.pentaho.di.core.encryption.Encr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xbi.testutils.dbunit.ConfigurableKettleTestCase;
import xbi.testutils.dbunit.Connector;
import xbi.testutils.kettle.RunnerFactory;

public class XbiTestUtilCli {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(XbiTestUtilCli.class);

	// CLI options for this run.
	private Configurables configurables;
	
	public XbiTestUtilCli() {
		configurables = new Configurables();
	}

	/**
	 * Execute what needs to be run. What actually runs will depend on the mode.
	 */
	void run() throws Exception {

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
		Connector connector = new Connector(url, username, password, schema);

		// dump table(s) contents to XML file
		if (this.configurables.getMode() == Configurables.Mode.DUMP) {
			/*
			 * For dumps there will only be one output file to write to,
			 * so pop index 0.
			 */
			File outfile = this.configurables.getOutFiles().get(0);
			if (outfile.exists()) {
				LOGGER.info("Warning: " + outfile.getAbsolutePath()
						+ " is about to be overwritten.");
				outfile.delete(); // get rid of the existing one to be sure
			}
			
			connector.dumpXml(this.configurables.getSqlMap(), outfile.getAbsolutePath());
			
			if (outfile.exists()) {
				LOGGER.info("Created " + outfile.getAbsolutePath());
			}
		} else if (this.configurables.getMode() == Configurables.Mode.LOAD) {
			for (File infile : this.configurables.getInFiles()) {
				LOGGER.info("Loading " + infile.getAbsolutePath());
				connector.loadDataSet(infile);
			}
		} else if (this.configurables.getMode() == Configurables.Mode.EXE) {
			ConfigurableKettleTestCase.addConfigurable(this.configurables);
			Result result = JUnitCore.runClasses(ConfigurableKettleTestCase.class);
			for (Failure failure : result.getFailures()) {
				LOGGER.error(failure.toString());
			}
			LOGGER.info("Test success?  " + result.wasSuccessful());
		}
	}

	public static void printUsage() {
		String exe = "java -cp .:./XbiTestUtils-{version}.jar:$KETTLE_INSTALL/lib/* xbi.testutils.client.XbiTestUtilCli";
		StringBuffer usage = new StringBuffer();
		usage.append("\nUsage: " + exe + " <options>\n");
		usage.append("\n");
		usage.append("This utility provides a method to run JUnit tests against Kettle artifacts.\n");
		usage.append("The artifact, along with input and output test files, are passed in on the command line.\n");
		usage.append("The input data will be loaded during set up. The artifact will then be run.\n");
		usage.append("Finally, the output produced by running the artifact will be compared to the output test\n");
		usage.append("data passed in on the command line. The results are printed to STDOUT.\n");
		usage.append("\n");
		usage.append("Expected results can be either combined into a single XML file or split into multiple files. \n");
		usage.append("If multiple files are used (passed in as a comma-delimited list with the -o flag) then the \n");
		usage.append("number of files must be equal to the number of target tables (passed in with the -t flag) that \n");
		usage.append("you are testing against. Typically, it is easier to combine them into a single file. \n");
		usage.append("\n");
		usage.append("The utility also provides methods to a) load input data (i.e. just execute the set up)\n");
		usage.append("and b) write out data from database tables to XML format.\n");
		usage.append("\n");
		usage.append("Warning: When loading multiple files be sure that there is no overlap in table names.\n");
		usage.append("DBUnit executes a truncate for each table it finds in a data file. For example, if you have .\n");
		usage.append("data for TABLE_A in both FILE_A and FILE_B, FILE_A.TABLE_A data will be deleted \n");
		usage.append("when FILE_B is loaded! \n");
		usage.append("\n");
		usage.append("Notes: \n");
		usage.append("\n");
		usage.append("(1) This utility relies on kettle.properties in KETTLE_HOME for configuration. It uses the \n");
		usage.append("following entries to connect to the DB: XBIS_DBNAME, XBIS_STG_USER, XBIS_STG_PASSWORD \n");
		usage.append("(encrypted) and XBIS_STG_SCHEMA. At this point, it is hard-coded to only work with Oracle.\n");
		usage.append("\n");
		usage.append("(2) This utility relies on your local Kettle install libraries. Hence, the environment variable \n");
		usage.append("KETTLE_INSTALL must be set in your local environment. See Confluence for more details.  \n");
		usage.append("\n");
		usage.append("Options:\n");
		usage.append("  -h\t\t\tshow this help message and exit\n");
		usage.append("  -x XML_FILE\t\texecute the Kettle artifact XML_FILE. Use with -i, -o and (optionally) -t flags\n");
		usage.append("  -i IN_FILES\t\tpath to the input (set-up test data) XML file(s). Comma-delimited (no spaces) for multiple files\n");
		usage.append("  -o OUT_FILES\t\tpath to the output (expected results) XML file(s). Comma-delimited (no spaces) for multiple files\n");
		usage.append("  -t TARGET_TABLES\t(Optional) target table(s) to compare expected results against. Table(s) will be truncated after each test.\n");
		usage.append("  \t\t\t\tEach entry for a table should take the form {Table Name:Column Name 1, Column Name2} if ordering is required;\n");
		usage.append("  \t\t\t\tOtherwise, just {Table Name:} if ordering is NOT required. (DBUnit will use 'natural' ordering in this case.)\n");
		usage.append("  \t\t\t\tComma-delimited (no spaces) for multiple tables.\n");
		usage.append("  -p PARAMETERS\t(Optional) parameters to pass to the transformation in the form paramName=paramValue.\n");
		usage.append("  \t\t\t\tComma-delimited (no spaces) for multiple parameters.\n");
		usage.append("  -l\t\t\tload input XML file(s).  Use with -i flag to define input files\n");
		usage.append("  -d DUMP_FILE\t\tdump table contents to XML file. Use with -s flag to define the SQL query set(s).\n");
		usage.append("  -s SQL_QUERY_SET\tTable name and SQL query pairing to execute for dump. The query set must take the form {Table Name:'SQL query'}. \n");
		usage.append("  \t\t\t\tTable Name is the name of the table as it will be output in the file. The table name does not necessarily have to match\n");
		usage.append("  \t\t\t\tthe table name in the query. The query itself must be in single or double quotes. Comma-delimited (no spaces) for multiple query sets.\n");
		usage.append("  \t\t\t\t");
		usage.append("\n");
		usage.append("Examples:\n");
		usage.append("  Loading data from XML:$ " + exe
				+ " -l -i /Users/xbi/input.xml,/Users/xbi/input2.xml\n");
		usage.append("  Dumping data to XML:\t$ "
				+ exe
				+ " -d /Users/xbi/output.xml -s {table_name1:\'select * from some_table_name\'},{table_name2:'select col1, col2 from another_table\'}\n");
		usage.append("  Executing a test:\t$ "
				+ exe
				+ " -x /Users/xbi/test.ktr -i /Users/xbi/input.xml -o /Users/xbi/output.xml -t {target_table1:col1,col2},{target_table2:} -p foo=bar");
		usage.append("\n");
		System.out.println(usage);
		System.exit(0);
	}

	public static void main(String[] args) {
		final GetOpt getopt = new GetOpt(args, "hx:ld:i:o:t:s:p:");
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
					client.configurables.setOutFiles(optArg); // piggy-back on outfiles for the dump
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
					client.configurables.setSqlMap(optArg);
					break;
				case 'p':
					optsStr.append("p");
					client.configurables.setParamsMap(optArg);
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

		try {
			client.configurables.validate();
			client.run();
		} catch (Exception e) {
			LOGGER.error(
					"Something went wrong trying to run: " + e.getMessage(), e);
		}
		LOGGER.info("Done.");
	}

}
