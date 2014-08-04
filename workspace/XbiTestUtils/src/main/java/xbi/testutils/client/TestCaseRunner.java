package xbi.testutils.client;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton to run DB Unit test cases. They will be run in the order that they
 * are added.
 * 
 * @author eniesc200
 */
enum TestCaseRunner {
	INSTANCE;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(TestCaseRunner.class);

	// the list of test cases to be run
	private Queue<Mode> modes = new LinkedList<Mode>();

	/**
	 * Add a test case to be executed.
	 * 
	 * @param mode
	 *            the test case to execute.
	 */
	void addMode(Mode mode) {
		if (mode == null) {
			throw new NullPointerException();
		}
		modes.add(mode);
	}

	/**
	 * Execute all test cases that have been added.
	 */
	void run() {
		Iterator<Mode> listIterator = modes.iterator();
		while (listIterator.hasNext()) {
			Mode m = modes.poll();
			LOGGER.info("Running " + m.toString());
			if (m.isValid()) {
				m.execute();
				LOGGER.info("Completed " + m.toString());
			}
		}
	}
}
