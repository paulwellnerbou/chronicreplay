package de.wellnerbou.chronic.replay;

import com.ning.http.client.Response;
import com.ning.http.client.uri.Uri;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Instant;

import static org.mockito.Mockito.when;

public class JsonResultDataLoggerTest {

    JsonResultDataLogger jsonResultDataLogger = new JsonResultDataLogger();

    @Test
    public void testJsonLogging() {
        final LogLineData logLineData = new LogLineData();
        logLineData.setTime(Instant.now().toEpochMilli());

        final Response response = Mockito.mock(Response.class);
        when(response.getUri()).thenReturn(Uri.create("http://www.example.com"));

        jsonResultDataLogger.logResultDataLine(logLineData, response, Boolean.TRUE, 10, Instant.now().toEpochMilli());
    }
}
