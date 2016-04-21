package de.wellnerbou.chronic.replay;

import org.joda.time.DateTime;
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
		delay(delayMilliseconds, originalRequestTime);
	}

	public void delay(final long delayMilliseconds, final long originalRequestTime) throws InterruptedException {
		if (delayMilliseconds < 0) {
			LOG.info("{} ms behind replay schedule, executing immediately.", delayMilliseconds);
		} else if (delayMilliseconds > 0) {
			LOG.info("Waiting {} to fire the next request at {}", formatDuration(delayMilliseconds), new DateTime(originalRequestTime));
			Thread.sleep(delayMilliseconds);
		}
	}

	private String formatDuration(final long delayMilliseconds) {
		if(delayMilliseconds > 1000) {
			return String.valueOf(delayMilliseconds/1000) + " seconds, " + String.valueOf(delayMilliseconds%1000) + " ms";
		}
		return String.valueOf(delayMilliseconds%1000) + " ms";
	}


	public long getDelayTo(final long originalRequestTime) {
		return getDelayTo(originalRequestTime, System.currentTimeMillis());
	}

	protected long getDelayTo(final long originalRequestTime, final long actualTimeNow) {
//		LOG.info("Offset: {}", new java.util.Date(offset));
//		LOG.info("originalTime: {}", new java.util.Date(originalRequestTime));
//		LOG.info("actualTimeNow: {}", new java.util.Date(actualTimeNow));
		return offset - (actualTimeNow - originalRequestTime);
	}
}
