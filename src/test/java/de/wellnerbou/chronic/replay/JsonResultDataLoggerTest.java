package de.wellnerbou.chronic.replay;

import com.ning.http.client.Response;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;

import static org.mockito.Mockito.when;

public class JsonResultDataLoggerTest {

    JsonResultDataLogger jsonResultDataLogger = new JsonResultDataLogger();

    @Test
    public void testJsonLogging() throws URISyntaxException, MalformedURLException {
        final LogLineData logLineData = new LogLineData();
        logLineData.setDuration(20L);

        final Response response = Mockito.mock(Response.class);
        when(response.getUri()).thenReturn(new URI("http://www.example.com"));
        jsonResultDataLogger.logResultDataLine(logLineData, response, true, 30L, Instant.now().toEpochMilli());
    }
}