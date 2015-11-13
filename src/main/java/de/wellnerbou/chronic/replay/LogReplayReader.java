package de.wellnerbou.chronic.replay;

import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import de.wellnerbou.chronic.logreader.LogLineReader;
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

	public LogReplayReader(final LineReplayer lineReplayer, final LogLineReader logLineReader) {
		this.lineReplayer = lineReplayer;
		this.logLineReader = logLineReader;
	}

	public void readAndReplay(final InputStream is) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
			String line;
			LogLineData lineData;
			if ((line = reader.readLine()) != null) {
				lineData = logLineReader.parseLine(line);
				Delayer delayer = new Delayer(lineData.getTime());
				lineReplayer.replay(lineData);

				while ((line = reader.readLine()) != null) {
					try {
						lineData = logLineReader.parseLine(line);
						if (!noDelay) {
							delayer.delay(lineData.getTime());
						}
						spawnedFutures.add(lineReplayer.replay(lineData));
					} catch (InterruptedException | RuntimeException e) {
						LOG.error("Exception replaying line {}", line, e);
					}
				}
			}
		}

		for(ListenableFuture spawnedFuture : spawnedFutures) {
			try {
				spawnedFuture.get();
			} catch (InterruptedException | ExecutionException e) {
				LOG.warn("Unable to terminate thread {}", spawnedFuture);
			}
		}
	}

	public void setNoDelay(final boolean noDelay) {
		this.noDelay = noDelay;
	}
}
