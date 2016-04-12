package de.wellnerbou.chronic.logparser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogLineReaderProvider {

	private ServiceLoader<LogLineParser> serviceLoader;
	private static final Logger LOG = LoggerFactory.getLogger(LogLineReaderProvider.class);

	public LogLineReaderProvider() {
		this.load();
	}

	private void load() {
		serviceLoader = ServiceLoader.load(LogLineParser.class);
	}

	public LogLineParser getLogLineReader(final String id) {
		List<LogLineParser> list = getAllLogLineReader(id);
		if (list.isEmpty()) {
			throw new NoImplementationFoundException(LogLineParser.class, id);
		} else if (list.size() > 1) {
			LOG.warn("More than one implementations for {} with id {} found, using first one: {}", LogLineParser.class.getName(), id, list.get(0).getClass().getName());
		}
		return list.get(0);
	}

	public List<LogLineParser> getAllLogLineReader(final String id) {
		List<LogLineParser> list = new ArrayList<>();
		Iterator<LogLineParser> it = serviceLoader.iterator();
		while (it.hasNext()) {
			LogLineParser logLineParser = it.next();
			if (logLineParser.getId().equalsIgnoreCase(id)) {
				list.add(logLineParser);
			}
		}
		return list;
	}
}
