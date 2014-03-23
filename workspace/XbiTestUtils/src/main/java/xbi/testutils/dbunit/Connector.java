package xbi.testutils.dbunit;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Connector.class);

	private IDatabaseTester databaseTester;

	/* wraps a java.sql.Connection object */
	private IDatabaseConnection databaseConnection;
	private IDataSet databaseDataSet;

	/* Preserve order in case there are foreign key constraints to satisfy */
	private Map<String, String> tablesToClean = new LinkedHashMap<String, String>();

	private Map<String, Long> sequencesToReset = new LinkedHashMap<String, Long>();

	public Connector(String url, String username, String password, String schema) {
		try {
			// Could be configuration-driven to support different kinds of
			// databases.
			// Override getConnection() as it insists on using the
			// DefaultConnectionFactory otherwise
			databaseTester = new JdbcDatabaseTester("oracle.jdbc.OracleDriver",
					url, username, password, schema) {
				@Override
				public IDatabaseConnection getConnection() throws Exception {
					IDatabaseConnection connection = super.getConnection();

					connection.getConfig().setProperty(
							DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
							new OracleDataTypeFactory());

					return connection;
				}
			};
			databaseConnection = databaseTester.getConnection();

			DatabaseConfig databaseConfig = databaseConnection.getConfig();
			databaseConfig.setProperty(
					DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
					new OracleDataTypeFactory());

			databaseDataSet = databaseConnection.createDataSet();
		} catch (Exception e) {
			LOGGER.error("Encountered an error initializing DBUnit: "
					+ e.getMessage());
		}
	}

	public IDatabaseTester getDatabaseTester() {
		return databaseTester;
	}

	public IDatabaseConnection getDatabaseConnection() {
		return databaseConnection;
	}

	public IDataSet getDatabaseDataSet() {
		return databaseDataSet;
	}

	/**
	 * Tells the base class to clean this table during teardown
	 * 
	 * @param table
	 *            The name of the table to clean
	 * @param conditions
	 *            The content of a 'where' clause, e.g., 'key > 0'
	 */
	public void registerTableForCleanup(String table, String conditions) {
		if (table == null) {
			return;
		}
		tablesToClean.put(table, conditions);
	}

	/**
	 * Tells the base class to clean this table during teardown
	 * 
	 * @param table
	 *            The name of the table to clean
	 */
	public void registerTableForCleanup(String table) {
		registerTableForCleanup(table, null);
	}

	/**
	 * Tells the base class to reset the sequence during teardown
	 * 
	 * @param sequence
	 *            The name of the sequence to reset
	 * @param value
	 *            The value the sequence should be set to
	 */
	public void registerSequenceForReset(String sequence, long value) {
		if (sequence == null) {
			return;
		}
		sequencesToReset.put(sequence, value);
	}

	/**
	 * Tells the base class to reset the sequence during teardown
	 * 
	 * @param sequence
	 *            The name of the sequence to reset to 1
	 */
	public void registerSequenceForReset(String sequence) {
		registerSequenceForReset(sequence, 1);
	}

	/**
	 * Builds an IDataSet from an XML file
	 * 
	 * @param file
	 * @return
	 */
	public IDataSet buildDataSet(File file) {
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setColumnSensing(true); // Tolerates nulls in columns
		IDataSet dataSet = null;
		try {
			dataSet = builder.build(file);
		} catch (MalformedURLException e) {
			LOGGER.error("Unable to load file: " + e.getMessage());
		} catch (DataSetException e) {
			LOGGER.error("Unable to populate dataset:" + e.getMessage());
		}
		return dataSet;
	}

	/**
	 * Loads data into the DB from an XML file
	 * 
	 * @param file
	 * @return
	 */
	public void loadDataSet(File file) {
		IDataSet dataSet = buildDataSet(file);
		databaseTester.setDataSet(dataSet);
		// will call default setUpOperation
		try {
			databaseTester.onSetup();
		} catch (Exception e) {
			LOGGER.error("Unable to load dataset:" + e.getMessage());
		}
	}

	/**
	 * For use during development to generate an XML representation of the data
	 * in the database.
	 * 
	 * @param tableName
	 *            e.g. "fuel.prices"
	 * @param queryFromTable
	 *            e.g. "select * from fuel.prices order by date_key, time_key"
	 * @param fileOutputName
	 *            e.g. "/tmp/pricedump.xml"
	 * @throws Exception
	 */
	public void dumpXml(String tableName, String queryFromTable,
			String fileOutputName) throws Exception {
		// partial database export
		QueryDataSet partialDataSet = new QueryDataSet(databaseConnection);
		partialDataSet.addTable(tableName, queryFromTable);
		FlatXmlDataSet.write(partialDataSet, new FileOutputStream(
				fileOutputName));
	}

	public void dumpXml(Map<String, String> queryMap, String fileOutputName)
			throws Exception {
		QueryDataSet partialDataSet = new QueryDataSet(databaseConnection);
		for (Map.Entry<String, String> entry : queryMap.entrySet()) {
			String tableName = entry.getKey();
			String query = entry.getValue();
			LOGGER.info("Adding " + tableName + " with query " + query
					+ " to XML dump file " + fileOutputName);
			partialDataSet.addTable(tableName, query);
		}
		FlatXmlDataSet.write(partialDataSet, new FileOutputStream(
				fileOutputName));
	}

	/**
	 * Dynamically builds SQL to execute against the database for cleaning up
	 * after tests.
	 * 
	 * @return A proper string of SQL
	 */
	public String generateCleanupSQL() {
		StringBuffer sql = new StringBuffer();
		sql.append("begin\n");
		for (String table : tablesToClean.keySet()) {
			sql.append("delete from " + table);
			String conditions = tablesToClean.get(table);
			if (conditions != null) {
				sql.append(" where " + conditions);
			}
			sql.append(";\n");
		}
		for (String sequence : sequencesToReset.keySet()) {
			sql.append("select setval('" + sequence + "',"
					+ sequencesToReset.get(sequence) + ", false);\n");
		}
		sql.append("commit;\n");
		sql.append("end;\n");

		return sql.toString();
	}

	public void execute(String sql) {
		try {
			Connection conn = databaseConnection.getConnection();
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			LOGGER.error("Unable to execute " + sql + ": " + e.getMessage());
			throw new RuntimeException(e);
		}

	}

	public ResultSet executeQuery(String sql) {
		try {
			Connection conn = databaseConnection.getConnection();
			Statement stmt = conn.createStatement();
			return stmt.executeQuery(sql);
		} catch (SQLException e) {
			LOGGER.error("Unable to execute " + sql + ": " + e.getMessage());
			throw new RuntimeException(e);
		}

	}

	public void cleanupDatabase() {
		String sql = generateCleanupSQL();
		execute(sql);
	}
}
