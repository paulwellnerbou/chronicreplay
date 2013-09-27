package de.tecfem.chronic.replay;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HttpHeaders;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;

public class LineReplayer {

	private static final Logger LOG = LoggerFactory.getLogger(LineReplayer.class);

	private String host;
	private String hostHeader = null;
	private AsyncHttpClient asyncHttpClient;

	public LineReplayer(final String host, final AsyncHttpClient asyncHttpClient) {
		this.host = host;
		this.asyncHttpClient = asyncHttpClient;
	}

	public void replayWithDelay(final LogLineData readLine, final long offset) throws IOException {
		replay(readLine);
	}

	protected long getTimeToWait(final long time, final long offset) {
		return offset - time;
	}

	public void replay(final LogLineData logLineData) throws IOException {
		BoundRequestBuilder req = asyncHttpClient.prepareGet(host + logLineData.getRequest());
		if (hostHeader != null) {
			req.addHeader(HttpHeaders.HOST, hostHeader);
		}
		LOG.debug("Executing request {}: {} with host header {}", req, host + logLineData.getRequest(), hostHeader);
		req.execute(new LoggingAsyncCompletionHandler(logLineData));
	}

	public void setHostHeader(final String hostHeader) {
		this.hostHeader = hostHeader;
	}
}
