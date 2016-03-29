package de.wellnerbou.chronic.logreader.filter;

import de.wellnerbou.chronic.replay.LogLineData;
import org.fest.assertions.api.Assertions;
import org.junit.Test;

/**
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public class RequestFilterTest {

    @Test
    public void testMatch_withoutRegexes() {
        LogLineData logLineData = new LogLineData();
        logLineData.setRequest("/request");

        RequestFilter requestFilter = new RequestFilter();
        Assertions.assertThat(requestFilter.matches(logLineData)).isFalse();
    }

    @Test
    public void testMatch_wrongRegex() {
        LogLineData logLineData = new LogLineData();
        logLineData.setRequest("/request");

        RequestFilter requestFilter = new RequestFilter("regex1", "regex2");
        Assertions.assertThat(requestFilter.matches(logLineData)).isFalse();
    }

    @Test
    public void testMatch_correctRegex() {
        LogLineData logLineData = new LogLineData();
        logLineData.setRequest("/request");

        RequestFilter requestFilter = new RequestFilter("^/req.*", "regex2");
        Assertions.assertThat(requestFilter.matches(logLineData)).isTrue();
    }

    @Test
    public void testMatch_correctRegexSecondInList() {
        LogLineData logLineData = new LogLineData();
        logLineData.setRequest("/request");

        RequestFilter requestFilter = new RequestFilter("doesnotmatch", "^/req.*");
        Assertions.assertThat(requestFilter.matches(logLineData)).isTrue();
    }
}
