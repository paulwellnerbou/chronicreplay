package de.tecfem.chronic.logreader;

import de.tecfem.chronic.replay.LogLineData;

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
