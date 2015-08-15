package de.wellnerbou.chronic.replay;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class DelayerTest {

	@Before
	public void setUp() {
	}

	@Test
	public void testGetDelay_before() {
		long timeReplayStarted = 200L;
		long timeOriginalLogFileStarted = 100L;
		long timeOfActualReadingLogLine = 105L;

		Delayer delayer = new Delayer(timeOriginalLogFileStarted, timeReplayStarted);
		long delay = delayer.getDelayTo(timeOfActualReadingLogLine, 201L);

		assertThat(delay).isEqualTo(4L);
	}

	@Test
	public void testGetDelay_withoutDelay() {
		long timeReplayStarted = 200L;
		long timeOriginalLogFileStarted = 100L;
		long timeOfActualReadingLogLine = 105L;

		Delayer delayer = new Delayer(timeOriginalLogFileStarted, timeReplayStarted);
		long delay = delayer.getDelayTo(timeOfActualReadingLogLine, 205L);

		assertThat(delay).isEqualTo(0L);
	}

	@Test
	public void testGetDelay_behind() {
		long timeReplayStarted = 200L;
		long timeOriginalLogFileStarted = 100L;
		long timeOfActualReadingLogLine = 105L;

		Delayer delayer = new Delayer(timeOriginalLogFileStarted, timeReplayStarted);
		long delay = delayer.getDelayTo(timeOfActualReadingLogLine, 207L);

		assertThat(delay).isEqualTo(-2L);
	}
}
