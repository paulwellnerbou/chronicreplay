package de.wellnerbou.chronic.replay;

import com.ning.http.client.Response;

public interface ResultDataLogger {
    default void logColumnTitles(){
    }

    void logResultDataLine(LogLineData originalData, Response response, Boolean sameStatus, long duration, long startTime);
}
