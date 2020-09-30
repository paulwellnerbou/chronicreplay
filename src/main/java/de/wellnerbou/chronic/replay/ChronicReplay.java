package de.wellnerbou.chronic.replay;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.Cli;
import com.lexicalscope.jewel.cli.CliFactory;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpProvider;
import com.ning.http.client.providers.grizzly.GrizzlyAsyncHttpProvider;
import com.ning.http.client.providers.jdk.JDKAsyncHttpProvider;
import com.ning.http.client.providers.netty.NettyAsyncHttpProvider;
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

    public void replay(final CliOptions options) throws IOException {
        final AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
        if (options.getCustomUserAgent() != null) {
            builder.setUserAgent(options.getCustomUserAgent());
        }
        builder.setAcceptAnyCertificate(options.getInsecure());
        final AsyncHttpClientConfig config = builder.build();
        try (final AsyncHttpClient asyncHttpClient = new AsyncHttpClient(createHttpProvider(config, options))) {
            final LogLineParserProvider logLineParserProvider = new LogLineParserProvider(options.getGrokPattern());
            final LogLineParser logLineParser = logLineParserProvider.getImplementation(options.getLogparser());
            final ResultDataLogger resultDataLogger = createLogger(options.getLogger());
            final HostRequestBuilder hostRequestBuilder = new HostRequestBuilder(options.getHost(), options.getHostmap());

            final LineReplayer lineReplayer = new LineReplayer(hostRequestBuilder, asyncHttpClient, resultDataLogger, options.getResolve());
            lineReplayer.setHostHeader(options.getHostheader());
            lineReplayer.setHeaders(options.getHeader());
            if (options.getCustomUserAgent() != null) {
                lineReplayer.setCustomUserAgent(options.getCustomUserAgent());
            }
            lineReplayer.setFollowRedirects(options.getFollowRedirects());
            final LogReplayReader logReplayReader = new LogReplayReader(lineReplayer, logLineParser, new LogSourceReaderFactoryProvider().getImplementation(options.getLogreader()));
            logReplayReader.setDelay(options.getDelay());
            logReplayReader.setWaitForTermination(options.getWaitForTermination());

            final Integer repetitions = options.getRepetitions();
            if (repetitions == 0) {
                System.out.println("WARNING: repetitions = 0, will repeat ever and ever until program is killed.");
            }
            for (int i = 0; repetitions == 0 || i < repetitions; i++) {
                try (InputStream inputStream = getInputStreamFromGivenFile(options.getLogfile())) {
                    logReplayReader.replay(inputStream, convertToDateTime(options.getFrom()), convertToDateTime(options.getUntil()));
                }
            }
        }
    }

    private AsyncHttpProvider createHttpProvider(final AsyncHttpClientConfig config, final CliOptions options) {
        if (options.getHttpProvider() != null && options.getHttpProvider().equals("grizzly")) {
            return new GrizzlyAsyncHttpProvider(config);
        } else if (options.getHttpProvider() != null && options.getHttpProvider().equals("jdk")) {
            return new JDKAsyncHttpProvider(config);
        }

        return new NettyAsyncHttpProvider(config);
    }

    protected DateTime convertToDateTime(final String until) {
        return DateTime.parse(until, DateTimeFormat.forPattern("HH:mm:ss"));
    }

    private ResultDataLogger createLogger(final String loggerType) {

        if (loggerType.equals("json")) {
            return new JsonResultDataLogger();
        } else {
            final CsvResultDataLogger csvResultDataLogger = new CsvResultDataLogger();
            csvResultDataLogger.logColumnTitles();
            return csvResultDataLogger;
        }

    }
}
