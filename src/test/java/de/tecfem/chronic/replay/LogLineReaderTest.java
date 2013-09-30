package de.tecfem.chronic.replay;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.tecfem.chronic.replay.LogLineData;
import de.tecfem.chronic.replay.LogLineReader;

public class LogLineReaderTest {

	LogLineReader logReaderReplay = new LogLineReader();

	@Before
	public void setUp() {
	}

	@Test
	public void testFormatDate() {
		long time = logReaderReplay.formatDate("[25/Sep/2013:12:00:02");
		assertThat(time).isEqualTo(1380103202000L);
	}

	@Test
	public void testReadLine() {
		String testLine = "80.219.17.55 - - [25/Sep/2013:12:00:02 +0200] \"GET /aktuell/lebensstil/leib-seele/40-days-of-dating-eins-minus-eins-12586789.html?ot=de.faz.ot.www.Gallery&offset=2&ignoreCurrentOffset=true&cid=1.2586789&fazgets_pct=Bildergalerie HTTP/1.1\" 200 3464 \"http://www.faz.net/aktuell/lebensstil/leib-seele/40-days-of-dating-eins-minus-eins-12586789.html\" \"Mozilla/5.0 (iPad; CPU OS 6_1 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10B141 Safari/8536.25\" \"SSL:-\" 33685 80.219.17.55, 10.50.160.80 10.50.180.45";
		LogLineData logLineData = logReaderReplay.readLine(testLine);

		assertThat(logLineData.getTime()).isEqualTo(1380103202000L);
		assertThat(logLineData.getRequestMethod()).isEqualTo("GET");
		assertThat(logLineData.getRequest())
				.isEqualTo(
						"/aktuell/lebensstil/leib-seele/40-days-of-dating-eins-minus-eins-12586789.html?ot=de.faz.ot.www.Gallery&offset=2&ignoreCurrentOffset=true&cid=1.2586789&fazgets_pct=Bildergalerie");
		assertThat(logLineData.getDuration()).isEqualTo(34);
	}

	@Test
	public void testReadLineWithSsl1() {
		String testLine = "87.155.64.163 - - [25/Sep/2013:12:00:04 +0200] \"GET /mein-faz-net/musterdepot-watchlist/overview/ HTTP/1.1\" 200 7492 \"https://www.faz.net/mein-faz-net/musterdepot-watchlist/\" \"Opera/9.80 (Windows NT 6.2; WOW64) Presto/2.12.388 Version/12.16\" \"SSL:1\" 224040 10.50.160.80 10.50.180.45";
		LogLineData logLineData = logReaderReplay.readLine(testLine);

		assertThat(logLineData.getTime()).isEqualTo(1380103204000L);
		assertThat(logLineData.getRequestMethod()).isEqualTo("GET");
		assertThat(logLineData.getRequest()).isEqualTo("/mein-faz-net/musterdepot-watchlist/overview/");
		assertThat(logLineData.getDuration()).isEqualTo(224L);
	}
}
