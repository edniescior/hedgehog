package com.comcast.xbi;

import java.io.File;
import java.io.IOException;

import xbi.testutils.dbunit.KettleTestCase;

public class TrXboStgActivdiscoTest extends KettleTestCase 
{

	public TrXboStgActivdiscoTest() throws IOException {
		super(
				new File(
						"/Users/eniesc200/Work/XBI/trunk/Pentaho/Projects/xbo/trans/tr_xbo_stg_activdisco.ktr"));
		// TODO Auto-generated constructor stub
	}

	@Override
	public void registerObjectsForCleanup() {
		connector.registerTableForCleanup("ws_xbo_devices_sus");
		connector.registerTableForCleanup("ws_xbo_activ_disco_ps");
	}

	@Override
	protected void afterSetup() {
		connector
				.loadDataSet(new File(
						"/Users/eniesc200/Work/XBI/trunk/Pentaho/Projects/xbo/data/test/input/tr_xbo_stg_activdisco.in.xml"));
	}

	@Override
	protected String getPropertyPrefix() {
		// TODO Auto-generated method stub
		return null;
	}

}
