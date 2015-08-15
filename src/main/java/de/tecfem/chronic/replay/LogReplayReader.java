package de.tecfem.chronic.replay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tecfem.chronic.logreader.LogLineReader;

public class LogReplayReader {

	private static final Logger LOG = LoggerFactory.getLogger(LogReplayReader.class);

	private LineReplayer lineReplayer;
	private LogLineReader logLineReader;

	public LogReplayReader(final LineReplayer lineReplayer, final LogLineReader logLineReader) {
		this.lineReplayer = lineReplayer;
		this.logLineReader = logLineReader;
	}

	public void readAndReplay(final InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		String line = null;
		LogLineData lineData = null;
		if ((line = reader.readLine()) != null) {

			lineData = logLineReader.parseLine(line);
			Delayer delayer = new Delayer(lineData.getTime());
			lineReplayer.replay(lineData);

			while ((line = reader.readLine()) != null) {
				try {
					lineData = logLineReader.parseLine(line);
					delayer.delay(lineData.getTime());
					lineReplayer.replay(lineData);
				} catch (InterruptedException | RuntimeException e) {
					LOG.error("Exception replaying line {}", line, e);
				}
			}
		}

        reader.close();
	}
}
