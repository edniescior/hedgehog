package xbi.testutils.client;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

public class DumpTestDataModeTest {

	@Test
	public void setInvalidSqlMapStringNoBraces() {
		DumpTestDataMode mode = new DumpTestDataMode(new File("/dev/null"));
		String sqlMapString = "somerandomstring:huh";
		mode.setSqlMap(sqlMapString);
		Assert.assertFalse(mode.isValid());
		Assert.assertEquals(0, mode.getSqlMap().size());
	}
	
	@Test
	public void setInvalidSqlMapStringNoColon() {
		DumpTestDataMode mode = new DumpTestDataMode(new File("/dev/null"));
		String sqlMapString = "{onlyasinglestring}";
		mode.setSqlMap(sqlMapString);
		Assert.assertFalse(mode.isValid());
		Assert.assertEquals(0, mode.getSqlMap().size());
	}
	
	@Test
	public void setSingleSqlMapString() {
		DumpTestDataMode mode = new DumpTestDataMode(new File("/dev/null"));
		String sqlMapString = "{tablename:select * from dual}";
		mode.setSqlMap(sqlMapString);
		Assert.assertTrue(mode.isValid());
		Assert.assertEquals(1, mode.getSqlMap().size());
		Assert.assertEquals("select * from dual", mode.getSqlMap().get("tablename"));
	}
	
	@Test
	public void setMultiSqlMapString() {
		DumpTestDataMode mode = new DumpTestDataMode(new File("/dev/null"));
		String sqlMapString = "{tablename:select * from dual},{tablename2:select col1,c02 from xbi_pres.ed},{foo:bar}";
		mode.setSqlMap(sqlMapString);
		Assert.assertTrue(mode.isValid());
		Assert.assertEquals(3, mode.getSqlMap().size());
		Assert.assertEquals("select * from dual", mode.getSqlMap().get("tablename"));
		Assert.assertEquals("select col1,c02 from xbi_pres.ed", mode.getSqlMap().get("tablename2"));
		Assert.assertEquals("bar", mode.getSqlMap().get("foo"));
	}
}
