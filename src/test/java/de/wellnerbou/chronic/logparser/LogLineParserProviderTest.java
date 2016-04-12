package de.wellnerbou.chronic.logparser;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

public class LogLineParserProviderTest {

	LogLineReaderProvider logLineReaderProvider = new LogLineReaderProvider();

	@Test
	public void testGetAllLogLineReader_noImplFound() {
		List<LogLineParser> logLineParser = logLineReaderProvider.getAllLogLineReader("[Id without any implementation]");
		assertThat(logLineParser.size()).isEqualTo(0);
	}

	@Test
	public void testGetAllLogLineReader_dummy() {
		List<LogLineParser> logLineParser = logLineReaderProvider.getAllLogLineReader("dummy");
		assertThat(logLineParser.size()).isEqualTo(1);
		assertThat(logLineParser.get(0)).isInstanceOf(DummyLogLineParser.class);
	}

	@Test(expected = NoImplementationFoundException.class)
	public void testGetLogLineReader_noImplFound() {
		@SuppressWarnings("unused")
		LogLineParser logLineParser = logLineReaderProvider.getLogLineReader("[Id without any implementation]");
	}

	@Test
	public void testGetLogLineReader_dummy() {
		LogLineParser logLineParser = logLineReaderProvider.getLogLineReader("dummy");
		assertThat(logLineParser).isInstanceOf(DummyLogLineParser.class);
	}
}
