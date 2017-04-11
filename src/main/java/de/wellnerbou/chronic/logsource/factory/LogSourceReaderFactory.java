package de.wellnerbou.chronic.logsource.factory;

import de.wellnerbou.chronic.logsource.LogSourceReader;

import java.io.InputStream;

/**
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public interface LogSourceReaderFactory {
	String getId();
	LogSourceReader create(InputStream is);
}
