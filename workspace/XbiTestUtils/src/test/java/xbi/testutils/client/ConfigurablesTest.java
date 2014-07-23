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
	public void setInvalidTargetTablesNoBraces() {
		Configurables configurable = new Configurables();
		String targetTabStr = "somerandomstring:huh";
		configurable.setTargetTables(targetTabStr);
		Assert.assertEquals(0, configurable.getTargetTables().size());
	}
	
	@Test
	public void setInvalidTargetTablesNoColon() {
		Configurables configurable = new Configurables();
		String targetTabStr = "{onlyasinglestring}";
		configurable.setTargetTables(targetTabStr);
		Assert.assertEquals(1, configurable.getTargetTables().size());
	}
	
	@Test
	public void setSingleTargetTablesNatOrder() {
		Configurables configurable = new Configurables();
		configurable.setTargetTables("{targettable1:}");
		Assert.assertEquals(1, configurable.getTargetTables().size());
		Assert.assertTrue(configurable.getTargetTables().containsKey("targettable1"));
		Assert.assertEquals(0, configurable.getTargetTables().get("targettable1").length);
	}
	
	@Test
	public void setMultiTargetTablesNatOrder() {
		Configurables configurable = new Configurables();
		configurable.setTargetTables("{targettable1:},{targettable2:},{targettable3:}");
		Assert.assertEquals(3, configurable.getTargetTables().size());
		Assert.assertTrue(configurable.getTargetTables().containsKey("targettable1"));
		Assert.assertEquals(0, configurable.getTargetTables().get("targettable1").length);
		Assert.assertTrue(configurable.getTargetTables().containsKey("targettable2"));
		Assert.assertEquals(0, configurable.getTargetTables().get("targettable2").length);
		Assert.assertTrue(configurable.getTargetTables().containsKey("targettable3"));
		Assert.assertEquals(0, configurable.getTargetTables().get("targettable3").length);
	}
	
	@Test
	public void setSingleTargetTablesWithOrder() {
		Configurables configurable = new Configurables();
		configurable.setTargetTables("{targettable1:colname1}");
		Assert.assertEquals(1, configurable.getTargetTables().size());
		Assert.assertEquals("colname1", configurable.getTargetTables().get("targettable1")[0]);
	}
	
	@Test
	public void setMultiTargetTablesWithOrder() {
		Configurables configurable = new Configurables();
		configurable.setTargetTables("{targettable1:colname1,colname2},{targettable2:},{targettable3:colname1,colname4,colname3}");
		
		Assert.assertTrue(configurable.getTargetTables().containsKey("targettable1"));
		Assert.assertEquals(2, configurable.getTargetTables().get("targettable1").length);
		Assert.assertEquals("colname1", configurable.getTargetTables().get("targettable1")[0]);
		Assert.assertEquals("colname2", configurable.getTargetTables().get("targettable1")[1]);
		
		Assert.assertTrue(configurable.getTargetTables().containsKey("targettable2"));
		Assert.assertEquals(0, configurable.getTargetTables().get("targettable2").length);
		
		Assert.assertTrue(configurable.getTargetTables().containsKey("targettable3"));
		Assert.assertEquals(3, configurable.getTargetTables().get("targettable3").length);
		Assert.assertEquals("colname1", configurable.getTargetTables().get("targettable3")[0]);
		Assert.assertEquals("colname4", configurable.getTargetTables().get("targettable3")[1]);
		Assert.assertEquals("colname3", configurable.getTargetTables().get("targettable3")[2]);
	}
	
	@Test
	public void setSingleTargetTablesWithOrderDupe() {
		Configurables configurable = new Configurables();
		configurable.setTargetTables("{targettable1:colname1,colname4,colname1,colname3}");
		
		Assert.assertTrue(configurable.getTargetTables().containsKey("targettable1"));
		Assert.assertEquals(3, configurable.getTargetTables().get("targettable1").length);
		Assert.assertEquals("colname1", configurable.getTargetTables().get("targettable1")[0]);
		Assert.assertEquals("colname4", configurable.getTargetTables().get("targettable1")[1]);
		Assert.assertEquals("colname3", configurable.getTargetTables().get("targettable1")[2]);
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
	
	@Test
	public void setInvalidParamStringNoEquals() {
		Configurables configurable = new Configurables();
		String pString = "somerandomstring:huh";
		configurable.setParamsMap(pString);
		Assert.assertEquals(0, configurable.getParams().size());
	}
	
	@Test
	public void setSingleParamString() {
		Configurables configurable = new Configurables();
		String pString = "paramName=paramValue";
		configurable.setParamsMap(pString);
		Assert.assertEquals(1, configurable.getParams().size());
		Assert.assertEquals("paramValue", configurable.getParams().get("paramName"));
	}
	
	@Test
	public void setMultiParamString() {
		Configurables configurable = new Configurables();
		String pString = "param_Name=param_Value,foo=bar,zoom=mooz";
		configurable.setParamsMap(pString);
		Assert.assertEquals(3, configurable.getParams().size());
		Assert.assertEquals("param_Value", configurable.getParams().get("param_Name"));
		Assert.assertEquals("bar", configurable.getParams().get("foo"));
		Assert.assertEquals("mooz", configurable.getParams().get("zoom"));
	}
	
}
