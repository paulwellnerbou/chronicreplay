package de.tecfem.chronic.logreader;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import de.tecfem.chronic.replay.LogLineData;

public class CommonLogFormatLogLineReaderTest {

	CommonLogFormatLogLineReader commonLogFormatLogLineReader = new CommonLogFormatLogLineReader();

	@Test
	public void testFormatDate() {
		long time = commonLogFormatLogLineReader.formatDate("[25/Sep/2013:12:00:02");
		assertThat(time).isEqualTo(1380103202000L);
	}

	@Test
	public void testParseLine() {
		String testLine = "127.0.0.1 - - [02/Jan/2014:11:55:12 +0100] \"GET / HTTP/1.1\" 200 481";
		LogLineData logLineData = commonLogFormatLogLineReader.parseLine(testLine);

		assertThat(logLineData.getTime()).isEqualTo(1388660112000L);
		assertThat(logLineData.getRequestMethod()).isEqualTo("GET");
		assertThat(logLineData.getRequest()).isEqualTo("/");
		assertThat(logLineData.getDuration()).isEqualTo(0);
		assertThat(logLineData.getStatusCode()).isEqualTo("200");
	}
}
