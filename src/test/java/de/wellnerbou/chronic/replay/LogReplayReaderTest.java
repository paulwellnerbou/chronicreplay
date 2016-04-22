package de.wellnerbou.chronic.replay;

import org.fest.assertions.api.Assertions;
import org.joda.time.DateTime;
import org.junit.Test;

public class LogReplayReaderTest {

	private final LogReplayReader logReplayReader = new LogReplayReader(null, null, null);

	@Test
	public void calculateStarttimeRelativeToLogs_fromOptionBeforeLogfileStart_shouldReturnLogfileStart() {
		DateTime fromOptionGiven = new DateTime(1970, 1, 1, 10, 50, 12);
		DateTime firstLogfileTime = new DateTime(2014, 1, 2, 11, 50, 12);
		LogLineData logLineData = new LogLineData();
		logLineData.setTime(firstLogfileTime.getMillis());

		final long result = logReplayReader.calculateStarttimeRelativeToLogs(fromOptionGiven, logLineData);
		Assertions.assertThat(result).isEqualTo(firstLogfileTime.getMillis());
	}

	@Test
	public void calculateStarttimeRelativeToLogs_fromOptionAfterLogfileStart_shouldReturnFromOption() {
		DateTime fromOptionGiven = new DateTime(1970, 1, 1, 11, 50, 12);
		DateTime firstLogfileTime = new DateTime(2014, 1, 2, 10, 50, 12);
		LogLineData logLineData = new LogLineData();
		logLineData.setTime(firstLogfileTime.getMillis());

		final long result = logReplayReader.calculateStarttimeRelativeToLogs(fromOptionGiven, logLineData);
		Assertions.assertThat(new DateTime(result).getMillisOfDay()).isEqualTo(fromOptionGiven.getMillisOfDay());
	}

	@Test
	public void testIsInTimeRange() {
		final DateTime from = new DateTime(39300000);
		final long lineDataTime = 1388660111000L;
		LogLineData logLineData = new LogLineData();
		logLineData.setTime(lineDataTime);

		Assertions.assertThat(logReplayReader.isInTimeRange(from, from.plusSeconds(1), logLineData)).isFalse();
	}
}
