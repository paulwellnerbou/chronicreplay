package de.wellnerbou.chronic.replay;

import org.assertj.core.api.Assertions;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

public class ChronicReplayTest {

	@BeforeClass
	public static void before() {
		System.setProperty("user.timezone", "UTC");
	}

	@Test
	public void testGetLoggingDiscriminatorVariable() {
		String testString = "http://subdomain.example.com/path";
		String stripped = ChronicReplay.getLoggingDiscriminatorVariable(testString);
		assertThat(stripped).startsWith("subdomain.example.com-path");
	}

	@Test
	public void test_jodaTimeApi() {
		DateTime dateTime = new DateTime(1380103200 * 1000L);
		Assertions.assertThat(dateTime.getHourOfDay()).isEqualTo(10);
		Assertions.assertThat(dateTime.getMinuteOfHour()).isEqualTo(0);
	}

	@Test
	public void test_jodaTimeApi_compare1() {
		DateTime dateTime = new DateTime(1380103200 * 1000L);
		DateTime earlierDateTime = DateTime.parse("11:59:59", DateTimeFormat.forPattern("HH:mm:ss"));

		Assertions.assertThat(earlierDateTime.isBefore(dateTime));
	}

	@Test
	public void test_jodaTimeApi_compare2() {
		final String str = "abc";
		DateTime dateTime = new DateTime(1380103200 * 1000L);
		DateTime laterDateTime = DateTime.parse("12:00:01", DateTimeFormat.forPattern("HH:mm:ss"));

		Assertions.assertThat(laterDateTime.isAfter(dateTime));
	}

	@Test
	public void test_convertUntil() {
		assertThat(new ChronicReplay().convertToDateTime("22:00:00").isAfter(new DateTime(1380103200 * 1000L)));
	}
}
