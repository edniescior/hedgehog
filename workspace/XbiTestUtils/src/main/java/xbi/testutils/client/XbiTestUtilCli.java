package xbi.testutils.client;

import java.io.File;

import org.apache.xalan.xsltc.cmdline.getopt.GetOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XbiTestUtilCli {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(XbiTestUtilCli.class);

	private static final TestCaseRunner runner = TestCaseRunner.INSTANCE;

	public XbiTestUtilCli() {

	}

	public static void printUsage() {
		String exe = "java -cp .:./XbiTestUtils.jar:$KETTLE_INSTALL/lib/* xbi.testutils.client.XbiTestUtilCli";
		StringBuffer usage = new StringBuffer();
		usage.append("\nUsage: " + exe + " <options>\n");
		usage.append("\n");
		usage.append("This utility provides a method to run JUnit tests against Kettle artifacts.\n");
		usage.append("\n");
		usage.append("The artifact to test, along with input and output test files, are passed in via an XML configuration file \n");
		usage.append("(see sample below). The input data will be loaded during set up. The artifact will then be run.\n");
		usage.append("Finally, the output produced by running the artifact will be compared to the output test\n");
		usage.append("data. The results are printed to STDOUT.\n");
		usage.append("\n");
		usage.append("Expected results can be either combined into a single XML file or split into multiple files. \n");
		usage.append("If multiple files are used then the \n");
		usage.append("number of files must be equal to the number of target tables that \n");
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
		usage.append("  -x XML_CONFIG\t\texecute a test initialised as per this configuration file\n");
		usage.append("  -l INPUT_FILES\tload input XML file(s).  Comma-delimited (no spaces) for multiple files.\n");
		usage.append("  -d DUMP_FILE\t\tdump table contents to XML file. Use with -s flag to define the SQL query set(s).\n");
		usage.append("  -s SQL_QUERY_SET\ttable name and SQL query pairing to execute for dump. The query set must take the form {Table Name:'SQL query'}. \n");
		usage.append("  \t\t\t\tTable Name is the name of the table as it will be output in the file. The table name does not necessarily have to match\n");
		usage.append("  \t\t\t\tthe table name in the query. The query itself must be in single or double quotes. Comma-delimited (no spaces) for multiple query sets.\n");
		usage.append("  \t\t\t\t");
		usage.append("\n");
		usage.append("Examples:\n");
		usage.append("  Loading data from XML:$ " + exe
				+ " -l /Users/xbi/input.xml,/Users/xbi/input2.xml\n");
		usage.append("  Dumping data to XML:\t$ "
				+ exe
				+ " -d /Users/xbi/tableout.xml -s {table_name1:\'select * from some_table_name\'},{table_name2:'select col1, col2 from another_table\'}\n");
		usage.append("  Executing a test:\t$ "
				+ exe
				+ " -x /Users/xbi/testconfig.xml");
		usage.append("\n");
		usage.append("\n");
	
		System.out.println(usage);
		System.exit(0);
	}

	public static void main(String[] args) {
		final GetOpt getopt = new GetOpt(args, "hx:l:d:s:");
		if (args.length < 1)
			printUsage();

		int c;
		StringBuffer optsStr = new StringBuffer("CLI options: ");
		DumpTestDataMode dumpMode = null; // need to keep this around for -s
											// option
		try {
			ROOT: while ((c = getopt.getNextOption()) != -1) {
				String optArg = getopt.getOptionArg();
				switch (c) {
				case 'h':
					optsStr.append("h");
					printUsage();
					break ROOT;
				case 'x': // EXECUTE followed by a Kettle file
					optsStr.append("x");
					ExecuteKettleArtifactMode exeMode = new ExecuteKettleArtifactMode();
					exeMode.loadFromXMLConfig(new File(optArg));
					runner.addMode(exeMode);
					break;
				case 'l': // LOAD followed by a list of input files
					optsStr.append("l");
					LoadTestDataMode loadMode = new LoadTestDataMode();
					loadMode.setInFiles(optArg);
					runner.addMode(loadMode);
					break;
				case 'd': // DUMP followed by an output file
					optsStr.append("d");
					dumpMode = new DumpTestDataMode(new File(optArg));
					runner.addMode(dumpMode);
					break;
				case 's':
					optsStr.append("s");
					if (dumpMode == null) {
						LOGGER.warn("SQL map -s flag must come after -d dump flag. Ignoring this option");
					} else {
						dumpMode.setSqlMap(optArg);
					}
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

		LOGGER.debug(optsStr.toString());

		try {
			runner.run();
		} catch (Exception e) {
			LOGGER.error(
					"Something went wrong trying to run: " + e.getMessage(), e);
		}
		LOGGER.info("Done.");
	}

}
