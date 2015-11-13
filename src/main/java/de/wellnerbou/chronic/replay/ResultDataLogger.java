package de.wellnerbou.chronic.replay;

import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

public class ResultDataLogger {
	private static final Logger RESULTDATA = LoggerFactory.getLogger("RESULTDATA");
	private static final Logger RESULTDATA_SAMESTATUS = LoggerFactory.getLogger("RESULTDATA_SAMESTATUS");

	public void logColumnTitles() {
		logColumnTitles(RESULTDATA);
		logColumnTitles(RESULTDATA_SAMESTATUS);
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

	public void logResultDataLine(LogLineData originalData, Response response, Boolean sameStatus, long duration, long startTime) {
		String uriString;
		try {
			uriString = response.getUri().toASCIIString();
		} catch (MalformedURLException e) {
			uriString = e.getMessage();
		}
		logResultDataLine(originalData, response, sameStatus, duration, startTime, uriString, RESULTDATA);
		if(sameStatus) {
			logResultDataLine(originalData, response, sameStatus, duration, startTime, uriString, RESULTDATA_SAMESTATUS);
		}
	}

	private void logResultDataLine(LogLineData originalData, Response response, Boolean sameStatus, long duration, long startTime, final String uriString, final Logger logger) {
		logger.debug("{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}",
				startTime / 1000,
				originalData.getTime() / 1000,
				response.getStatusCode(), originalData.getStatusCode(),
				sameStatus, duration,
				originalData.getDuration(),
				duration - originalData.getDuration(),
				uriString);
	}
}
