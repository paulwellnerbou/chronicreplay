package de.wellnerbou.chronic.replay;

import org.junit.Before;
import org.junit.Test;

public class LineReplayerTest {

	LineReplayer lineReplayer = new LineReplayer(null, null);

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetTimeToWait() {
		long timeReplayStarted = 1000L;
		long originalTimeLogFileStarted = 900L;
		long offset = timeReplayStarted - originalTimeLogFileStarted;

		long timeOfOriginalRequest = 1010L;
	}

}
