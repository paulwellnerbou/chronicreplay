package de.wellnerbou.chronic.logsource.factory;

import de.wellnerbou.chronic.logsource.JsonUriLogSourceReader;
import de.wellnerbou.chronic.logsource.LogSourceReader;
import de.wellnerbou.chronic.logsource.LogfileReader;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public class JsonUrlReaderFactory implements LogSourceReaderFactory {

	@Override
	public String getId() {
		return "jsonreader";
	}

	@Override
	public LogSourceReader create(final InputStream is) {
		return new JsonUriLogSourceReader(new InputStreamReader(is));
	}
}
