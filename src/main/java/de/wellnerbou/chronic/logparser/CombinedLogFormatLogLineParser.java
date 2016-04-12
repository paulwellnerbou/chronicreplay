package de.wellnerbou.chronic.logparser;

import com.google.common.annotations.VisibleForTesting;

import de.wellnerbou.chronic.replay.LogLineData;

/**
 * Implementation of LogLineParser supporting apache log files written in following format, defined in
 * mod_log_config.conf:
 * 
 * <pre>
 * LogFormat "%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-agent}i\""
 * </pre>
 * 
 * Example:
 * 
 * <pre>
 * 127.0.0.1 - - [02/Jan/2014:11:55:12 +0100] "GET / HTTP/1.1" 200 481 "-" "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:26.0) Gecko/20100101 Firefox/26.0"
 * </pre>
 * 
 * This format is defined by apache as the default log format, named NCSA extended/combined log format. This is
 * documented on
 * <a href="http://httpd.apache.org/docs/current/mod/mod_log_config.html">http://httpd.apache.org/docs/current/mod/
 * mod_log_config.html</a>.
 * 
 * Unfortunately, there is no request duration time information in this log message, so it won't be possible to compare
 * the original and the actual time, you will just be able to simulate traffic on your target system.
 * 
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public class CombinedLogFormatLogLineParser extends CommonLogFormatLogLineParser {

	/* (non-Javadoc)
	 * @see de.wellnerbou.chronic.logreader.LogLineParser#parseLine(java.lang.String)
	 */
	@Override
	public LogLineData parseLine(final String logLine) {
		LogLineData logLineData = new LogLineData();
		String[] parts = logLine.split("\\s");

		logLineData.setTime(formatDate(parts[3]));
		logLineData.setRequestMethod(parts[5].replace("\"", ""));
		logLineData.setRequest(parts[6]);
		logLineData.setStatusCode(parts[8]);
		logLineData.setUserAgent(getUserAgent(parts));
		return logLineData;
	}

	@VisibleForTesting
	protected String getUserAgent(final String[] parts) {
		int index = 11;
		StringBuffer stringBuffer = new StringBuffer(parts[index++]);
		while (stringBuffer.charAt(stringBuffer.length() - 1) != '"' && index < parts.length) {
			stringBuffer.append(" " + parts[index++]);
		}

		if ('"' == stringBuffer.charAt(0)) {
			stringBuffer = stringBuffer.deleteCharAt(0);
		}

		if ('"' == stringBuffer.charAt(stringBuffer.length() - 1)) {
			stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
		}

		return stringBuffer.toString();
	}

	@Override
	public String getId() {
		return "combined";
	}
}
