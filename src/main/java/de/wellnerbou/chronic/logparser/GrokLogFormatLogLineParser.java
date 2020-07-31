package de.wellnerbou.chronic.logparser;

import de.wellnerbou.chronic.replay.LogLineData;
import io.thekraken.grok.api.Grok;
import io.thekraken.grok.api.GrokCompiler;
import io.thekraken.grok.api.Match;
import io.thekraken.grok.api.exception.GrokException;

import java.util.Map;

public class GrokLogFormatLogLineParser implements LogLineParser {

	private String pattern = "%{COMBINEDAPACHELOG}";
	private GrokResultMapper grokResultMapper;

	@Override
	public LogLineData parseLine(final Object logLine) {
		try {
			return parseLineWithGrok(logLine.toString());
		} catch (GrokException e) {
			throw new RuntimeException(e);
		}
	}

	public void init(final String pattern, final GrokResultMapper grokResultMapper) {
		this.pattern = pattern;
		this.grokResultMapper = grokResultMapper;
	}

	private LogLineData parseLineWithGrok(final String logLine) throws GrokException {
		GrokCompiler grokCompiler = GrokCompiler.newInstance();
		grokCompiler.registerDefaultPatterns();
		final Grok grok = grokCompiler.compile(pattern);
		Match match = grok.match(logLine);
		final Map<String, Object> capture = match.capture();
		return grokResultMapper.map(capture);
	}

	@Override
	public String getId() {
		return "grok";
	}
}
