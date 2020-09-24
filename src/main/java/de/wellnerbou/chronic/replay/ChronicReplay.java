package de.wellnerbou.chronic.replay;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.Cli;
import com.lexicalscope.jewel.cli.CliFactory;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.providers.grizzly.GrizzlyAsyncHttpProvider;
import de.wellnerbou.chronic.logparser.LogLineParser;
import de.wellnerbou.chronic.logparser.LogLineParserProvider;
import de.wellnerbou.chronic.logsource.factory.LogSourceReaderFactoryProvider;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.MDC;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
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
        if (targetserver != null) {
            MDC.put("targetserver", getLoggingDiscriminatorVariable(targetserver));
        }
    }

    static String getLoggingDiscriminatorVariable(final String hostPrefix) {
        final String targetHost = hostPrefix.replaceAll("^(ht|f)tp(s?)://", "").replace(':', '-').replace('/', '-').replaceAll("[^a-zA-Z0-9_\\-.]*", "");
        return targetHost + "_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
    }

    private void replay(final CliOptions options) throws IOException {
        try (InputStream is = getInputStreamFromGivenFile(options.getLogfile())) {
            this.replay(is, options);
        }
    }

    private InputStream getInputStreamFromGivenFile(final String file) throws IOException {
        if (new File(file).exists()) {
            return new FileInputStream(file);
        } else {
            try {
                return new URI(file).toURL().openStream();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void replay(final InputStream inputStream, final CliOptions options) throws IOException {
        final AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder().build();
        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient(new GrizzlyAsyncHttpProvider(config));
        final LogLineParserProvider logLineParserProvider = new LogLineParserProvider(options.getGrokPattern());
        final LogLineParser logLineParser = logLineParserProvider.getImplementation(options.getLogparser());
        final ResultDataLogger resultDataLogger = createLogger(options.getLogger());
        final HostRequestBuilder hostRequestBuilder = new HostRequestBuilder(options.getHost(), options.getHostmap());
        final LineReplayer lineReplayer = new LineReplayer(hostRequestBuilder, asyncHttpClient, resultDataLogger);
        lineReplayer.setHostHeader(options.getHostheader());
        lineReplayer.setHeaders(options.getHeader());
        lineReplayer.setCustomUserAgent(options.getCustomUserAgent());
        lineReplayer.setFollowRedirects(options.getFollowRedirects());
        final LogReplayReader logReplayReader = new LogReplayReader(lineReplayer, logLineParser, new LogSourceReaderFactoryProvider().getImplementation(options.getLogreader()));
        logReplayReader.setDelay(options.getDelay());
        logReplayReader.setWaitForTermination(options.getWaitForTermination());
        logReplayReader.readAndReplay(inputStream, convertToDateTime(options.getFrom()), convertToDateTime(options.getUntil()));
        close(asyncHttpClient);
    }

    protected DateTime convertToDateTime(final String until) {
        return DateTime.parse(until, DateTimeFormat.forPattern("HH:mm:ss"));
    }

    private void close(AsyncHttpClient asyncHttpClient) {
        asyncHttpClient.closeAsynchronously();
    }

    private ResultDataLogger createLogger(final String loggerType) {

        if (loggerType.equals("json")) {
            final JsonResultDataLogger jsonResultDataLogger = new JsonResultDataLogger();
            return jsonResultDataLogger;
        } else {
            final CsvResultDataLogger csvResultDataLogger = new CsvResultDataLogger();
            csvResultDataLogger.logColumnTitles();
            return csvResultDataLogger;
        }

    }
}
