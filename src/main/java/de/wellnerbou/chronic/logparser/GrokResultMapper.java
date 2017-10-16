package de.wellnerbou.chronic.logparser;

import de.wellnerbou.chronic.replay.LogLineData;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Map;

public class GrokResultMapper {

	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z");

	public LogLineData map(final Map<String, Object> map) {
		LogLineData logLineData = new LogLineData();

		logLineData.setUserAgent(String.valueOf(map.get("agent")));
		logLineData.setRequest(String.valueOf(map.get("request")));
		logLineData.setRequestMethod(String.valueOf(map.get("verb")));
		logLineData.setTime(toTimestamp(String.valueOf(map.get("timestamp"))));
		logLineData.setStatusCode(String.valueOf(map.get("response")));
		if(map.containsKey("duration")) {
			logLineData.setDuration(Long.parseLong(String.valueOf(map.get("duration"))));
		}
		logLineData.setReferrer(String.valueOf(map.get("referrer")));
		logLineData.setClientip(String.valueOf(map.get("clientip")));

		return logLineData;
	}

	private long toTimestamp(final String timestampstring) {
		final TemporalAccessor temporalAccessor = dateTimeFormatter.parse(timestampstring);
		return getTimezoneUnawareUnixTimestamp(temporalAccessor);
	}

	private long getTimezoneUnawareUnixTimestamp(final TemporalAccessor temporalAccessor) {
		return temporalAccessor.getLong(ChronoField.INSTANT_SECONDS) + temporalAccessor.getLong(ChronoField.OFFSET_SECONDS);
	}
}
