package xbi.testutils.kettle;

import java.io.File;

public class RunnerFactory {

	public static Runner createRunner(File xmlFile) {
		// It just works with Transformations for now.
		return new TransformationRunner(xmlFile);
	}
}
