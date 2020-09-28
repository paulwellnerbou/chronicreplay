package de.wellnerbou.chronic.replay;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.NameResolver;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineReplayer {

    public static final String USER_AGENT_HEADER_NAME = "user-agent";
    private static final Logger LOG = LoggerFactory.getLogger(LineReplayer.class);
    private final NameResolver resolver;

    private String hostHeader = null;
    private String customUserAgent = null;
    private List<Header> headers = new ArrayList<>();
    private final HostRequestBuilder hostRequestBuilder;
    private final AsyncHttpClient asyncHttpClient;
    private final ResultDataLogger resultDataLogger;

    private boolean followRedirects = false;

    public LineReplayer(final HostRequestBuilder hostRequestBuilder, final AsyncHttpClient asyncHttpClient, final ResultDataLogger resultDataLogger, final String resolve) {
        this.hostRequestBuilder = hostRequestBuilder;
        this.asyncHttpClient = asyncHttpClient;
        this.resultDataLogger = resultDataLogger;
        this.resolver = resolve != null ? new FixedHostResolver(resolve) : null;
    }

    public ListenableFuture<Response> replay(final LogLineData logLineData) throws URISyntaxException {
        final String requestTarget = hostRequestBuilder.requestTarget(logLineData.getRequest());
        BoundRequestBuilder req = asyncHttpClient.prepareGet(requestTarget);
        req.setFollowRedirects(followRedirects);
        if (resolver != null) {
            req.setNameResolver(resolver);
        }

        String usedHostHeader = detectVirtualHostHeader(logLineData);
        req = req.setVirtualHost(usedHostHeader);

        if (customUserAgent != null) {
            req.setHeader(USER_AGENT_HEADER_NAME, customUserAgent);
        } else if (logLineData.getUserAgent() != null) {
            req.setHeader(USER_AGENT_HEADER_NAME, logLineData.getUserAgent());
        }

        for (final Header header : headers) {
            if (header != null && header.getName() != null) {
                req = req.setHeader(header.getName(), header.getValue());
            }
        }

        LOG.info("Executing request {}: {} with host headers {}", req, requestTarget, usedHostHeader);
        return req.execute(new LoggingAsyncCompletionHandler(logLineData, resultDataLogger));
    }

    private String detectVirtualHostHeader(final LogLineData logLineData) throws URISyntaxException {
        if (logLineData.getHost() != null) {
            return logLineData.getHost();
        }
        if (hostHeader != null) {
            return hostHeader;
        }
        final String virtualHostScheme = hostRequestBuilder.getVirtualHostScheme(logLineData.getRequest());
        if (virtualHostScheme != null && (virtualHostScheme.startsWith("http://") || virtualHostScheme.startsWith("https://"))) {
            return new URI(virtualHostScheme).getHost();
        } else {
            return virtualHostScheme;
        }
    }

    public void setCustomUserAgent(final String customUserAgent) {
        this.customUserAgent = customUserAgent;
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
