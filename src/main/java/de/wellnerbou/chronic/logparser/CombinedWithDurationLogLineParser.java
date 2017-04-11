package de.wellnerbou.chronic.logparser;

import de.wellnerbou.chronic.replay.LogLineData;

/**
 * Implementation of LogLineParser supporting apache log files written in following format, defined in
 * mod_log_config.conf:
 *
 * <pre>
 * LogFormat "%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-agent}i\" %D"
 * </pre>
 *
 * Example:
 *
 * <pre>
 * 127.0.0.1 - - [02/Jan/2014:11:55:12 +0100] "GET / HTTP/1.1" 200 481 "-" "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:26.0) Gecko/20100101 Firefox/26.0 1000"
 * </pre>
 *
 * This format is derived from the standard apache format named NCSA extended/combined log format, enhanced by the
 * duration of the request (%D). This is
 * documented on <a href="http://httpd.apache.org/docs/current/mod/mod_log_config.html">http://httpd.apache.org/docs/current/mod/mod_log_config.html</a>.
 *
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public class CombinedWithDurationLogLineParser extends CombinedLogFormatLogLineParser {

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

	/**
	 * %D is the duration in microseconds
	 *
	 * @param s
	 * @return the duration in milliseconds
	 */
	protected long formatDuration(final String s) {
		Double dur = Long.parseLong(s) / 1000D;
		return Math.round(dur);
	}

	private long extractDuration(final String line) {
		String substr = line.substring(line.lastIndexOf(' ') + 1);
		return formatDuration(substr);
	}

	@Override
	public String getId() {
		return "combined-with-duration";
	}

}
