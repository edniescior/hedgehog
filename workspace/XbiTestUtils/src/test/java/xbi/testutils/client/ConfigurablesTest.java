package xbi.testutils.client;

import junit.framework.Assert;

import org.junit.Test;

public class ConfigurablesTest {

	@Test(expected = IllegalStateException.class)
	public void multipleCallsToSetMode() {
		Configurables configurable = new Configurables();
		configurable.setMode('x');
		configurable.setMode('l');
	}
	
	@Test
	public void setSingleInFiles() {
		Configurables configurable = new Configurables();
		configurable.setInFiles("single/file.txt");
		Assert.assertEquals(1, configurable.getInFiles().size());
		Assert.assertEquals("single/file.txt", configurable.getInFiles().get(0).toString());
	}
	
	@Test
	public void setMultiInFiles() {
		Configurables configurable = new Configurables();
		configurable.setInFiles("single/file.txt,second/file.txt, athird.txt");
		Assert.assertEquals(3, configurable.getInFiles().size());
		Assert.assertEquals("single/file.txt", configurable.getInFiles().get(0).toString());
		Assert.assertEquals("second/file.txt", configurable.getInFiles().get(1).toString());
		Assert.assertEquals("athird.txt", configurable.getInFiles().get(2).toString());
	}

	@Test
	public void setSingleOutFiles() {
		Configurables configurable = new Configurables();
		configurable.setOutFiles("single/file.txt");
		Assert.assertEquals(1, configurable.getOutFiles().size());
		Assert.assertEquals("single/file.txt", configurable.getOutFiles().get(0).toString());
	}
	
	@Test
	public void setMultiOutFiles() {
		Configurables configurable = new Configurables();
		configurable.setOutFiles("single/file.txt,second/file.txt, athird.txt");
		Assert.assertEquals(3, configurable.getOutFiles().size());
		Assert.assertEquals("single/file.txt", configurable.getOutFiles().get(0).toString());
		Assert.assertEquals("second/file.txt", configurable.getOutFiles().get(1).toString());
		Assert.assertEquals("athird.txt", configurable.getOutFiles().get(2).toString());
	}
	
	@Test
	public void setSingleTargetTables() {
		Configurables configurable = new Configurables();
		configurable.setTargetTables("targettable1");
		Assert.assertEquals(1, configurable.getTargetTables().size());
		Assert.assertEquals("targettable1", configurable.getTargetTables().get(0).toString());
	}
	
	@Test
	public void setMultiTargetTables() {
		Configurables configurable = new Configurables();
		configurable.setTargetTables("targettable1,targettable2, targettable3");
		Assert.assertEquals(3, configurable.getTargetTables().size());
		Assert.assertEquals("targettable1", configurable.getTargetTables().get(0).toString());
		Assert.assertEquals("targettable2", configurable.getTargetTables().get(1).toString());
		Assert.assertEquals("targettable3", configurable.getTargetTables().get(2).toString());
	}
	
	@Test
	public void setInvalidSqlMapStringNoBraces() {
		Configurables configurable = new Configurables();
		String sqlMapString = "somerandomstring:huh";
		configurable.setSqlMap(sqlMapString);
		Assert.assertEquals(0, configurable.getSqlMap().size());
	}
	
	@Test
	public void setInvalidSqlMapStringNoColon() {
		Configurables configurable = new Configurables();
		String sqlMapString = "{onlyasinglestring}";
		configurable.setSqlMap(sqlMapString);
		Assert.assertEquals(0, configurable.getSqlMap().size());
	}
	
	@Test
	public void setSingleSqlMapString() {
		Configurables configurable = new Configurables();
		String sqlMapString = "{tablename:select * from dual}";
		configurable.setSqlMap(sqlMapString);
		Assert.assertEquals(1, configurable.getSqlMap().size());
		Assert.assertEquals("select * from dual", configurable.getSqlMap().get("tablename"));
	}
	
	@Test
	public void setMultiSqlMapString() {
		Configurables configurable = new Configurables();
		String sqlMapString = "{tablename:select * from dual},{tablename2:select col1,c02 from xbi_pres.ed},{foo:bar}";
		configurable.setSqlMap(sqlMapString);
		Assert.assertEquals(3, configurable.getSqlMap().size());
		Assert.assertEquals("select * from dual", configurable.getSqlMap().get("tablename"));
		Assert.assertEquals("select col1,c02 from xbi_pres.ed", configurable.getSqlMap().get("tablename2"));
		Assert.assertEquals("bar", configurable.getSqlMap().get("foo"));
	}
	
}
