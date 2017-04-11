package de.wellnerbou.chronic.logparser;

import de.wellnerbou.chronic.replay.LogLineData;

public interface LogLineParser {

	LogLineData parseLine(Object logLine);
	String getId();
}
