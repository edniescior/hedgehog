package xbi.testutils.dbunit;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;



public class KettleTestCaseConfigurationTest {
	
	private File f = new File("/foo.txt");

	@Test
	public void createDefaultInstance() {
		KettleTestCaseConfiguration.Builder builder = new KettleTestCaseConfiguration.Builder(f);
		KettleTestCaseConfiguration configuration = builder.build();
		
		Assert.assertEquals(f, configuration.getExecutableFile());
		Assert.assertEquals(0, configuration.getInFiles().size());
		Assert.assertEquals(0, configuration.getOutFiles().size());
		Assert.assertEquals(0, configuration.getTargetTables().size());
		Assert.assertEquals(0, configuration.getParams().size());
	}
	
	@Test
	public void addSingleInFile() {
		File testInFile = new File("/testinputfile");
		
		KettleTestCaseConfiguration.Builder builder = new KettleTestCaseConfiguration.Builder(f);
		builder.addInFile(testInFile);
		KettleTestCaseConfiguration configuration = builder.build();
		
		Assert.assertEquals(f, configuration.getExecutableFile());
		Assert.assertEquals(1, configuration.getInFiles().size());
		Assert.assertEquals(0, configuration.getOutFiles().size());
		Assert.assertEquals(0, configuration.getTargetTables().size());
		Assert.assertEquals(0, configuration.getParams().size());
		
		Assert.assertNotSame(testInFile, configuration.getInFiles().get(0));
		Assert.assertEquals("/testinputfile", configuration.getInFiles().get(0).getAbsolutePath());
	}

	@Test
	public void addMultipleInFile() {
		File testInFile1 = new File("/testinputfile1");
		File testInFile2 = new File("/testinputfile2");
		File testInFile3 = new File("/testinputfile3");
		
		KettleTestCaseConfiguration.Builder builder = new KettleTestCaseConfiguration.Builder(f);
		builder.addInFile(testInFile1);
		builder.addInFile(testInFile2);
		builder.addInFile(testInFile3);
		KettleTestCaseConfiguration configuration = builder.build();
		
		Assert.assertEquals(f, configuration.getExecutableFile());
		Assert.assertEquals(3, configuration.getInFiles().size());
		Assert.assertEquals(0, configuration.getOutFiles().size());
		Assert.assertEquals(0, configuration.getTargetTables().size());
		Assert.assertEquals(0, configuration.getParams().size());
		
		Assert.assertNotSame(testInFile1, configuration.getInFiles().get(0));
		Assert.assertEquals("/testinputfile1", configuration.getInFiles().get(0).getAbsolutePath());
		Assert.assertNotSame(testInFile2, configuration.getInFiles().get(1));
		Assert.assertEquals("/testinputfile2", configuration.getInFiles().get(1).getAbsolutePath());
		Assert.assertNotSame(testInFile3, configuration.getInFiles().get(2));
		Assert.assertEquals("/testinputfile3", configuration.getInFiles().get(2).getAbsolutePath());
	}
	
	@Test
	public void addDuplicateInFile() {
		File testInFile1 = new File("/testinputfile1");
		File testInFile2 = new File("/testinputfile2");
		File testInFile3 = new File("/testinputfile1");  // same path, different File object
		
		KettleTestCaseConfiguration.Builder builder = new KettleTestCaseConfiguration.Builder(f);
		builder.addInFile(testInFile1);
		builder.addInFile(testInFile2);
		builder.addInFile(testInFile3);
		KettleTestCaseConfiguration configuration = builder.build();
		
		Assert.assertEquals(f, configuration.getExecutableFile());
		Assert.assertEquals(2, configuration.getInFiles().size());  // it should ignore testFile3
		Assert.assertEquals(0, configuration.getOutFiles().size());
		Assert.assertEquals(0, configuration.getTargetTables().size());
		Assert.assertEquals(0, configuration.getParams().size());
		
		Assert.assertNotSame(testInFile1, configuration.getInFiles().get(0));
		Assert.assertEquals("/testinputfile1", configuration.getInFiles().get(0).getAbsolutePath());
		Assert.assertNotSame(testInFile2, configuration.getInFiles().get(1));
		Assert.assertEquals("/testinputfile2", configuration.getInFiles().get(1).getAbsolutePath());
	}
	
	@Test
	public void addSingleOutFile() {
		File testOutFile = new File("/testoutputfile");
		
		KettleTestCaseConfiguration.Builder builder = new KettleTestCaseConfiguration.Builder(f);
		builder.addOutFile(testOutFile);
		KettleTestCaseConfiguration configuration = builder.build();
		
		Assert.assertEquals(f, configuration.getExecutableFile());
		Assert.assertEquals(0, configuration.getInFiles().size());
		Assert.assertEquals(1, configuration.getOutFiles().size());
		Assert.assertEquals(0, configuration.getTargetTables().size());
		Assert.assertEquals(0, configuration.getParams().size());
		
		Assert.assertNotSame(testOutFile, configuration.getOutFiles().get(0));
		Assert.assertEquals("/testoutputfile", configuration.getOutFiles().get(0).getAbsolutePath());
	}
	
	@Test
	public void addMultipleOutFile() {
		File testOutFile1 = new File("/testoutputfile1");
		File testOutFile2 = new File("/testoutputfile2");
		File testOutFile3 = new File("/testoutputfile3");
		
		KettleTestCaseConfiguration.Builder builder = new KettleTestCaseConfiguration.Builder(f);
		builder.addOutFile(testOutFile1);
		builder.addOutFile(testOutFile2);
		builder.addOutFile(testOutFile3);
		KettleTestCaseConfiguration configuration = builder.build();
		
		Assert.assertEquals(f, configuration.getExecutableFile());
		Assert.assertEquals(0, configuration.getInFiles().size());
		Assert.assertEquals(3, configuration.getOutFiles().size());
		Assert.assertEquals(0, configuration.getTargetTables().size());
		Assert.assertEquals(0, configuration.getParams().size());
		
		Assert.assertNotSame(testOutFile1, configuration.getOutFiles().get(0));
		Assert.assertEquals("/testoutputfile1", configuration.getOutFiles().get(0).getAbsolutePath());
		Assert.assertNotSame(testOutFile2, configuration.getOutFiles().get(1));
		Assert.assertEquals("/testoutputfile2", configuration.getOutFiles().get(1).getAbsolutePath());
		Assert.assertNotSame(testOutFile3, configuration.getOutFiles().get(2));
		Assert.assertEquals("/testoutputfile3", configuration.getOutFiles().get(2).getAbsolutePath());
	}
	
	@Test
	public void addDuplicateOutFile() {
		File testOutFile1 = new File("/testoutputfile1");
		File testOutFile2 = new File("/testoutputfile2");
		File testOutFile3 = new File("/testoutputfile1");  // same path, different File object
		
		KettleTestCaseConfiguration.Builder builder = new KettleTestCaseConfiguration.Builder(f);
		builder.addOutFile(testOutFile1);
		builder.addOutFile(testOutFile2);
		builder.addOutFile(testOutFile3);
		KettleTestCaseConfiguration configuration = builder.build();
		
		Assert.assertEquals(f, configuration.getExecutableFile());
		Assert.assertEquals(0, configuration.getInFiles().size());
		Assert.assertEquals(2, configuration.getOutFiles().size());  // it should ignore testFile3
		Assert.assertEquals(0, configuration.getTargetTables().size());
		Assert.assertEquals(0, configuration.getParams().size());
		
		Assert.assertNotSame(testOutFile1, configuration.getOutFiles().get(0));
		Assert.assertEquals("/testoutputfile1", configuration.getOutFiles().get(0).getAbsolutePath());
		Assert.assertNotSame(testOutFile2, configuration.getOutFiles().get(1));
		Assert.assertEquals("/testoutputfile2", configuration.getOutFiles().get(1).getAbsolutePath());
	}
	
	@Test
	public void addTargetTable() {
		KettleTestCaseConfiguration.Builder builder = new KettleTestCaseConfiguration.Builder(f);
		builder.addTargetTable("target1");
		KettleTestCaseConfiguration configuration = builder.build();
		
		Assert.assertEquals(f, configuration.getExecutableFile());
		Assert.assertEquals(0, configuration.getInFiles().size());
		Assert.assertEquals(0, configuration.getOutFiles().size());
		Assert.assertEquals(1, configuration.getTargetTables().size());
		Assert.assertEquals(0, configuration.getParams().size());
		
		Assert.assertTrue(configuration.getTargetTables().containsKey("target1"));
		Assert.assertEquals(0, configuration.getTargetTables().get("target1").length);
	}
	
	@Test
	public void addMultipleTargetTables() {
		KettleTestCaseConfiguration.Builder builder = new KettleTestCaseConfiguration.Builder(f);
		builder.addTargetTable("target1");
		builder.addTargetTable("target2");
		builder.addTargetTable("target3");
		KettleTestCaseConfiguration configuration = builder.build();
		
		Assert.assertEquals(f, configuration.getExecutableFile());
		Assert.assertEquals(0, configuration.getInFiles().size());
		Assert.assertEquals(0, configuration.getOutFiles().size());
		Assert.assertEquals(3, configuration.getTargetTables().size());
		Assert.assertEquals(0, configuration.getParams().size());
		
		Assert.assertTrue(configuration.getTargetTables().containsKey("target1"));
		Assert.assertEquals(0, configuration.getTargetTables().get("target1").length);
		Assert.assertTrue(configuration.getTargetTables().containsKey("target2"));
		Assert.assertEquals(0, configuration.getTargetTables().get("target2").length);
		Assert.assertTrue(configuration.getTargetTables().containsKey("target3"));
		Assert.assertEquals(0, configuration.getTargetTables().get("target3").length);
	}
	
	@Test
	public void addDuplicateTargetTable() {
		KettleTestCaseConfiguration.Builder builder = new KettleTestCaseConfiguration.Builder(f);
		builder.addTargetTable("target1");
		builder.addTargetTable("target2");
		builder.addTargetTable("target1");
		KettleTestCaseConfiguration configuration = builder.build();
		
		Assert.assertEquals(f, configuration.getExecutableFile());
		Assert.assertEquals(0, configuration.getInFiles().size());
		Assert.assertEquals(0, configuration.getOutFiles().size());
		Assert.assertEquals(2, configuration.getTargetTables().size());
		Assert.assertEquals(0, configuration.getParams().size());
		
		Assert.assertTrue(configuration.getTargetTables().containsKey("target1"));
		Assert.assertEquals(0, configuration.getTargetTables().get("target1").length);
		Assert.assertTrue(configuration.getTargetTables().containsKey("target2"));
		Assert.assertEquals(0, configuration.getTargetTables().get("target2").length);
	}
	
	@Test
	public void addOrderBy() {
		KettleTestCaseConfiguration.Builder builder = new KettleTestCaseConfiguration.Builder(f);
		builder.addTargetTable("target1");
		builder.addOrderBy("target1", "column1");
		KettleTestCaseConfiguration configuration = builder.build();
		
		Assert.assertEquals(f, configuration.getExecutableFile());
		Assert.assertEquals(0, configuration.getInFiles().size());
		Assert.assertEquals(0, configuration.getOutFiles().size());
		Assert.assertEquals(1, configuration.getTargetTables().size());
		Assert.assertEquals(0, configuration.getParams().size());
		
		Assert.assertTrue(configuration.getTargetTables().containsKey("target1"));
		Assert.assertEquals(1, configuration.getTargetTables().get("target1").length);
		Assert.assertEquals("column1", configuration.getTargetTables().get("target1")[0]);
	}
	
	@Test
	public void addMultipleOrderBy() {
		KettleTestCaseConfiguration.Builder builder = new KettleTestCaseConfiguration.Builder(f);
		builder.addTargetTable("target1");
		builder.addTargetTable("target2");
		builder.addOrderBy("target1", "column1");
		builder.addOrderBy("target1", "column2");
		builder.addOrderBy("target2", "column1");
		KettleTestCaseConfiguration configuration = builder.build();
		
		Assert.assertEquals(f, configuration.getExecutableFile());
		Assert.assertEquals(0, configuration.getInFiles().size());
		Assert.assertEquals(0, configuration.getOutFiles().size());
		Assert.assertEquals(2, configuration.getTargetTables().size());
		Assert.assertEquals(0, configuration.getParams().size());
		
		Assert.assertTrue(configuration.getTargetTables().containsKey("target1"));
		Assert.assertEquals(2, configuration.getTargetTables().get("target1").length);
		Assert.assertEquals("column1", configuration.getTargetTables().get("target1")[0]);
		Assert.assertEquals("column2", configuration.getTargetTables().get("target1")[1]);
		
		Assert.assertTrue(configuration.getTargetTables().containsKey("target2"));
		Assert.assertEquals(1, configuration.getTargetTables().get("target2").length);
		Assert.assertEquals("column1", configuration.getTargetTables().get("target2")[0]);
		
		System.out.println(configuration);
	}
	
	@Test
	public void addDuplicateOrderBy() {
		KettleTestCaseConfiguration.Builder builder = new KettleTestCaseConfiguration.Builder(f);
		builder.addTargetTable("target1");
		builder.addOrderBy("target1", "column1");
		builder.addOrderBy("target1", "column2");
		builder.addOrderBy("target1", "column1");
		KettleTestCaseConfiguration configuration = builder.build();
		
		Assert.assertEquals(f, configuration.getExecutableFile());
		Assert.assertEquals(0, configuration.getInFiles().size());
		Assert.assertEquals(0, configuration.getOutFiles().size());
		Assert.assertEquals(1, configuration.getTargetTables().size());
		Assert.assertEquals(0, configuration.getParams().size());
		
		Assert.assertTrue(configuration.getTargetTables().containsKey("target1"));
		Assert.assertEquals(2, configuration.getTargetTables().get("target1").length);
		Assert.assertEquals("column1", configuration.getTargetTables().get("target1")[0]);
		Assert.assertEquals("column2", configuration.getTargetTables().get("target1")[1]);
	}
	
	@Test(expected = IllegalStateException.class)
	public void addUnknownTableToOrderBy() {
		KettleTestCaseConfiguration.Builder builder = new KettleTestCaseConfiguration.Builder(f);
		builder.addTargetTable("foo");
		builder.addOrderBy("target1", "column1");
		builder.build();
	}
	
	@Test
	public void addParameter() {
		KettleTestCaseConfiguration.Builder builder = new KettleTestCaseConfiguration.Builder(f);
		builder.addParameter("param1", "foo");
		KettleTestCaseConfiguration configuration = builder.build();
		
		Assert.assertEquals(f, configuration.getExecutableFile());
		Assert.assertEquals(0, configuration.getInFiles().size());
		Assert.assertEquals(0, configuration.getOutFiles().size());
		Assert.assertEquals(0, configuration.getTargetTables().size());
		Assert.assertEquals(1, configuration.getParams().size());
		
		Assert.assertTrue(configuration.getParams().containsKey("param1"));
		Assert.assertEquals("foo", configuration.getParams().get("param1"));
	}
	
	@Test
	public void addMultipleParameters() {
		KettleTestCaseConfiguration.Builder builder = new KettleTestCaseConfiguration.Builder(f);
		builder.addParameter("param1", "foo");
		builder.addParameter("param2", "bar");
		builder.addParameter("param3", "zoo");
		KettleTestCaseConfiguration configuration = builder.build();
		
		Assert.assertEquals(f, configuration.getExecutableFile());
		Assert.assertEquals(0, configuration.getInFiles().size());
		Assert.assertEquals(0, configuration.getOutFiles().size());
		Assert.assertEquals(0, configuration.getTargetTables().size());
		Assert.assertEquals(3, configuration.getParams().size());
		
		Assert.assertTrue(configuration.getParams().containsKey("param1"));
		Assert.assertEquals("foo", configuration.getParams().get("param1"));
		Assert.assertTrue(configuration.getParams().containsKey("param2"));
		Assert.assertEquals("bar", configuration.getParams().get("param2"));
		Assert.assertTrue(configuration.getParams().containsKey("param3"));
		Assert.assertEquals("zoo", configuration.getParams().get("param3"));
	}
	
	@Test
	public void addDuplicateParameters() {
		KettleTestCaseConfiguration.Builder builder = new KettleTestCaseConfiguration.Builder(f);
		builder.addParameter("param1", "foo");
		builder.addParameter("param2", "bar");
		builder.addParameter("param2", "zoo");
		KettleTestCaseConfiguration configuration = builder.build();
		
		Assert.assertEquals(f, configuration.getExecutableFile());
		Assert.assertEquals(0, configuration.getInFiles().size());
		Assert.assertEquals(0, configuration.getOutFiles().size());
		Assert.assertEquals(0, configuration.getTargetTables().size());
		Assert.assertEquals(2, configuration.getParams().size());
		
		Assert.assertTrue(configuration.getParams().containsKey("param1"));
		Assert.assertEquals("foo", configuration.getParams().get("param1"));
		Assert.assertTrue(configuration.getParams().containsKey("param2"));
		Assert.assertEquals("zoo", configuration.getParams().get("param2"));
	}
}
