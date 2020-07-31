package de.wellnerbou.chronic.logparser;

import de.wellnerbou.chronic.plugins.ServiceLoaderImplementationProvider;

public class LogLineParserProvider extends ServiceLoaderImplementationProvider<LogLineParser> {

	private String parserPattern;

	public LogLineParserProvider(String grokPattern) {
		this.parserPattern = grokPattern;
	}

	@Override
	public LogLineParser getImplementation(String id) {
		final LogLineParser implementation = super.getImplementation(id);
		if(implementation instanceof GrokLogFormatLogLineParser) {
			((GrokLogFormatLogLineParser) implementation).init(parserPattern, new GrokResultMapper());
		}
		return implementation;
	}

	@Override
	protected boolean matches(final String id, final LogLineParser implementation) {
		return implementation.getId().equalsIgnoreCase(id);
	}

	@Override
	protected Class<LogLineParser> getImplementationClass() {
		return LogLineParser.class;
	}
}
