package de.wellnerbou.chronic.logparser;

import com.google.common.collect.Maps;
import de.wellnerbou.chronic.replay.LogLineData;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class GrokResultMapperTest {

	private GrokResultMapper grokResultMapper;

	@Before
	public void setUp() {
		grokResultMapper = new GrokResultMapper();
	}

	@Test
	public void testMap() {
		Map<String, Object> map = Maps.newTreeMap();
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
		expectedLoglineData.setTime(1362533790);
		expectedLoglineData.setRequest("/");
		expectedLoglineData.setRequestMethod("GET");
		expectedLoglineData.setUserAgent(String.valueOf(map.get("agent")));
		expectedLoglineData.setClientip(String.valueOf(map.get("clientip")));
		expectedLoglineData.setReferrer(String.valueOf(map.get("referrer")));
		expectedLoglineData.setStatusCode(String.valueOf(map.get("response")));
		Assertions.assertThat(logLineData).isEqualToComparingFieldByField(expectedLoglineData);
	}
}
