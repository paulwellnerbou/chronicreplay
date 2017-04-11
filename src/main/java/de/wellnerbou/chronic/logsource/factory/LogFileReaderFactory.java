package de.wellnerbou.chronic.logsource.factory;

import de.wellnerbou.chronic.logsource.LogSourceReader;
import de.wellnerbou.chronic.logsource.LogfileReader;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public class LogFileReaderFactory implements LogSourceReaderFactory {

	@Override
	public String getId() {
		return "file";
	}

	@Override
	public LogSourceReader create(final InputStream is) {
		return new LogfileReader(new InputStreamReader(is));
	}
}
