package de.wellnerbou.chronic.replay;

import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

public class ResultDataLogger {
	private static final Logger LOG = LoggerFactory.getLogger(ResultDataLogger.class);

	public void logTitles() {
		LOG.debug("{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}",
				"StartTime",
				"OriginalStartTime",
				"StatusCode", "OriginalStatusCode",
				"SameStatus", "Duration",
				"OriginalDuration",
				"Difference",
				"Request");
	}

	public void logLine(LogLineData originalData, Response response, Boolean sameStatus, long duration, long startTime) {
		String uriString;
		try {
			uriString = response.getUri().toASCIIString();
		} catch (MalformedURLException e) {
			uriString = e.getMessage();
		}
		LOG.debug("{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}",
				startTime / 1000,
				originalData.getTime() / 1000,
				response.getStatusCode(), originalData.getStatusCode(),
				sameStatus, duration,
				originalData.getDuration(),
				duration - originalData.getDuration(),
				uriString);
	}
}
