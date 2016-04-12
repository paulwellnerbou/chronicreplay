package de.wellnerbou.chronic.logparser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import de.wellnerbou.chronic.plugins.NoImplementationFoundException;
import de.wellnerbou.chronic.plugins.ServiceLoaderImplementationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogLineParserProvider extends ServiceLoaderImplementationProvider<LogLineParser> {

	@Override
	protected boolean matches(final String id, final LogLineParser implementation) {
		return implementation.getId().equalsIgnoreCase(id);
	}

	@Override
	protected Class<LogLineParser> getImplementationClass() {
		return LogLineParser.class;
	}
}
