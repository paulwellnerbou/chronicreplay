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

	public void delayTo(final long originalRequestTime) throws InterruptedException {
		long delayMilliseconds = getDelayTo(originalRequestTime);
		delay(delayMilliseconds);
	}

	public void delay(final long delayMilliseconds) throws InterruptedException {
		if (delayMilliseconds < 0) {
			LOG.info("{} ms behind replay schedule, executing immediately.", delayMilliseconds);
		} else if (delayMilliseconds > 0) {
			LOG.info("Waiting {} ms to fire the next request.", delayMilliseconds);
			Thread.sleep(delayMilliseconds);
		}
	}

	public long getDelayTo(final long originalRequestTime) {
		return getDelayTo(originalRequestTime, System.currentTimeMillis());
	}

	protected long getDelayTo(final long originalRequestTime, final long actualTimeNow) {
		return offset - (actualTimeNow - originalRequestTime);
	}
}
