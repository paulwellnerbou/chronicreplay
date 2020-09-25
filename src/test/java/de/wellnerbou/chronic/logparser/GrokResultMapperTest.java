package de.wellnerbou.chronic.logparser;

import de.wellnerbou.chronic.replay.LogLineData;
import org.assertj.core.api.Assertions;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;

public class GrokResultMapperTest {

	private GrokResultMapper grokResultMapper;

	@BeforeClass
	public static void before() {
		System.setProperty("user.timezone", "UTC");
	}

	@Before
	public void setUp() {
		grokResultMapper = new GrokResultMapper();
	}

	@Test
	public void testMap() {
		Map<String, Object> map = new TreeMap<>();
		map.put("agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.152 Safari/537.22");
		map.put("clientip", "112.169.19.192");
		map.put("request", "/");
		map.put("timestamp", "06/Mar/2013:01:36:30 +0900");
		map.put("verb", "GET");
		map.put("referrer", "-");
		map.put("rawrequest", null);
		map.put("response", "200");

		LogLineData logLineData = grokResultMapper.map(map);

		final LogLineData expectedLoglineData = new LogLineData();
		expectedLoglineData.setTime(1362533790000L);
		expectedLoglineData.setRequest("/");
		expectedLoglineData.setRequestMethod("GET");
		expectedLoglineData.setUserAgent(String.valueOf(map.get("agent")));
		expectedLoglineData.setClientip(String.valueOf(map.get("clientip")));
		expectedLoglineData.setReferrer(String.valueOf(map.get("referrer")));
		expectedLoglineData.setStatusCode(String.valueOf(map.get("response")));
		Assertions.assertThat(logLineData).isEqualToComparingFieldByField(expectedLoglineData);
	}

	@Test
	public void testCorrectTimeMapping() {
		Map<String, Object> map = new TreeMap<>();
		map.put("timestamp", "06/Mar/2013:01:36:30 +0900");

		LogLineData logLineData = grokResultMapper.map(map);
		Assertions.assertThat(logLineData.getTime()).isEqualTo(1362533790000L);

		final DateTime dateTime = new DateTime(logLineData.getTime());
		Assertions.assertThat(dateTime.toString()).isEqualTo("2013-03-06T02:36:30.000+01:00");
	}

	@Test
	public void testCorrectTimeMapping_timezoneUnaware() {
		Map<String, Object> map = new TreeMap<>();
		map.put("timestamp", "18/Sep/2020:03:45:01 +0200");

		LogLineData logLineData = grokResultMapper.map(map);
		Assertions.assertThat(logLineData.getTime()).isEqualTo(1600400701000L);

		final DateTime dateTime = new DateTime(logLineData.getTime());
		Assertions.assertThat(dateTime.toString()).isEqualTo("2020-09-18T05:45:01.000+02:00");
	}
}
