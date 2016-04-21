package de.wellnerbou.chronic.logparser;

import com.google.gson.stream.JsonReader;
import de.wellnerbou.chronic.replay.LogLineData;
import org.fest.assertions.api.Assertions;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

/**
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public class ElasticSearchJsonLogLineParserTest {

	final ElasticSearchJsonLogLineParser elasticSearchJsonLogLineParser = new ElasticSearchJsonLogLineParser();

	@Test
	public void parseLine() throws Exception {
		final String resourceUrlStr = "/elasticsearch-example.json";
		final InputStream is = this.getClass().getResourceAsStream(resourceUrlStr);

		final JsonReader jsonReader = new JsonReader(new InputStreamReader(is));
		final LogLineData lineData = elasticSearchJsonLogLineParser.parseLine(jsonReader);

		final LogLineData expected = new LogLineData();
		expected.setTime(1460429014000L);
		expected.setDuration(141546L);
		expected.setStatusCode("200");
		expected.setRequestMethod("GET");
		expected.setRequest("/one");
		expected.setHost("example.com");
		expected.setReferrer("http://referrer.example.com/1");
		expected.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");
		Assertions.assertThat(lineData).isEqualsToByComparingFields(expected);
	}

	@Test
	public void parseLine2() throws Exception {
		final String resourceUrlStr = "/test.json";
		final InputStream is = this.getClass().getResourceAsStream(resourceUrlStr);

		final JsonReader jsonReader = new JsonReader(new InputStreamReader(is));
		final LogLineData lineData = elasticSearchJsonLogLineParser.parseLine(jsonReader);

		final LogLineData expected = new LogLineData();
		expected.setTime(1460350800000L);
		expected.setDuration(0);
		expected.setStatusCode("304");
		expected.setRequestMethod("GET");
		expected.setRequest("/one");
		expected.setReferrer("-");
		expected.setHost("host1.example.com");
		expected.setUserAgent("Mozilla/4.0 (compatible;)");
		Assertions.assertThat(lineData).isEqualsToByComparingFields(expected);
	}
}
