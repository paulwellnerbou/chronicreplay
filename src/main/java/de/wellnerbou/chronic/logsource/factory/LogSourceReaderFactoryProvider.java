package de.wellnerbou.chronic.logsource.factory;

import de.wellnerbou.chronic.plugins.ServiceLoaderImplementationProvider;

public class LogSourceReaderFactoryProvider extends ServiceLoaderImplementationProvider<LogSourceReaderFactory> {

	@Override
	protected boolean matches(final String id, final LogSourceReaderFactory implementation) {
		return implementation.getId().equalsIgnoreCase(id) || implementation.getClass().getCanonicalName().equals(id);
	}

	@Override
	protected Class<LogSourceReaderFactory> getImplementationClass() {
		return LogSourceReaderFactory.class;
	}
}
