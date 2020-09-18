package de.wellnerbou.chronic.replay;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostRequestBuilder {

    private final Map<String, String> hostmap = new HashMap<>();
    private String defaultHost = "";

    public HostRequestBuilder(final String host) {
        this(host, null);
    }

    public HostRequestBuilder(final String host, final List<String> hostmaplist) {
        this.defaultHost = host != null ? host : "";
        if (hostmaplist != null) {
            hostmaplist.forEach(s -> {
                final String[] split = s.split(":");
                if (split.length == 2) {
                    hostmap.put(split[0], split[1]);
                }
            });
        }
    }

    public String getHost(final String request) throws URISyntaxException {
        if (request.startsWith("http://") || request.startsWith("https://")) {
            final URI uri = new URI(request);
            if (hostmap.containsKey(uri.getHost())) {
                return uri.getScheme() + "://" + hostmap.get(uri.getHost());
            }
            if (hostmap.containsKey(uri.getScheme() + "://" + uri.getHost())) {
                return hostmap.get(uri.getScheme() + "://" + uri.getHost());
            }
        }

        return this.defaultHost;
    }

    String requestTarget(String requestTarget) throws URISyntaxException {
        final String host = getHost(requestTarget);
        if (host.length() > 0 && (requestTarget.startsWith("http://") || requestTarget.startsWith("https://"))) {
            final URI uri = new URI(host);
            final URIBuilder uriBuilder = new URIBuilder(requestTarget).setHost(uri.getHost()).setScheme(uri.getScheme());
            if (uri.getPath() != null && uri.getPath().length() > 0) {
                uriBuilder.setPath(uri.getPath() + uriBuilder.getPath());
            }
            return uriBuilder.toString();
        }
        return requestTarget;
    }
}
