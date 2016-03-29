package de.wellnerbou.chronic.logreader.filter;

import de.wellnerbou.chronic.replay.LogLineData;

/**
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public class RequestFilter implements LogLineReadingFilter {

    private final String[] filterRegexes;

    public RequestFilter(final String... filterRegexes) {
        this.filterRegexes = filterRegexes;
    }

    @Override
    public boolean matches(LogLineData logLineData) {
        for (String filterRegex : filterRegexes) {
            if(logLineData.getRequest().matches(filterRegex)) {
                return true;
            }
        }
        return false;
    }
}
