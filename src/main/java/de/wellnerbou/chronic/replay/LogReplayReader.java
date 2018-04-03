package de.wellnerbou.chronic.replay;

import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import de.wellnerbou.chronic.logparser.LogLineParser;
import de.wellnerbou.chronic.logsource.LogSourceReader;
import de.wellnerbou.chronic.logsource.factory.LogSourceReaderFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LogReplayReader {

	private static final Logger LOG = LoggerFactory.getLogger(LogReplayReader.class);

	private LineReplayer lineReplayer;
	private LogLineParser logLineParser;
	private LogSourceReaderFactory logSourceReaderFactory;
	private Long delay;

	private List<ListenableFuture<Response>> spawnedFutures = new ArrayList<>();
	private boolean waitForTermination;

	public LogReplayReader(final LineReplayer lineReplayer, final LogLineParser logLineParser, final LogSourceReaderFactory logSourceReaderFactory) {
		this.lineReplayer = lineReplayer;
		this.logLineParser = logLineParser;
		this.logSourceReaderFactory = logSourceReaderFactory;
	}

	public void readAndReplay(final InputStream is, final DateTime from, final DateTime until) throws IOException {
		LOG.info("Replaying from {} until {}", from.toLocalTime(), until.toLocalTime());
		try (LogSourceReader logSourceReader = logSourceReaderFactory.create(is)) {
			Object line;
			LogLineData lineData;

			if ((line = logSourceReader.next()) != null && (lineData = logLineParser.parseLine(line)) != null) {
				if (isBeforeTimeRangeEnd(until, lineData)) {
					Delayer delayer = new Delayer(calculateStarttimeRelativeToLogs(from, lineData));
					if(isInTimeRange(from, until, lineData)) {
						lineReplayer.replay(lineData);
					}

					while ((line = logSourceReader.next()) != null && lineData != null && isBeforeTimeRangeEnd(until, lineData)) {
						try {
							lineData = logLineParser.parseLine(line);
							if(lineData != null) {
								if (isInTimeRange(from, until, lineData)) {
									if (delay == null) {
										delayer.delayTo(lineData.getTime());
									} else {
										delayer.delay(delay, lineData.getTime());
									}
									if (waitForTermination) {
										spawnedFutures.add(lineReplayer.replay(lineData));
									} else {
										lineReplayer.replay(lineData);
									}
								}
							}
						} catch (InterruptedException | RuntimeException e) {
							LOG.error("Exception replaying line {}", line, e);
						}
					}
				}
			}
		}

		for (ListenableFuture spawnedFuture : spawnedFutures) {
			try {
				spawnedFuture.get();
			} catch (InterruptedException | ExecutionException e) {
				LOG.warn("Unable to terminate thread {}", spawnedFuture);
			}
		}
	}

	long calculateStarttimeRelativeToLogs(final DateTime from, final LogLineData firstLineData) {
		final long dateOfFirstLogLine = firstLineData.getTime();
		if(from.toLocalTime().isAfter(new DateTime(dateOfFirstLogLine).toLocalTime())) {
			return new DateTime(dateOfFirstLogLine).withMillisOfDay(from.getMillisOfDay()).getMillis();
		} else {
			return firstLineData.getTime();
		}
	}

	boolean isInTimeRange(final DateTime from, final DateTime until, final LogLineData lineData) {
		final DateTime time = new DateTime(lineData.getTime());
		if (from.toLocalTime().isAfter(time.toLocalTime())) {
			LOG.info("Skipping replay, {} before {}", time.toLocalTime(), from.toLocalTime());
			return false;
		}
		return isBeforeTimeRangeEnd(until, lineData);
	}

	private boolean isBeforeTimeRangeEnd(final DateTime until, final LogLineData lineData) {
		final DateTime logLineTime = new DateTime(lineData.getTime());
		if (logLineTime.toLocalTime().isBefore(until.toLocalTime())) {
			LOG.trace("{} before {}", logLineTime.toLocalTime(), until.toLocalTime());
			return true;
		}
		LOG.debug("Canceling replay, {} not before {}", logLineTime.toLocalTime(), until.toLocalTime());
		return false;
	}

	public void setDelay(final Long delay) {
		this.delay = delay;
	}

	public void setWaitForTermination(final boolean waitForTermination) {
		this.waitForTermination = waitForTermination;
	}
}
