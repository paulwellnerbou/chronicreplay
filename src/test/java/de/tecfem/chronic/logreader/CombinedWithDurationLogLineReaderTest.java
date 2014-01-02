package de.tecfem.chronic.logreader;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import de.tecfem.chronic.replay.LogLineData;

public class CombinedWithDurationLogLineReaderTest {
	private static final String TEST_LINE = "127.0.0.1 - - [02/Jan/2014:11:55:12 +0100] \"GET / HTTP/1.1\" 200 481 \"-\" \"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:26.0) Gecko/20100101 Firefox/26.0\" 1000";
	CombinedWithDurationLogLineReader combinedWithDurationLogLineReader = new CombinedWithDurationLogLineReader();

	@Test
	public void testParseLine() {
		LogLineData logLineData = combinedWithDurationLogLineReader.parseLine(TEST_LINE);

		assertThat(logLineData.getTime()).isEqualTo(1388660112000L);
		assertThat(logLineData.getRequestMethod()).isEqualTo("GET");
		assertThat(logLineData.getRequest()).isEqualTo("/");
		assertThat(logLineData.getDuration()).isEqualTo(1);
		assertThat(logLineData.getStatusCode()).isEqualTo("200");
		assertThat(logLineData.getUserAgent()).isEqualTo("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:26.0) Gecko/20100101 Firefox/26.0");
	}
}
