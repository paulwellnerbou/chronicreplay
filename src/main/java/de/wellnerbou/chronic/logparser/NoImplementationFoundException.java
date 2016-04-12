package de.wellnerbou.chronic.logparser;

public class NoImplementationFoundException extends RuntimeException {
	private static final long serialVersionUID = -4811467211038403821L;

	public NoImplementationFoundException(final Class<LogLineParser> clazz, final String id) {
		super("No implementation of " + clazz.getName() + " found with id " + id);
	}
}
