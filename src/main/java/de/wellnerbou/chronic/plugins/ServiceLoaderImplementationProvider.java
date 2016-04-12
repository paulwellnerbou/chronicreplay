package de.wellnerbou.chronic.plugins;

import de.wellnerbou.chronic.logparser.LogLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public abstract class ServiceLoaderImplementationProvider<T> {

	private ServiceLoader<T> serviceLoader;
	private static final Logger LOG = LoggerFactory.getLogger(ServiceLoaderImplementationProvider.class);

	public ServiceLoaderImplementationProvider() {
		this.load();
	}

	private void load() {
		serviceLoader = ServiceLoader.load(getImplementationClass());
	}

	public T getImplementation(final String id) {
		List<T> list = getAllImplementations(id);
		if (list.isEmpty()) {
			throw new NoImplementationFoundException(getImplementationClass(), id);
		} else if (list.size() > 1) {
			LOG.warn("More than one implementations for {} with id {} found, using first one: {}", LogLineParser.class.getName(), id, list.get(0).getClass().getName());
		}
		return list.get(0);
	}

	public List<T> getAllImplementations(final String id) {
		List<T> list = new ArrayList<>();
		for (T implementation : serviceLoader) {
			if (matches(id, implementation)) {
				list.add(implementation);
			}
		}
		return list;
	}

	protected abstract boolean matches(final String id, final T implementation);
	protected abstract Class<T> getImplementationClass();
}
