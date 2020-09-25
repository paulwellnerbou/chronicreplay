package de.wellnerbou.chronic.logparser.custom;

import static org.assertj.core.api.Assertions.assertThat;

import de.wellnerbou.chronic.replay.LogLineData;
import org.junit.BeforeClass;
import org.junit.Test;

public class FazDefaultLogLineParserTest {

	FazDefaultLogLineParser fazDefaultLogLineReader = new FazDefaultLogLineParser();

	@BeforeClass
	public static void before() {
		System.setProperty("user.timezone", "UTC");
	}

	@Test
	public void testParseLine() {
		String testLine = "1.1.1.1 - - [25/Sep/2013:12:00:02 +0200] \"GET /aktuell/lebensstil/leib-seele/40-days-of-dating-eins-minus-eins-12586789.html?ot=de.faz.ot.www.Gallery&offset=2&ignoreCurrentOffset=true&cid=1.2586789&fazgets_pct=Bildergalerie HTTP/1.1\" 200 3464 \"http://www.faz.net/aktuell/lebensstil/leib-seele/40-days-of-dating-eins-minus-eins-12586789.html\" \"Mozilla/5.0 (iPad; CPU OS 6_1 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10B141 Safari/8536.25\" \"SSL:-\" 33685 4.4.4.4, 2.2.2.2 3.3.3.3";
		LogLineData logLineData = fazDefaultLogLineReader.parseLine(testLine);

		assertThat(logLineData.getTime()).isEqualTo(1380103202000L);
		assertThat(logLineData.getRequestMethod()).isEqualTo("GET");
		assertThat(logLineData.getRequest())
				.isEqualTo(
						"/aktuell/lebensstil/leib-seele/40-days-of-dating-eins-minus-eins-12586789.html?ot=de.faz.ot.www.Gallery&offset=2&ignoreCurrentOffset=true&cid=1.2586789&fazgets_pct=Bildergalerie");
		assertThat(logLineData.getDuration()).isEqualTo(34);
		assertThat(logLineData.getUserAgent()).isEqualTo(
				"Mozilla/5.0 (iPad; CPU OS 6_1 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10B141 Safari/8536.25");
		assertThat(logLineData.getStatusCode()).isEqualTo("200");
	}

	@Test
	public void testParseLineWithSsl1() {
		String testLine = "1.1.1.1 - - [25/Sep/2013:12:00:04 +0200] \"GET /mein-faz-net/musterdepot-watchlist/overview/ HTTP/1.1\" 200 7492 \"https://www.faz.net/mein-faz-net/musterdepot-watchlist/\" \"Opera/9.80 (Windows NT 6.2; WOW64) Presto/2.12.388 Version/12.16\" \"SSL:1\" 224040 2.2.2.2 3.3.3.3";
		LogLineData logLineData = fazDefaultLogLineReader.parseLine(testLine);

		assertThat(logLineData.getTime()).isEqualTo(1380103204000L);
		assertThat(logLineData.getRequestMethod()).isEqualTo("GET");
		assertThat(logLineData.getRequest()).isEqualTo("/mein-faz-net/musterdepot-watchlist/overview/");
		assertThat(logLineData.getDuration()).isEqualTo(224L);
		assertThat(logLineData.getStatusCode()).isEqualTo("200");
		assertThat(logLineData.getUserAgent()).isEqualTo("Opera/9.80 (Windows NT 6.2; WOW64) Presto/2.12.388 Version/12.16");
	}
}
