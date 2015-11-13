package de.wellnerbou.chronic.replay;

import com.google.common.base.Optional;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.providers.grizzly.GrizzlyAsyncHttpProvider;
import de.wellnerbou.chronic.logreader.LogLineReader;
import de.wellnerbou.chronic.logreader.LogLineReaderProvider;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChronicReplay {
	private static final Logger LOG = LoggerFactory.getLogger(LineReplayer.class);

	private static final String LOGFILE = "logfile";
	private static final String URLPREFIX = "urlprefix";
	private static final String HOSTHEADER = "hostheader";
	private static final String HEADER = "header";
	private static final String LOGREADERID = "logreader";
	private static final String FOLLOWREDIRECTS = "followredirects";

	public static void main(final String[] args) throws IOException {
		OptionParser optionParser = new OptionParser();
		optionParser.accepts(URLPREFIX, "Scheme, host and port which will be prefixed to the request.").withRequiredArg().required();
		optionParser.accepts(HOSTHEADER, "Virtual host header").withRequiredArg();
		optionParser.accepts(HEADER, "HTTP Header").withRequiredArg();
		optionParser.accepts(LOGFILE, "Logfile to replay").withRequiredArg().required();
		optionParser.accepts(LOGREADERID, "Id of the reader class which should be used to read the given log file.").withRequiredArg().required();

		OptionSet optionSet = optionParser.parse(args);
		ChronicReplay chronicReplay = new ChronicReplay();

		boolean followRedirects = false;
		if (optionSet.has(FOLLOWREDIRECTS)) {
			followRedirects = true;
		}
		Optional<String> virtualHostHeader = Optional.<String>absent();
		if (optionSet.has(HOSTHEADER)) {
			virtualHostHeader = Optional.fromNullable((String) optionSet.valueOf(HOSTHEADER));
		}
		Optional<String> header = Optional.absent();
		if (optionSet.has(HEADER)) {
			header = Optional.fromNullable((String) optionSet.valueOf(HEADER));
		}

		setLoggingDiscriminatorVariable(optionSet);
		chronicReplay.replay((String) optionSet.valueOf(URLPREFIX), (String) optionSet.valueOf(LOGFILE), virtualHostHeader, header, followRedirects, (String) optionSet.valueOf(LOGREADERID));
	}

	private static void setLoggingDiscriminatorVariable(final OptionSet optionSet) {
		final String targetserver = (String) optionSet.valueOf(URLPREFIX);
		MDC.put("targetserver", getLoggingDiscriminatorVariable(targetserver));
	}

	static String getLoggingDiscriminatorVariable(final String hostPrefix) {
		final String targetHost = hostPrefix.replaceAll("^(ht|f)tp(s?)://", "").replace(':', '-').replace('/', '-').replaceAll("[^a-zA-Z0-9_\\-\\.]*", "");
		return targetHost + "_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
	}

	private void replay(final String host, final String file, final Optional<String> virtualHostHeader, final Optional<String> header, boolean followRedirects, final String logReaderId) throws IOException, FileNotFoundException {
		try (InputStream is = getInputStreamFromGivenFile(file)) {
			this.replay(host, is, virtualHostHeader, header, followRedirects, logReaderId);
		}
	}

	private InputStream getInputStreamFromGivenFile(final String file) throws FileNotFoundException {
		return new FileInputStream(file);
	}

	public void replay(final String host, final InputStream inputStream, final Optional<String> virtualHostHeader, final Optional<String> header, boolean followRedirects, final String logReaderId) throws IOException {
		AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder().build();
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient(new GrizzlyAsyncHttpProvider(config));
		LogLineReaderProvider logLineReaderProvider = new LogLineReaderProvider();
		LogLineReader logLineReader = logLineReaderProvider.getLogLineReader(logReaderId);
		final ResultDataLogger resultDataLogger = new ResultDataLogger();
		resultDataLogger.logColumnTitles();
		LineReplayer lineReplayer = new LineReplayer(host, asyncHttpClient, resultDataLogger);
		lineReplayer.setHostHeader(virtualHostHeader.orNull());
		lineReplayer.setHeader(header.orNull());
		lineReplayer.setFollowRedirects(followRedirects);
		LogReplayReader logReplayReader = new LogReplayReader(lineReplayer, logLineReader);
		logReplayReader.readAndReplay(inputStream);
		close(asyncHttpClient);
	}

	private void close(AsyncHttpClient asyncHttpClient) {
		try {
			// sleep a while to allow all threads to finish
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			LOG.warn("Interrupted while sleeping, log lines of http client threads still running may not be printed.", e);
		}
		asyncHttpClient.closeAsynchronously();
	}

	private ChronicReplay() {
	}
}
