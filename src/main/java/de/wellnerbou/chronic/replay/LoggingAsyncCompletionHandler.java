package de.wellnerbou.chronic.replay;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingAsyncCompletionHandler extends AsyncCompletionHandler<Response> {

	private static final Logger LOG = LoggerFactory.getLogger(LoggingAsyncCompletionHandler.class);

	private long startTime;
	private LogLineData originalData;
	private ResultDataLogger resultDataLogger;

	public LoggingAsyncCompletionHandler(final LogLineData originalData, final ResultDataLogger resultDataLogger) {
		this.resultDataLogger = resultDataLogger;
		this.startTime = System.currentTimeMillis();
		this.originalData = originalData;
	}

	@Override
	public STATE onBodyPartReceived(final HttpResponseBodyPart content) throws Exception {
		// Don't store the content anyway, saving memory. We want to receive it, but nothing else.
		return STATE.CONTINUE;
	}

    @Override
    public STATE onHeadersReceived(final HttpResponseHeaders headers) throws Exception {
        return super.onHeadersReceived(headers);
    }

    public void onThrowable(Throwable t) {
        long duration = System.currentTimeMillis() - startTime;
        LOG.info("Request {} failed after {}ms", originalData.getRequest(), duration, t);
        super.onThrowable(t);
    }

	@Override
	public Response onCompleted(final Response response) throws Exception {
		final long duration = System.currentTimeMillis() - startTime;
		Boolean sameStatus = null;
		try {
			sameStatus = Integer.parseInt(originalData.getStatusCode()) == response.getStatusCode();
		} catch (NumberFormatException e) {
			LOG.warn("Unable to parse original status code to int: {}", originalData.getStatusCode());
		}
		resultDataLogger.logResultDataLine(originalData, response, sameStatus, duration, startTime);
		LOG.info("Status={} OriginalStatus={} SameStatus={} Duration={} OriginalDuration={} Difference={} Request={}", response.getStatusCode(), originalData.getStatusCode(),
				sameStatus, duration,
				originalData.getDuration(),
				duration - originalData.getDuration(), response.getUri().toASCIIString());
		return response;
	}
}
