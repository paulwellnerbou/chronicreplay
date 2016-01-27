package de.wellnerbou.chronic.replay;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.Cli;
import com.lexicalscope.jewel.cli.CliFactory;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.providers.grizzly.GrizzlyAsyncHttpProvider;
import de.wellnerbou.chronic.logreader.LogLineReader;
import de.wellnerbou.chronic.logreader.LogLineReaderProvider;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.MDC;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChronicReplay {

	public static void main(final String[] args) throws IOException {
		final Cli<CliOptions> cli = CliFactory.createCli(CliOptions.class);
		try {
			final CliOptions options = cli.parseArguments(args);
			setLoggingDiscriminatorVariable(options);
			final ChronicReplay chronicReplay = new ChronicReplay();
			chronicReplay.replay(options);
		} catch (ArgumentValidationException e) {
			System.out.println(e.getMessage());
			System.out.println(cli.getHelpMessage());
		}
	}

	private static void setLoggingDiscriminatorVariable(final CliOptions options) {
		final String targetserver = options.getHost();
		MDC.put("targetserver", getLoggingDiscriminatorVariable(targetserver));
	}

	static String getLoggingDiscriminatorVariable(final String hostPrefix) {
		final String targetHost = hostPrefix.replaceAll("^(ht|f)tp(s?)://", "").replace(':', '-').replace('/', '-').replaceAll("[^a-zA-Z0-9_\\-\\.]*", "");
		return targetHost + "_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
	}

	private void replay(final CliOptions options) throws IOException {
		try (InputStream is = getInputStreamFromGivenFile(options.getLogfile())) {
			this.replay(is, options);
		}
	}

	private InputStream getInputStreamFromGivenFile(final String file) throws FileNotFoundException {
		return new FileInputStream(file);
	}

	public void replay(final InputStream inputStream, final CliOptions options) throws IOException {
		AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder().build();
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient(new GrizzlyAsyncHttpProvider(config));
		LogLineReaderProvider logLineReaderProvider = new LogLineReaderProvider();
		LogLineReader logLineReader = logLineReaderProvider.getLogLineReader(options.getLogreader());
		final ResultDataLogger resultDataLogger = new ResultDataLogger();
		resultDataLogger.logColumnTitles();
		LineReplayer lineReplayer = new LineReplayer(options.getHost(), asyncHttpClient, resultDataLogger);
		lineReplayer.setHostHeader(options.getHostheader());
		lineReplayer.setHeaders(options.getHeader());
		lineReplayer.setFollowRedirects(options.getFollowRedirects());
		LogReplayReader logReplayReader = new LogReplayReader(lineReplayer, logLineReader);
		logReplayReader.setNoDelay(options.getNoDelay());
		logReplayReader.readAndReplay(inputStream, convertToDateTime(options.getFrom()), convertToDateTime(options.getUntil()));
		close(asyncHttpClient);
	}

	protected DateTime convertToDateTime(final String until) {
		return DateTime.parse(until, DateTimeFormat.forPattern("HH:mm:ss"));
	}

	private void close(AsyncHttpClient asyncHttpClient) {
		asyncHttpClient.closeAsynchronously();
	}
}
