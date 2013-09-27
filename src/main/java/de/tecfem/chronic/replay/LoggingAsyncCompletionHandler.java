package de.tecfem.chronic.replay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.Response;

public class LoggingAsyncCompletionHandler extends AsyncCompletionHandler<Response> {

	private static final Logger LOG = LoggerFactory.getLogger(LoggingAsyncCompletionHandler.class);

	private long startTime;
	private LogLineData originalData;

	public LoggingAsyncCompletionHandler(final LogLineData originalData) {
		this.startTime = System.currentTimeMillis();
		this.originalData = originalData;
	}

	@Override
	public STATE onBodyPartReceived(final HttpResponseBodyPart content) throws Exception {
		// Don't store the content anyway, saving memory. We want to receive it, but nothing else.
		return STATE.CONTINUE;
	}

	@Override
	public Response onCompleted(final Response response) throws Exception {
		long duration = System.currentTimeMillis() - startTime;
		LOG.info("Status={} OriginalStatus={} Duration={} OriginalDuration={} Difference={} Request={}", response.getStatusCode(), originalData.getStatusCode(), duration,
				originalData.getDuration(),
				duration - originalData.getDuration(), response
				.getUri().toASCIIString());
		return response;
	}
}
