package de.wellnerbou.chronic.replay;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostRequestBuilder {

    private final Map<String, String> hostmap = new HashMap<>();
    private final String targetHost;

    public HostRequestBuilder(final String host) {
        this(host, null);
    }

    public HostRequestBuilder(final String host, final List<String> hostmaplist) {
        this.targetHost = host != null ? host : "";
        if (hostmaplist != null) {
            hostmaplist.forEach(s -> {
                final String[] split = s.split(":");
                if (split.length == 2) {
                    hostmap.put(split[0], split[1]);
                }
            });
        }
    }

    public String getTargetHost(final String request) throws URISyntaxException {
        if(targetHost != null) {
            return targetHost;
        }

        return getVirtualHost(request);
    }

    public String getVirtualHost(final String request) throws URISyntaxException {
        if (request.startsWith("http://") || request.startsWith("https://")) {
            final URI uri = new URI(request);
            if (hostmap.containsKey(uri.getHost())) {
                return prefixScheme(uri, hostmap.get(uri.getHost()));
            }
            if (hostmap.containsKey(prefixScheme(uri, uri.getHost()))) {
                return prefixScheme(uri, hostmap.get(prefixScheme(uri, uri.getHost())));
            }
            if(hostmap.containsKey("*")) {
                return prefixScheme(uri, hostmap.get("*"));
            }
        }

        return this.targetHost;
    }

    private String prefixScheme(final URI uri, String mappedHost) {
        if (mappedHost.startsWith("http://") || mappedHost.startsWith("https://")) {
            return mappedHost;
        } else {
            return uri.getScheme() + "://" + mappedHost;
        }
    }

    String requestTarget(String requestTarget) throws URISyntaxException {
        final String host = getTargetHost(requestTarget);
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
