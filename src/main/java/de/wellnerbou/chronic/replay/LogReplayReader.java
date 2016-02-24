package de.wellnerbou.chronic.replay;

import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import de.wellnerbou.chronic.logreader.LogLineReader;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LogReplayReader {

	private static final Logger LOG = LoggerFactory.getLogger(LogReplayReader.class);

	private LineReplayer lineReplayer;
	private LogLineReader logLineReader;
	private boolean noDelay;

	private List<ListenableFuture<Response>> spawnedFutures = new ArrayList<>();
	private boolean waitForTermination;

	public LogReplayReader(final LineReplayer lineReplayer, final LogLineReader logLineReader) {
		this.lineReplayer = lineReplayer;
		this.logLineReader = logLineReader;
	}

	public void readAndReplay(final InputStream is, final DateTime from, final DateTime until) throws IOException {
		LOG.info("Replaying from {} until {}", from.toLocalTime(), until.toLocalTime());
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
			String line;
			LogLineData lineData;
			if ((line = reader.readLine()) != null) {
				lineData = logLineReader.parseLine(line);
				if (isBeforeTimeRangeEnd(until, lineData)) {
					Delayer delayer = new Delayer(lineData.getTime());
					lineReplayer.replay(lineData);

					while ((line = reader.readLine()) != null && isBeforeTimeRangeEnd(until, lineData)) {
						try {
							lineData = logLineReader.parseLine(line);
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
