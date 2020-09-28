package de.wellnerbou.chronic.replay;

import org.slf4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.slf4j.LoggerFactory.getLogger;

public class FixedHostResolver implements com.ning.http.client.NameResolver {
    private static final Logger LOG = getLogger(FixedHostResolver.class);
    private final String resolve;

    public FixedHostResolver(final String resolve) {
        this.resolve = resolve;
    }

    @Override
    public InetAddress resolve(final String name) throws UnknownHostException {
        final InetAddress resolved = JdkNameResolver.INSTANCE.resolve(this.resolve);
        LOG.info("Resolved {} to {}", name, resolved.getHostAddress());
        return resolved;
    }
}
