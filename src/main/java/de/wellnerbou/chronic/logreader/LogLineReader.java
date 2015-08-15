package de.wellnerbou.chronic.logreader;

import de.wellnerbou.chronic.replay.LogLineData;

public interface LogLineReader {

	public LogLineData parseLine(String logLine);

	public String getId();

}
