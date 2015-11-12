package de.wellnerbou.chronic.replay;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LineReplayer {

	private static final Logger LOG = LoggerFactory.getLogger(LineReplayer.class);

	private String host;
	private String hostHeader = null;
	private AsyncHttpClient asyncHttpClient;

	private boolean followRedirects = false;

	public LineReplayer(final String host, final AsyncHttpClient asyncHttpClient) {
		this.host = host;
		this.asyncHttpClient = asyncHttpClient;
	}

	public ListenableFuture<Response> replay(final LogLineData logLineData) throws IOException {
		BoundRequestBuilder req = asyncHttpClient.prepareGet(host + logLineData.getRequest());
		req.setFollowRedirects(followRedirects);
		if (hostHeader != null) {
			req = req.setVirtualHost(hostHeader);
		}
		if (logLineData.getUserAgent() != null) {
			req.setHeader("user-agent", logLineData.getUserAgent());
		}
		LOG.info("Executing request {}: {} with host header {}", req, host + logLineData.getRequest(), hostHeader);
		return req.execute(new LoggingAsyncCompletionHandler(logLineData));
	}

	public void setHostHeader(final String hostHeader) {
		this.hostHeader = hostHeader;
	}

	public void setFollowRedirects(boolean followRedirects) {
		this.followRedirects = followRedirects;
	}
}
