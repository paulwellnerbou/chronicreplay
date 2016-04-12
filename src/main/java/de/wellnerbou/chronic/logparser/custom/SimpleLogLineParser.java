package de.wellnerbou.chronic.logparser.custom;

import de.wellnerbou.chronic.logparser.LogLineParser;
import de.wellnerbou.chronic.replay.LogLineData;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Simple log line reader reading lines in following format:
 * <p/>
 * <pre>DATE URL REQUEST-TIME</pre>
 *
 * Where the DATE is parseable automatically by {@link DateTime#parse(String)} or
 * has the format dd/MMM/yyyy:HH:mm:ss.
 * REQUEST-TIME the duration of the original request in milliseconds.
 *
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public class SimpleLogLineParser implements LogLineParser {

	/* (non-Javadoc)
	 * @see de.wellnerbou.chronic.logreader.LogLineParser#parseLine(java.lang.String)
	 */
	@Override
	public LogLineData parseLine(final Object logLine) {
		LogLineData logLineData = new LogLineData();
		String[] parts = logLine.toString().split("\\s");

		logLineData.setTime(formatDate(parts[0]));
		logLineData.setRequestMethod("GET");
		logLineData.setRequest(parts[1]);
		logLineData.setStatusCode("200");
		logLineData.setDuration(extractMilliseconds(parts));
		return logLineData;
	}

	private long formatDate(final String part) {
		if(part.contains("/")) {
			return DateTime.parse(part, DateTimeFormat.forPattern("dd/MM/yyyy'T'HH:mm:ss")).getMillis();
		} else {
			return DateTime.parse(part).getMillis();
		}
	}

	private long extractMilliseconds(final String[] parts) {
		try {
			if (parts.length > 2) {
				return Long.parseLong(parts[2]);
			}
		} catch (NumberFormatException e) {
			// ignore
		}
		return 0;
	}

	@Override
	public String getId() {
		return "simple";
	}
}
