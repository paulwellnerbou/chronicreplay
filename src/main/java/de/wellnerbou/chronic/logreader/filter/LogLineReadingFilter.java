package de.wellnerbou.chronic.logreader.filter;

import de.wellnerbou.chronic.replay.LogLineData;

/**
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public interface LogLineReadingFilter {
    public boolean matches(LogLineData logLineData);
}
