package de.wellnerbou.chronic.logparser.custom;

import de.wellnerbou.chronic.logparser.CombinedWithDurationLogLineParser;
import de.wellnerbou.chronic.replay.LogLineData;

/**
 * Implementation of LogLineParser supporting apache log files written in following format, defined in
 * mod_log_config.conf:
 *
 * <pre>
 * LogFormat "%{Client-IP}i %l %u %t \"%r\" %>s %b \
 * \"%{Referer}i\" \"%{User-Agent}i\" \"SSL:%{sec}i\" \
 * %D %{X-Forwarded-For}i %h"
 * </pre>
 *
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public class FazDefaultLogLineParser extends CombinedWithDurationLogLineParser {

	/* (non-Javadoc)
	 * @see de.wellnerbou.chronic.logreader.LogLineParser#parseLine(java.lang.String)
	 */
	@Override
	public LogLineData parseLine(final Object logLine) {
		final String logLineStr = castToStringOrThrowException(logLine);
		LogLineData logLineData = new LogLineData();
		String[] parts = logLineStr.split("\\s");

		logLineData.setTime(formatDate(parts[3]));
		logLineData.setRequestMethod(parts[5].replace("\"", ""));
		logLineData.setRequest(parts[6]);
		logLineData.setStatusCode(parts[8]);
		logLineData.setDuration(extractDuration(logLineStr));
		logLineData.setUserAgent(getUserAgent(parts));
		return logLineData;
	}

	private long extractDuration(final String line) {
		String substr = line.replaceFirst("^.*\"SSL:.{1}\" ", "");
		String number = substr.replaceFirst(" .*$", "");
		return formatDuration(number);
	}

	@Override
	public String getId() {
		return "faz_default";
	}

}
