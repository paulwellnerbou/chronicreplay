package de.wellnerbou.chronic.logreader;

import de.wellnerbou.chronic.replay.LogLineData;

public class DummyLogLineReader implements LogLineReader {

	@Override
	public LogLineData parseLine(final String logLine) {
		return null;
	}

	@Override
	public String getId() {
		return "dummy";
	}

}
