package de.tecfem.chronic.logreader;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.annotations.VisibleForTesting;

import de.tecfem.chronic.replay.LogLineData;

/**
 * Implementation of LogLineReader supporting apache log files written in following format, defined in
 * mod_log_config.conf:
 * 
 * <pre>
 * LogFormat "%h %l %u %t \"%r\" %>s %b"
 * </pre>
 * 
 * Example:
 * 
 * <pre>
 * 127.0.0.1 - - [02/Jan/2014:11:55:12 +0100] "GET / HTTP/1.1" 200 481
 * </pre>
 * 
 * This format is defined by apache as the default log format, named Common Log Format (CLF). This is documented on
 * <a href="http://httpd.apache.org/docs/current/mod/mod_log_config.html">http://httpd.apache.org/docs/current/mod/
 * mod_log_config.html</a>.
 * 
 * Unfortunately, there is no request duration time information in this log message, so it won't be possible to compare
 * the original and the actual time, you will just be able to simulate traffic on your target system.
 *
 * @author Paul Wellner Bou <pwb@faz.net>
 */
public class CommonLogFormatLogLineReader implements LogLineReader {

	static final DateTimeFormatter format = DateTimeFormat.forPattern("[dd/MMM/yyyy:HH:mm:ss");

	/*
	 * (non-Javadoc)
	 *
	 * @see de.tecfem.chronic.logreader.LogLineReader#parseLine(java.lang.String)
	 */
	@Override
	public LogLineData parseLine(final String logLine) {
		LogLineData logLineData = new LogLineData();
		String[] parts = logLine.split("\\s");

		logLineData.setTime(formatDate(parts[3]));
		logLineData.setRequestMethod(parts[5].replace("\"", ""));
		logLineData.setRequest(parts[6]);
		logLineData.setStatusCode(parts[8]);
		return logLineData;
	}

	@VisibleForTesting
	protected long formatDate(final String s) {
		return format.parseMillis(s);
	}

	@Override
	public String getId() {
		return "clf";
	}

}
