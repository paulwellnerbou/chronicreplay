package de.wellnerbou.chronic.replay;

import org.junit.Test;

import java.io.IOException;

public class ChronicReplayIT {

	@Test
	public void testRun() throws IOException {
		String[] args = new String[] { "--host=http://localhost",
				"--logfile=src/test/resources/combined-log-example.log",
				"--logparser=combined" };
		ChronicReplay.main(args);
	}

	@Test
	public void testRunWithLocalJsonFile() throws IOException {
		String[] args = new String[] { "--host=http://localhost",
				"--logfile=src/test/resources/elasticsearch-example.json",
				"--logreader=jsonreader",
				"--logparser=elasticsearch" };
		ChronicReplay.main(args);
	}
}
