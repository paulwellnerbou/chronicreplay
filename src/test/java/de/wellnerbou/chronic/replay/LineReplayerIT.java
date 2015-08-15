package de.wellnerbou.chronic.replay;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import com.ning.http.client.providers.apache.ApacheAsyncHttpProvider;
import com.ning.http.client.providers.grizzly.GrizzlyAsyncHttpProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class LineReplayerIT {

	LineReplayer lineReplayer;
    AsyncHttpClient asyncHttpClient;

	@Before
	public void setUp() {
        AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder().build();
        asyncHttpClient = new AsyncHttpClient(new GrizzlyAsyncHttpProvider(config));
        lineReplayer = new LineReplayer("http://w3.org", asyncHttpClient);
	}

    @After
    public void tearDown() throws InterruptedException {
        asyncHttpClient.closeAsynchronously();
        while(!asyncHttpClient.isClosed()) {
            Thread.sleep(100);
        }
    }

	@Test
	public void testApi() throws IOException, ExecutionException, InterruptedException {
        String host = "http://ba10-pol.tecfem.de";
        String request = "/polopoly_fs/1.2172444!/image/1683812984.jpg_gen/derivatives/article_teaser_gross/1683812984.jpg";
        AsyncHttpClient.BoundRequestBuilder req = asyncHttpClient.prepareGet(host + request);
        // req.setFollowRedirects(true);
        LogLineData logLineData = new LogLineData();
        logLineData.setRequest(request);
        ListenableFuture<Response> future = req.execute(new LoggingAsyncCompletionHandler(logLineData));
        // Response r = future.get();
	}

}
