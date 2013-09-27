package de.tecfem.chronic.replay;

public class LogLineData {
	private long time;
	private String requestMethod;
	private String request;
	private long duration;
	private String statusCode;

	public long getTime() {
		return time;
	}

	public void setTime(final long time) {
		this.time = time;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(final String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(final String request) {
		this.request = request;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(final long duration) {
		this.duration = duration;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
}
