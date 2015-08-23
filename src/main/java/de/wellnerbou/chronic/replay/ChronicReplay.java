package de.wellnerbou.chronic.replay;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.wellnerbou.chronic.logreader.LogLineReader;
import de.wellnerbou.chronic.logreader.LogLineReaderProvider;
import org.slf4j.MDC;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.google.common.base.Optional;
import com.ning.http.client.AsyncHttpClient;

public class ChronicReplay
{
	private static final String LOGFILE = "logfile";
	private static final String URLPREFIX = "urlprefix";
	private static final String HOSTHEADER = "hostheader";
	private static final String LOGREADERID = "logreader";

	public static void main(final String[] args) throws FileNotFoundException, IOException
    {
		OptionParser optionParser = new OptionParser();
		optionParser.accepts(URLPREFIX, "Scheme, host and port which will be prefixed to the request.").withRequiredArg().required();
		optionParser.accepts(HOSTHEADER, "Virtual host header").withRequiredArg();
		optionParser.accepts(LOGFILE, "Logfile to replay").withRequiredArg().required();
		optionParser.accepts(LOGREADERID, "Id of the reader class which should be used to read the given log file.").withRequiredArg().required();

		OptionSet optionSet = optionParser.parse(args);

		ChronicReplay chronicReplay = new ChronicReplay();
		Optional<String> virtualHostHeader = Optional.<String> absent();
		if (optionSet.has(HOSTHEADER)) {
			virtualHostHeader = Optional.fromNullable((String) optionSet.valueOf(HOSTHEADER));
		}
		setLoggingDiscriminatorVariable(optionSet);
		chronicReplay.replay((String) optionSet.valueOf(URLPREFIX), (String) optionSet.valueOf(LOGFILE), virtualHostHeader, (String) optionSet.valueOf(LOGREADERID));
    }

	private static void setLoggingDiscriminatorVariable(final OptionSet optionSet) {
		final String targetserver = (String) optionSet.valueOf(URLPREFIX);
		MDC.put("targetserver", getLoggingDiscriminatorVariable(targetserver));
	}

	static String getLoggingDiscriminatorVariable(final String hostPrefix) {
		final String targetHost = hostPrefix.replaceAll("^(ht|f)tp(s?)://", "").replace(':', '-').replace('/', '-').replaceAll("[^a-zA-Z0-9_\\-\\.]*", "");
		return targetHost + "_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
	}

	private void replay(final String host, final String file, final Optional<String> virtualHostHeader, final String logReaderId) throws IOException {
		this.replay(host, getInputStreamFromGivenFile(file), virtualHostHeader, logReaderId);
	}

	private InputStream getInputStreamFromGivenFile(final String file) throws FileNotFoundException {
		return new FileInputStream(file);
	}

	public void replay(final String host, final InputStream inputStream, final Optional<String> virtualHostHeader, final String logReaderId) throws IOException {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		LogLineReaderProvider logLineReaderProvider = new LogLineReaderProvider();
		LogLineReader logLineReader = logLineReaderProvider.getLogLineReader(logReaderId);
		final ResultDataLogger resultDataLogger = new ResultDataLogger();
		resultDataLogger.logColumnTitles();
		LineReplayer lineReplayer = new LineReplayer(host, asyncHttpClient, resultDataLogger);
		if (virtualHostHeader.isPresent()) {
			lineReplayer.setHostHeader(virtualHostHeader.get());
		}
		LogReplayReader logReplayReader = new LogReplayReader(lineReplayer, logLineReader);
		logReplayReader.readAndReplay(inputStream);
		asyncHttpClient.closeAsynchronously();
	}

	private ChronicReplay() {
	}
}
