package de.wellnerbou.chronic.replay;

import com.ning.http.client.NameResolver;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class FixedHostResolver implements com.ning.http.client.NameResolver {

    private final String resolve;

    public FixedHostResolver(final String resolve) {
        this.resolve = resolve;
    }

    @Override
    public InetAddress resolve(final String name) throws UnknownHostException {
        return NameResolver.JdkNameResolver.INSTANCE.resolve(resolve);
    }
}
