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
	private String pattern;
	private final String expectedIp;
	private String responseStatus;
	private Long duration;

	@Parameterized.Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{
					"127.0.0.1 - - [02/Jan/2014:11:55:12 +0100] \"GET / HTTP/1.1\" 200 481 \"-\" \"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:26.0) Gecko/20100101 Firefox/26.0\"",
					"%{COMBINEDAPACHELOG}",
					"127.0.0.1",
					"200", 0L
				}, {
					"88.215.192.113, 192.168.190.5 - - [16/Sep/2017:02:36:57 +0200] \"GET /i.2910.de.rpc?drbm:theme_ids=38 HTTP/1.1\" 200 53055 429877 \"-\" \"-\"",
					"%{IPORHOST:clientip}, %{IPORHOST:clientip2} %{USER:ident} %{USER:auth} \\[%{HTTPDATE:timestamp}\\] \"(?:%{WORD:verb} %{NOTSPACE:request}(?: HTTP/%{NUMBER:httpversion})?|%{DATA:rawrequest})\" %{NUMBER:response} (?:%{NUMBER:bytes}|-) (?:%{NUMBER:duration}|-)",
					"88.215.192.113",
					"200", 429877L
				}
		});
	}

	public GrokLogFormatLogLineParserTest(String logline, String pattern, String expectedIp, String responseStatus, Long duration) {
		this.logline = logline;
		this.pattern = pattern;
		this.expectedIp = expectedIp;
		this.responseStatus = responseStatus;
		this.duration = duration;
	}

	@Test
	public void testParseLine() {
		GrokLogFormatLogLineParser logLineParser = new GrokLogFormatLogLineParser();
		logLineParser.init(pattern, new GrokResultMapper());
		LogLineData logLineData = logLineParser.parseLine(logline);
		Assertions.assertThat(logLineData.getClientip()).isEqualTo(expectedIp);
		Assertions.assertThat(logLineData.getStatusCode()).isEqualTo(responseStatus);
		Assertions.assertThat(logLineData.getDuration()).isEqualTo(duration);
	}
}
