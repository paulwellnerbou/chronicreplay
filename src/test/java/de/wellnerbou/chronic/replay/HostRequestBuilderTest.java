package de.wellnerbou.chronic.replay;

import org.assertj.core.util.Lists;
import org.junit.Test;

import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class HostRequestBuilderTest {

    @Test
    public void singleHostname() throws URISyntaxException {
        final HostRequestBuilder hostRequestBuilder = new HostRequestBuilder("myhost");
        assertThat(hostRequestBuilder.getVirtualHostScheme("/any/request")).isEqualTo("myhost");
    }

    @Test
    public void requestTarget_noHost_relativeUrl() throws URISyntaxException {
        final HostRequestBuilder hostRequestBuilder = new HostRequestBuilder(null);
        assertThat(hostRequestBuilder.requestTarget("/path/to")).isEqualTo("/path/to");
    }

    @Test
    public void requestTarget_noHost_absoluteUrl() throws URISyntaxException {
        final HostRequestBuilder hostRequestBuilder = new HostRequestBuilder(null);
        assertThat(hostRequestBuilder.requestTarget("http://example.com/path/to")).isEqualTo("http://example.com/path/to");
    }

    @Test
    public void requestTarget_host_absoluteUrl() throws URISyntaxException {
        final HostRequestBuilder hostRequestBuilder = new HostRequestBuilder("http://host.example.com");
        assertThat(hostRequestBuilder.requestTarget("https://example.com/path/to")).isEqualTo("http://host.example.com/path/to");
    }

    @Test
    public void requestTarget_host_absoluteUrl_pathPrefix() throws URISyntaxException {
        final HostRequestBuilder hostRequestBuilder = new HostRequestBuilder("http://host.example.com/path/prefix");
        assertThat(hostRequestBuilder.requestTarget("https://example.com/path/to?query&a#rtz")).isEqualTo("http://host.example.com/path/prefix/path/to?query&a#rtz");
    }

    @Test
    public void requestTarget_with_mapping_default_host() throws URISyntaxException {
        final HostRequestBuilder hostRequestBuilder = new HostRequestBuilder("http://host.example.com", Lists.newArrayList("example.de", "example.io"));
        assertThat(hostRequestBuilder.requestTarget("https://example.com/path/to")).isEqualTo("http://host.example.com/path/to");
    }

    @Test
    public void requestTarget_with_mapping_mapped_host() throws URISyntaxException {
        final HostRequestBuilder hostRequestBuilder = new HostRequestBuilder("http://host.example.com", Lists.newArrayList("example.com:example.io"));
        assertThat(hostRequestBuilder.requestTarget("https://example.com/path/to")).isEqualTo("http://host.example.com/path/to");
        assertThat(hostRequestBuilder.getVirtualHostScheme("https://example.com/path/to")).isEqualTo("https://example.io");
    }
}