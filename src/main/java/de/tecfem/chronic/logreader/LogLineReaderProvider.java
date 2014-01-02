package de.tecfem.chronic.logreader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogLineReaderProvider {

	private ServiceLoader<LogLineReader> serviceLoader;
	private static final Logger LOG = LoggerFactory.getLogger(LogLineReaderProvider.class);

	public LogLineReaderProvider() {
		this.load();
	}

	private void load() {
		serviceLoader = ServiceLoader.load(LogLineReader.class);
	}

	public LogLineReader getLogLineReader(final String id) {
		List<LogLineReader> list = getAllLogLineReader(id);
		if (list.isEmpty()) {
			throw new NoImplementationFoundException(LogLineReader.class, id);
		} else if (list.size() > 1) {
			LOG.warn("More than one implementations for {} with id {} found, using first one: {}", LogLineReader.class.getName(), id, list.get(0).getClass().getName());
		}
		return list.get(0);
	}

	public List<LogLineReader> getAllLogLineReader(final String id) {
		List<LogLineReader> list = new ArrayList<>();
		Iterator<LogLineReader> it = serviceLoader.iterator();
		while (it.hasNext()) {
			LogLineReader logLineReader = it.next();
			if (logLineReader.getId().equalsIgnoreCase(id)) {
				list.add(logLineReader);
			}
		}
		return list;
	}
}
