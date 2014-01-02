package de.tecfem.chronic.replay;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.MDC;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.google.common.base.Optional;
import com.ning.http.client.AsyncHttpClient;

import de.tecfem.chronic.logreader.LogLineReader;
import de.tecfem.chronic.logreader.LogLineReaderProvider;

public class ChronicReplay
{
	private static final String LOGFILE = "logfile";
	private static final String URLPREFIX = "urlprefix";
	private static final String HOSTHEADER = "hostheader";
	private static final String LOGREADERID = "logreaderid";

	public static void main(final String[] args) throws FileNotFoundException, IOException
    {
		OptionParser optionParser = new OptionParser();
		optionParser.accepts(URLPREFIX, "Scheme, host and port which will be prefixed to the request.").withRequiredArg().required();
		optionParser.accepts(HOSTHEADER, "Virtual host header").withRequiredArg();
		optionParser.accepts(LOGFILE, "Logfile to replay").withRequiredArg().required();
		optionParser.accepts(LOGREADERID, "Id of the reader class which should be used to read the given log file.").withRequiredArg();

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
		String targetserver = (String) optionSet.valueOf(URLPREFIX);
		MDC.put("targetserver", getLoggingDiscriminatorVariable(targetserver));
	}

	static String getLoggingDiscriminatorVariable(final String hostPrefix) {
		return hostPrefix.replaceAll("^(ht|f)tp(s?)://", "").replace('/', '-').replaceAll("[^a-zA-Z0-9_\\-\\.]*", "");
	}

	private void replay(final String host, final String file, final Optional<String> virtualHostHeader, final String logReaderId) throws FileNotFoundException, IOException {
		this.replay(host, getInputStreamFromGivenFile(file), virtualHostHeader, logReaderId);
	}

	private InputStream getInputStreamFromGivenFile(final String file) throws FileNotFoundException {
		return new FileInputStream(file);
	}

	public void replay(final String host, final InputStream inputStream, final Optional<String> virtualHostHeader, final String logReaderId) throws IOException {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		LogLineReaderProvider logLineReaderProvider = new LogLineReaderProvider();
		LogLineReader logLineReader = logLineReaderProvider.getLogLineReader(logReaderId);
		LineReplayer lineReplayer = new LineReplayer(host, asyncHttpClient);
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
