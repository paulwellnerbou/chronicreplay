package de.wellnerbou.chronic.logparser;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import de.wellnerbou.chronic.replay.LogLineData;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * Implementation of LogLineParser supporting elasticsearch json responses of logstash systems.
 * See src/test/resources/elasticsearch-example.json.
 *
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public class ElasticSearchJsonLogLineParser implements LogLineParser {

	// 2016-04-12T04:43:34+0200
	static final DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");

	@Override
	public LogLineData parseLine(final Object logLine) {
		final JsonReader jsonReader = castOrThrowException(logLine);

		try {
			if(!jsonReader.getPath().contains("hits")) {
				searchHitsJsonNodeUpToBeginArray(jsonReader);
			}
			if(jsonReader.peek().equals(JsonToken.BEGIN_ARRAY)) {
				jsonReader.beginArray();
			}

			if(jsonReader.peek().equals(JsonToken.BEGIN_OBJECT)) {
				jsonReader.beginObject();
				searchNext(jsonReader, "_source");
				jsonReader.beginObject();
				final LogLineData logLineData = new LogLineData();
				handleAllValues(jsonReader, logLineData);
				jsonReader.endObject();
				jsonReader.endObject();
				return logLineData;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	private void handleAllValues(JsonReader jsonReader, final LogLineData logLineData) throws IOException {
		while (jsonReader.hasNext()) {
			String name = jsonReader.nextName();
			mapName(name, jsonReader, logLineData);
		}
	}

	private void mapName(final String name, final JsonReader jsonReader, final LogLineData logLineData) throws IOException {
		switch (name) {
			case "@timestamp":
				logLineData.setTime(format.parseMillis(jsonReader.nextString()));
				break;
			case "status":
				logLineData.setStatusCode(jsonReader.nextString());
				break;
			case "request":
				final String methodAndrequest = jsonReader.nextString();
				logLineData.setRequest(methodAndrequest.substring(methodAndrequest.indexOf(" ") + 1));
				break;
			case "request_method":
				logLineData.setRequestMethod(jsonReader.nextString());
				break;
			case "http_referrer":
				logLineData.setReferrer(jsonReader.nextString());
				break;
			case "http_user_agent":
				logLineData.setUserAgent(jsonReader.nextString());
				break;
			case "request_time":
				logLineData.setDuration(jsonReader.nextLong());
				break;
			case "@http":
				jsonReader.beginObject();
				handleAllValues(jsonReader, logLineData);
				jsonReader.endObject();
				break;
			default:
				jsonReader.skipValue();
				break;
		}
	}

	private void searchHitsJsonNodeUpToBeginArray(final JsonReader jsonReader) throws IOException {
		if (jsonReader.peek().equals(JsonToken.BEGIN_OBJECT)) {
			jsonReader.beginObject();
			searchNext(jsonReader, "hits");
			jsonReader.beginObject();
			searchNext(jsonReader, "hits");
		}
	}

	private void searchNext(final JsonReader jsonReader, final String jsonName) throws IOException {
		while (jsonReader.hasNext()) {
			String name = jsonReader.nextName();
			if (!name.equals(jsonName)) {
				jsonReader.skipValue();
			} else {
				return;
			}
		}
	}

	private JsonReader castOrThrowException(final Object logLine) throws IllegalArgumentException {
		if (logLine instanceof JsonReader) {
			return (JsonReader) logLine;
		} else {
			throw new IllegalArgumentException("Argument is of type " + logLine.getClass().getName() + ", expected is " + JsonReader.class.getName());
		}
	}

	@Override
	public String getId() {
		return "elasticsearch";
	}

}
