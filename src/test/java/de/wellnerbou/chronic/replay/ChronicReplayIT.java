package de.wellnerbou.chronic.replay;

import org.junit.Test;

import java.io.IOException;

public class ChronicReplayIT {

	@Test
	public void testRun() throws IOException {
		String[] args = new String[] { "--host=http://localhost",
				"--logfile=src/test/resources/combined-log-example.log",
				"--logreader=combined" };
		ChronicReplay.main(args);
	}
}
