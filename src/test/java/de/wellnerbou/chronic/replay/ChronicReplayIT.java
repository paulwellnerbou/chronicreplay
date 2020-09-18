package de.wellnerbou.chronic.replay;

import org.junit.Test;

import java.io.IOException;

public class ChronicReplayIT {

	@Test
	public void testMainOptionParsing() throws IOException {
		String[] args = new String[]{};
		ChronicReplay.main(args);
	}

	@Test
	public void testRun() throws IOException {
		String[] args = new String[]{"--host=http://localhost",
				"--logfile=src/test/resources/combined-log-example.log",
				"--logparser=combined"};
		ChronicReplay.main(args);
	}

	@Test
	public void testRun_grok() throws IOException {
		String[] args = new String[]{"--host=http://localhost",
				"--logfile=src/test/resources/combined-log-example.log",
				"--logparser=grok", "--grokpattern=%{COMBINEDAPACHELOG}"};
		ChronicReplay.main(args);
	}

	@Test
	public void testRun_withGivenTimeRange_runUntilEndOfFile() throws IOException {
		String[] args = new String[]{"--host=http://localhost",
				"--logfile=src/test/resources/combined-log-example-different-times.log",
				"--logparser=combined",
				"--from=11:55:00", "--until=12:56:00"
		};
		ChronicReplay.main(args);
	}

	@Test
	public void testRun_withGivenTimeRange_stopBeforeEndOfFile() throws IOException {
		String[] args = new String[]{"--host=http://localhost",
				"--logfile=src/test/resources/combined-log-example-different-times.log",
				"--logparser=combined",
				"--from=11:55:00", "--until=11:55:05"
		};
		ChronicReplay.main(args);
	}

	@Test
	public void testRunWithLocalJsonFile() throws IOException {
		String[] args = new String[]{"--host=http://localhost",
				"--logfile=src/test/resources/elasticsearch-example.json",
				"--logreader=jsonreader",
				"--logparser=elasticsearch"};
		ChronicReplay.main(args);
	}

	@Test
	public void testRunWithSimplifiedLocalJsonFile() throws IOException {
		String[] args = new String[]{"--host=http://localhost",
				"--logfile=src/test/resources/simplified.json",
				"--logreader=jsonreader",
				"--logparser=elasticsearch"};
		ChronicReplay.main(args);
	}

	@Test
	public void testRunWithDefaultCsvLogger() throws IOException {
		String[] args = new String[]{"--host=http://www.example.com",
				"--logfile=src/test/resources/combined-log-example.log",
				"--logparser=combined",
				"--wait-for-termination"};
		ChronicReplay.main(args);
	}

	@Test
	public void testRunWithJsonLogger() throws IOException {
		String[] args = new String[]{"--host=http://www.example.com",
				"--logfile=src/test/resources/combined-log-example.log",
				"--logparser=combined",
				"--logger=json",
				"--wait-for-termination"};
		ChronicReplay.main(args);
	}
}
