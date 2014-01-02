package de.tecfem.chronic.logreader;

import de.tecfem.chronic.replay.LogLineData;

public interface LogLineReader {

	public LogLineData parseLine(String logLine);

	public String getId();

}
