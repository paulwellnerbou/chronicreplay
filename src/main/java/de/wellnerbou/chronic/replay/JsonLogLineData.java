package de.wellnerbou.chronic.replay;

import org.joda.time.DateTime;

public class JsonLogLineData {

	public DateTime startTime;
	public DateTime originalTime;
	public int statusCode;
	public String originalStatusCode;
	public Boolean sameStatus;
	public Long duration;
	public Long originalDuration;
	public Long difference;
	public String request;
}
