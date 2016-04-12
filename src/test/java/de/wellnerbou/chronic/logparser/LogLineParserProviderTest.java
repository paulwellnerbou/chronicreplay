package de.wellnerbou.chronic.logparser;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import de.wellnerbou.chronic.plugins.NoImplementationFoundException;
import org.junit.Test;

public class LogLineParserProviderTest {

	LogLineParserProvider logLineParserProvider = new LogLineParserProvider();

	@Test
	public void testGetAllLogLineReader_noImplFound() {
		List<LogLineParser> logLineParser = logLineParserProvider.getAllImplementations("[Id without any implementation]");
		assertThat(logLineParser.size()).isEqualTo(0);
	}

	@Test
	public void testGetAllLogLineReader_dummy() {
		List<LogLineParser> logLineParser = logLineParserProvider.getAllImplementations("dummy");
		assertThat(logLineParser.size()).isEqualTo(1);
		assertThat(logLineParser.get(0)).isInstanceOf(DummyLogLineParser.class);
	}

	@Test(expected = NoImplementationFoundException.class)
	public void testGetLogLineReader_noImplFound() {
		@SuppressWarnings("unused")
		LogLineParser logLineParser = logLineParserProvider.getImplementation("[Id without any implementation]");
	}

	@Test
	public void testGetLogLineReader_dummy() {
		LogLineParser logLineParser = logLineParserProvider.getImplementation("dummy");
		assertThat(logLineParser).isInstanceOf(DummyLogLineParser.class);
	}
}
