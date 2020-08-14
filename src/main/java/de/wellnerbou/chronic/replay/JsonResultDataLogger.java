package de.wellnerbou.chronic.replay;

import com.google.gson.Gson;
import com.ning.http.client.Response;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

public class JsonResultDataLogger implements ResultDataLogger {
    private static final Logger RESULTDATA = LoggerFactory.getLogger("RESULTDATA");
    private static final Logger RESULTDATA_SAMESTATUS = LoggerFactory.getLogger("RESULTDATA_SAMESTATUS");

    @Override
    public void logColumnTitles() {
        logColumnTitles(RESULTDATA);
        logColumnTitles(RESULTDATA_SAMESTATUS);
    }

    private void logColumnTitles(Logger logger) {
    }

    @Override
    public void logResultDataLine(LogLineData originalData, Response response, Boolean sameStatus, long duration, long startTime) {
        String uriString;
        JsonLogLineData jsonData = new JsonLogLineData();
        try {
            uriString = response.getUri().toASCIIString();
        } catch (MalformedURLException e) {
            uriString = e.getMessage();
        }
        //logResultDataLine(originalData, response, sameStatus, duration, startTime, uriString, RESULTDATA);
        jsonData.startTime = new DateTime(startTime);
        jsonData.originalStartTime = new DateTime(originalData.getTime());
        jsonData.statusCode = response.getStatusCode();
        jsonData.originalStatusCode = originalData.getStatusCode();
        jsonData.sameStatus = sameStatus;
        jsonData.duration = duration;
        jsonData.originalDuration = originalData.getDuration();
        jsonData.difference = jsonData.duration-jsonData.originalDuration;
        jsonData.request = uriString;
        logResultDataLine(jsonData, RESULTDATA);
        if(sameStatus) {
            //logResultDataLine(originalData, response, sameStatus, duration, startTime, uriString, RESULTDATA_SAMESTATUS);
            logResultDataLine(jsonData, RESULTDATA_SAMESTATUS);
        }
    }

    private void logResultDataLine(JsonLogLineData jsonData, final Logger logger) {
        Gson gson = new Gson();
        logger.debug(gson.toJson(jsonData));
    }
}
