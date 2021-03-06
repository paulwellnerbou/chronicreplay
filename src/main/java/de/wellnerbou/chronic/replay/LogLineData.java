package de.wellnerbou.chronic.replay;

public class LogLineData {
	private long time;
	private String clientip;
	private String requestMethod;
	private String request;
	private long duration;
	private String statusCode;
	private String userAgent;
	private String referrer;
	private String host;
	private String scheme;

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

	public void setStatusCode(final String statusCode) {
		this.statusCode = statusCode;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public void setReferrer(final String referrer) {
		this.referrer = referrer;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}

	public String getClientip() {
		return clientip;
	}

	public void setClientip(final String clientip) {
		this.clientip = clientip;
	}

	@Override
	public String toString() {
		return "LogLineData{" +
				"time=" + time +
				", clientip='" + clientip + '\'' +
				", requestMethod='" + requestMethod + '\'' +
				", request='" + request + '\'' +
				", duration=" + duration +
				", statusCode='" + statusCode + '\'' +
				", userAgent='" + userAgent + '\'' +
				", referrer='" + referrer + '\'' +
				", host='" + host + '\'' +
				", scheme='" + scheme + '\'' +
				'}';
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(final String scheme) {
		this.scheme = scheme;
	}
}
