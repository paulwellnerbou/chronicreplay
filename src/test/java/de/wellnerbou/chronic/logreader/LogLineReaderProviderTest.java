package de.wellnerbou.chronic.logreader;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

public class LogLineReaderProviderTest {

	LogLineReaderProvider logLineReaderProvider = new LogLineReaderProvider();

	@Test
	public void testGetAllLogLineReader_noImplFound() {
		List<LogLineReader> logLineReader = logLineReaderProvider.getAllLogLineReader("[Id without any implementation]");
		assertThat(logLineReader.size()).isEqualTo(0);
	}

	@Test
	public void testGetAllLogLineReader_dummy() {
		List<LogLineReader> logLineReader = logLineReaderProvider.getAllLogLineReader("dummy");
		assertThat(logLineReader.size()).isEqualTo(1);
		assertThat(logLineReader.get(0)).isInstanceOf(DummyLogLineReader.class);
	}

	@Test(expected = NoImplementationFoundException.class)
	public void testGetLogLineReader_noImplFound() {
		@SuppressWarnings("unused")
		LogLineReader logLineReader = logLineReaderProvider.getLogLineReader("[Id without any implementation]");
	}

	@Test
	public void testGetLogLineReader_dummy() {
		LogLineReader logLineReader = logLineReaderProvider.getLogLineReader("dummy");
		assertThat(logLineReader).isInstanceOf(DummyLogLineReader.class);
	}

}
