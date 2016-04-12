package de.wellnerbou.chronic.logsource;

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public class JsonUriLogSourceReader extends JsonReader implements LogSourceReader<JsonReader> {

	public JsonUriLogSourceReader(final Reader in) {
		super(in);
	}

	@Override
	public JsonReader next() {
		return this;
	}
}
