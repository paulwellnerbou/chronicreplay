package de.tecfem.chronic.replay;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class LogLineReader {

	DateTimeFormatter format = DateTimeFormat.forPattern("[dd/MMM/yyyy:HH:mm:ss");

	public LogLineData readLine(final String logLine) {
		LogLineData logLineData = new LogLineData();
		String[] parts = logLine.split("\\s");

		logLineData.setTime(formatDate(parts[3]));
		logLineData.setRequestMethod(parts[5].replace("\"", ""));
		logLineData.setRequest(parts[6]);
		logLineData.setStatusCode(parts[8]);
		logLineData.setDuration(extractDuration(logLine));
		return logLineData;
	}

	private long formatDuration(final String s) {
		Double dur = Long.parseLong(s) / 1000D;
		return Math.round(dur);
	}

	private long extractDuration(final String line) {
		String substr = line.replaceFirst("^.*\"SSL:-\" ", "");
		String number = substr.replaceFirst(" .*$", "");
		return formatDuration(number);
	}

	public long formatDate(final String s) {
		return format.parseMillis(s);
	}

}
