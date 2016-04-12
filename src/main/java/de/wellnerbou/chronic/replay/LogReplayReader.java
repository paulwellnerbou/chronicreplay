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
	private boolean noDelay;

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

			if ((line = logSourceReader.next()) != null) {
				lineData = logLineParser.parseLine(line);
				if (isBeforeTimeRangeEnd(until, lineData)) {
					Delayer delayer = new Delayer(lineData.getTime());
					lineReplayer.replay(lineData);

					while ((line = logSourceReader.next()) != null && isBeforeTimeRangeEnd(until, lineData)) {
						try {
							lineData = logLineParser.parseLine(line);
							if (isInTimeRange(from, lineData)) {
								if (!noDelay) {
									delayer.delay(lineData.getTime());
								}
								if(waitForTermination) {
									spawnedFutures.add(lineReplayer.replay(lineData));
								} else {
									lineReplayer.replay(lineData);
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

	private boolean isInTimeRange(final DateTime from, final LogLineData lineData) {
		final DateTime time = new DateTime(lineData.getTime());
		if (from.toLocalTime().isAfter(time.toLocalTime())) {
			LOG.info("Skipping replay, {} before {}", time.toLocalTime(), from.toLocalTime());
			return false;
		}
		return true;
	}

	private boolean isBeforeTimeRangeEnd(final DateTime until, final LogLineData lineData) {
		final DateTime time = new DateTime(lineData.getTime());
		if (time.toLocalTime().isBefore(until.toLocalTime())) {
			return true;
		}
		LOG.info("Canceling replay, {} not before {}", time.toLocalTime(), until.toLocalTime());
		return false;
	}

	public void setNoDelay(final boolean noDelay) {
		this.noDelay = noDelay;
	}

	public void setWaitForTermination(final boolean waitForTermination) {
		this.waitForTermination = waitForTermination;
	}
}
