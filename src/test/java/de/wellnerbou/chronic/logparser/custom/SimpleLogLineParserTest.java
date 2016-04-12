package de.wellnerbou.chronic.logparser.custom;

import de.wellnerbou.chronic.replay.LogLineData;
import org.fest.assertions.api.Assertions;
import org.junit.Test;

/**
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public class SimpleLogLineParserTest {

	SimpleLogLineParser simpleLogLineReader = new SimpleLogLineParser();

	@Test
	public void testParseLineException() {
		final LogLineData parsed = simpleLogLineReader.parseLine("11/11/2015T19:19:03 http://example.com 125");
		assertCorrectValues(parsed);
	}

	@Test
	public void testParseLine() {
		final LogLineData parsed = simpleLogLineReader.parseLine("2015-11-11T19:19:03 http://example.com 125");
		assertCorrectValues(parsed);
	}

	private void assertCorrectValues(final LogLineData parsed) {
		Assertions.assertThat(parsed.getTime()).isEqualTo(1447265943000L);
		Assertions.assertThat(parsed.getRequest()).isEqualTo("http://example.com");
		Assertions.assertThat(parsed.getDuration()).isEqualTo(125);
	}
}
