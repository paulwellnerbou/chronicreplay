package de.wellnerbou.chronic.replay;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public class TimeRangeTest {

	@Test
	public void testIsInTimeRange() {
		TimeRange timeRange = new TimeRange("00:00:00", "00:00:00");
	}

}
