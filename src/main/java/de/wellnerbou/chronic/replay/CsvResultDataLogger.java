package de.wellnerbou.chronic.replay;

import com.ning.http.client.Response;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

public class CsvResultDataLogger implements ResultDataLogger {
	private static final Logger RESULTDATA_CSV = LoggerFactory.getLogger("RESULTDATA_CSV");
	private static final Logger RESULTDATA_CSV_SAMESTATUS = LoggerFactory.getLogger("RESULTDATA_CSV_SAMESTATUS");

	@Override
	public void logColumnTitles() {
		logColumnTitles(RESULTDATA_CSV);
		logColumnTitles(RESULTDATA_CSV_SAMESTATUS);
	}

	private void logColumnTitles(Logger logger) {
		logger.debug("{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}",
				"StartTime",
				"OriginalStartTime",
				"StatusCode", "OriginalStatusCode",
				"SameStatus", "Duration",
				"OriginalDuration",
				"Difference",
				"Request");
	}

	@Override
	public void logResultDataLine(LogLineData originalData, Response response, Boolean sameStatus, long duration, long startTime) {
		String uriString;
		uriString = response.getUri().toString();
		logResultDataLine(originalData, response, sameStatus, duration, startTime, uriString, RESULTDATA_CSV);
		if (sameStatus) {
			logResultDataLine(originalData, response, sameStatus, duration, startTime, uriString, RESULTDATA_CSV_SAMESTATUS);
		}
	}

	private void logResultDataLine(LogLineData originalData, Response response, Boolean sameStatus, long duration, long startTime, final String uriString, final Logger logger) {
		logger.debug("{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}",
				new DateTime(startTime),
				new DateTime(originalData.getTime()),
				response.getStatusCode(), originalData.getStatusCode(),
				sameStatus, duration,
				originalData.getDuration(),
				duration - originalData.getDuration(),
				uriString);
	}
}
