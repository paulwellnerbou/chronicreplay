package de.wellnerbou.chronic.replay;

import com.google.gson.Gson;
import com.ning.http.client.Response;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

public class JsonResultDataLogger implements ResultDataLogger {
    private static final Logger RESULTDATA_JSON = LoggerFactory.getLogger("RESULTDATA_JSON");
    private static final Logger RESULTDATA_SAMESTATUS = LoggerFactory.getLogger("RESULTDATA_SAMESTATUS");

    @Override
    public void logResultDataLine(LogLineData originalData, Response response, Boolean sameStatus, long duration, long startTime) {
        String uriString;
        JsonLogLineData jsonData = new JsonLogLineData();
        uriString = response.getUri().toString();
        jsonData.startTime = new DateTime(startTime).toString();
        jsonData.originalStartTime = new DateTime(originalData.getTime()).toString();
        jsonData.statusCode = response.getStatusCode();
        jsonData.originalStatusCode = originalData.getStatusCode();
        jsonData.sameStatus = sameStatus;
        jsonData.duration = duration;
        jsonData.originalDuration = originalData.getDuration();
        jsonData.difference = jsonData.duration-jsonData.originalDuration;
        jsonData.request = uriString;
        logResultDataLine(jsonData, RESULTDATA_JSON);
        if(sameStatus) {
            logResultDataLine(jsonData, RESULTDATA_SAMESTATUS);
        }
    }

    private void logResultDataLine(JsonLogLineData jsonData, final Logger logger) {
        Gson gson = new Gson();
        logger.debug(gson.toJson(jsonData));
    }
}
