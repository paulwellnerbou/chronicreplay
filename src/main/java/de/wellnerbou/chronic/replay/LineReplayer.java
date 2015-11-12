package de.wellnerbou.chronic.replay;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;

public class LineReplayer {

	private static final Logger LOG = LoggerFactory.getLogger(LineReplayer.class);

	private String host;
	private String hostHeader = null;
	private Header header = null;
	private AsyncHttpClient asyncHttpClient;
	private ResultDataLogger resultDataLogger;

	public LineReplayer(final String host, final AsyncHttpClient asyncHttpClient, final ResultDataLogger resultDataLogger) {
		this.host = host;
		this.asyncHttpClient = asyncHttpClient;
		this.resultDataLogger = resultDataLogger;
	}

	public void replay(final LogLineData logLineData) throws IOException {
		BoundRequestBuilder req = asyncHttpClient.prepareGet(host + logLineData.getRequest());
		if (hostHeader != null) {
			req = req.setVirtualHost(hostHeader);
		}
		if(header != null) {
			req = req.setHeader(header.getName(), header.getValue());
		}
		if (logLineData.getUserAgent() != null) {
			req.setHeader("user-agent", logLineData.getUserAgent());
		}
		LOG.debug("Executing request {}: {} with host header {}", req, host + logLineData.getRequest(), hostHeader);
		req.execute(new LoggingAsyncCompletionHandler(logLineData, resultDataLogger));
	}

	public void setHostHeader(final String hostHeader) {
		this.hostHeader = hostHeader;
	}

	public void setHeader(final String header) {
		if(header != null) {
			final String[] headerNameValue = header.split(":");
			this.header = new Header(headerNameValue[0], headerNameValue[1]);
		}
	}
}
