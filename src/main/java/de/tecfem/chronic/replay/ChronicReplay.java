package de.tecfem.chronic.replay;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.google.common.base.Optional;
import com.ning.http.client.AsyncHttpClient;

public class ChronicReplay
{
	private static final String LOGFILE = "logfile";
	private static final String URLPREFIX = "urlprefix";
	private static final String HOSTHEADER = "hostheader";

	public static void main(final String[] args) throws FileNotFoundException, IOException
    {
		OptionParser optionParser = new OptionParser();
		optionParser.accepts(URLPREFIX, "Scheme, host and port which will be prefixed to the request.").withRequiredArg().required();
		optionParser.accepts(HOSTHEADER, "Virtual host header").withRequiredArg();
		optionParser.accepts(LOGFILE, "Logfile").withRequiredArg().required();

		OptionSet optionSet = optionParser.parse(args);

		ChronicReplay chronicReplay = new ChronicReplay();
		Optional<String> virtualHostHeader = Optional.<String> absent();
		if (optionSet.has(HOSTHEADER)) {
			virtualHostHeader = Optional.fromNullable((String) optionSet.valueOf(HOSTHEADER));
		}
		chronicReplay.replay((String) optionSet.valueOf(URLPREFIX), (String) optionSet.valueOf(LOGFILE), virtualHostHeader);
    }

	private void replay(final String host, final String file, final Optional<String> virtualHostHeader) throws FileNotFoundException, IOException {
		this.replay(host, getInputStreamFromGivenFile(file), virtualHostHeader);
	}

	private InputStream getInputStreamFromGivenFile(final String file) throws FileNotFoundException {
		return new FileInputStream(file);
	}

	public void replay(final String host, final InputStream inputStream, final Optional<String> virtualHostHeader) throws IOException {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		LogLineReader logLineReader = new LogLineReader();
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
