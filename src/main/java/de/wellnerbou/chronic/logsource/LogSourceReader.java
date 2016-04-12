package de.wellnerbou.chronic.logsource;

import java.io.Closeable;
import java.util.Iterator;

/**
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public interface LogSourceReader<T> extends Closeable {
	T next();
}
