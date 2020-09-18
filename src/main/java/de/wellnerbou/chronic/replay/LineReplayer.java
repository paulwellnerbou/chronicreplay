package de.wellnerbou.chronic.replay;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineReplayer {

    private static final Logger LOG = LoggerFactory.getLogger(LineReplayer.class);

    private String hostHeader = null;
    private List<Header> headers = new ArrayList<>();
    private final HostRequestBuilder hostRequestBuilder;
    private final AsyncHttpClient asyncHttpClient;
    private final ResultDataLogger resultDataLogger;

    private boolean followRedirects = false;

    public LineReplayer(final HostRequestBuilder hostRequestBuilder, final AsyncHttpClient asyncHttpClient, final ResultDataLogger resultDataLogger) {
        this.hostRequestBuilder = hostRequestBuilder;
        this.asyncHttpClient = asyncHttpClient;
        this.resultDataLogger = resultDataLogger;
    }

    public ListenableFuture<Response> replay(final LogLineData logLineData) throws IOException, URISyntaxException {
        final String requestTarget = hostRequestBuilder.requestTarget(logLineData.getRequest());
        BoundRequestBuilder req = asyncHttpClient.prepareGet(requestTarget);
        req.setFollowRedirects(followRedirects);
        String usedHostHeader = null;
        if (logLineData.getHost() == null) {
            if (hostHeader != null) {
                usedHostHeader = hostHeader;
            }
        } else {
            usedHostHeader = logLineData.getHost();
        }
        req = req.setVirtualHost(usedHostHeader);

        for (final Header header : headers) {
            req = req.setHeader(header.getName(), header.getValue());
        }

        if (logLineData.getUserAgent() != null) {
            req.setHeader("user-agent", logLineData.getUserAgent());
        }
        LOG.info("Executing request {}: {} with host headers {}", req, requestTarget, usedHostHeader);
        return req.execute(new LoggingAsyncCompletionHandler(logLineData, resultDataLogger));
    }

    public void setHostHeader(final String hostHeader) {
        this.hostHeader = hostHeader;
    }

    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public void setHeaders(final List<String> headers) {
        this.headers = headers.stream().map(input -> {
            final String[] parts = input.split(":");
            return new Header(parts[0], parts.length > 1 ? parts[1] : "");
        }).collect(Collectors.toList());
    }
}
