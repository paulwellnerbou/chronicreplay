package de.wellnerbou.chronic.logparser;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import de.wellnerbou.chronic.replay.LogLineData;

public class CombinedLogFormatLogLineParserTest {

	private static final String TEST_LINE = "127.0.0.1 - - [02/Jan/2014:11:55:12 +0100] \"GET / HTTP/1.1\" 200 481 \"-\" \"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:26.0) Gecko/20100101 Firefox/26.0\"";
	private static final String TRUNCATED_TEST_LINE = "127.0.0.1 - - [02/Jan/2014:11:55:12 +0100] \"GET / HTTP/1.1\" 200 481 \"-\" \"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:26.0) Gecko/20100101 Firef";
	CombinedLogFormatLogLineParser combinedLogFormatLogLineReader = new CombinedLogFormatLogLineParser();

	@Test
	public void testParseLine() {
		LogLineData logLineData = combinedLogFormatLogLineReader.parseLine(TEST_LINE);

		assertThat(logLineData.getTime()).isEqualTo(1388663712000L);
		assertThat(logLineData.getRequestMethod()).isEqualTo("GET");
		assertThat(logLineData.getRequest()).isEqualTo("/");
		assertThat(logLineData.getDuration()).isEqualTo(0);
		assertThat(logLineData.getStatusCode()).isEqualTo("200");
		assertThat(logLineData.getUserAgent()).isEqualTo("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:26.0) Gecko/20100101 Firefox/26.0");
	}

	@Test
	public void testGetUserAgent() {
		String[] parts = TEST_LINE.split("\\s");
		String userAgent = combinedLogFormatLogLineReader.getUserAgent(parts);
		assertThat(userAgent).isEqualTo("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:26.0) Gecko/20100101 Firefox/26.0");
	}

	@Test
	public void testGetUserAgent_IncorrectData() {
		String[] parts = TRUNCATED_TEST_LINE.split("\\s");
		String userAgent = combinedLogFormatLogLineReader.getUserAgent(parts);
		assertThat(userAgent).isEqualTo("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:26.0) Gecko/20100101 Firef");
	}

}
