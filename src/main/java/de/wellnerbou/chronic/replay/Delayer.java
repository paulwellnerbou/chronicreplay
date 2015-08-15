package de.wellnerbou.chronic.replay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Delayer {

	private static final Logger LOG = LoggerFactory.getLogger(Delayer.class);

	private long offset;

	public Delayer(final long timeOfFirstLogLine) {
		this(timeOfFirstLogLine, System.currentTimeMillis());
	}

	protected Delayer(final long timeOfFirstLogLine, final long timeSimulationStarts) {
		this.offset = timeSimulationStarts - timeOfFirstLogLine;
	}

	public void delay(final long originalRequestTime) throws InterruptedException {
		long delayTime = getDelayTo(originalRequestTime);
		if (delayTime < 0) {
			LOG.info("{} ms behind our replay schedule, executing immediately, originalRequestTime: {}", delayTime, originalRequestTime);
		} else if (delayTime > 0) {
			LOG.info("Waiting {} ms to fire the next request, originalRequestTime: {}", delayTime, originalRequestTime);
			Thread.sleep(delayTime);
		}
	}

	public long getDelayTo(final long originalRequestTime) {
		return getDelayTo(originalRequestTime, System.currentTimeMillis());
	}

	protected long getDelayTo(final long originalRequestTime, final long actualTimeNow) {
		return offset - (actualTimeNow - originalRequestTime);
	}
}
