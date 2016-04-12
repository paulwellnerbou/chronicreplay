package de.wellnerbou.chronic.logparser;

import de.wellnerbou.chronic.replay.LogLineData;

public class DummyLogLineParser implements LogLineParser {

	@Override
	public LogLineData parseLine(final String logLine) {
		return null;
	}

	@Override
	public String getId() {
		return "dummy";
	}

}
