package de.wellnerbou.chronic.logparser;

import de.wellnerbou.chronic.replay.LogLineData;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class GrokLogFormatLogLineParserTest {

	private final String logline;
	private final String ip;

	@Parameterized.Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ "127.0.0.1 - - [02/Jan/2014:11:55:12 +0100] \"GET / HTTP/1.1\" 200 481 \"-\" \"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:26.0) Gecko/20100101 Firefox/26.0\"", "127.0.0.1" }
		});
	}

	public GrokLogFormatLogLineParserTest(String logline, String ip) {
		this.logline = logline;
		this.ip = ip;
	}

	@Test
	public void testParseLine() {
		GrokLogFormatLogLineParser logLineParser = new GrokLogFormatLogLineParser();
		LogLineData logLineData = logLineParser.parseLine(logline);
		Assertions.assertThat(logLineData.getClientip()).isEqualTo(ip);
	}
}
