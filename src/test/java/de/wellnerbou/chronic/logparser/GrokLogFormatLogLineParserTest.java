package de.wellnerbou.chronic.logparser;

import de.wellnerbou.chronic.replay.LogLineData;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class GrokLogFormatLogLineParserTest {

    private final String logline;
    private final String pattern;
    private final String expectedIp;
    private final String request;
    private final Long timestamp;
    private final String responseStatus;
    private final Long duration;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {
                    "127.0.0.1 - - [02/Jan/2014:11:55:12 +0100] \"GET / HTTP/1.1\" 200 481 \"-\" \"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:26.0) Gecko/20100101 Firefox/26.0\"",
                    "%{COMBINEDAPACHELOG}",
                    "127.0.0.1", "/", 1388663712000L,
                    "200", 0L
            }, {
                    "1.1.1.1, 2.2.2.2 - - [16/Sep/2017:02:36:57 +0200] \"GET /path HTTP/1.1\" 200 53055 429877 \"-\" \"-\"",
                    "%{IPORHOST:clientip}, %{IPORHOST:clientip2} %{USER:ident} %{USER:auth} \\[%{HTTPDATE:timestamp}\\] \"(?:%{WORD:verb} %{NOTSPACE:request}(?: HTTP/%{NUMBER:httpversion})?|%{DATA:rawrequest})\" %{NUMBER:response} (?:%{NUMBER:bytes}|-) (?:%{NUMBER:duration}|-)",
                    "1.1.1.1", "/path", 1505529417000L,
                    "200", 430L
            }, {
                    "1.1.1.1 2.2.2.2 - - [13/Jul/2018:00:00:00 +0200] \"GET /path HTTP/1.1\" 304 - okhttp/3.10.0",
                    "%{IPORHOST:clientip} %{IPORHOST:clientip2} %{USER:ident} %{USER:auth} \\[%{HTTPDATE:timestamp}\\] \"(?:%{WORD:verb} %{NOTSPACE:request}(?: HTTP/%{NUMBER:httpversion})?|%{DATA:rawrequest})\" %{NUMBER:response} (?:%{NUMBER:bytes}|-) (?:%{DATA:useragent}|-)",
                    "1.1.1.1", "/path", 1531440000000L,
                    "304", 0L
            }, {
                    "1.1.1.1 2.2.2.2 - - [13/Jul/2018:00:00:00 +0200] \"GET /?param1=1&param2=2 HTTP/1.1\" 200 87131 okhttp/3.10.0",
                    "%{IPORHOST:clientip} %{IPORHOST:clientip2} %{USER:ident} %{USER:auth} \\[%{HTTPDATE:timestamp}\\] \"(?:%{WORD:verb} %{NOTSPACE:request}(?: HTTP/%{NUMBER:httpversion})?|%{DATA:rawrequest})\" %{NUMBER:response} (?:%{NUMBER:bytes}|-) (?:%{DATA:useragent}|-)",
                    "1.1.1.1", "/?param1=1&param2=2", 1531440000000L,
                    "200", 0L
            }, {
                    "[13/Jul/2018:00:00:00 +0200] https://dlfkultur.deutschlandradio.de/kinderhoerspiel.960.de.nltext",
                    "\\[%{HTTPDATE:timestamp}\\] %{NOTSPACE:request}",
                    "null", "https://dlfkultur.deutschlandradio.de/kinderhoerspiel.960.de.nltext", 1531440000000L,
                    "null", 0L
            }
        });
    }

    public GrokLogFormatLogLineParserTest(String logline, String pattern, String expectedIp, String request, Long timestamp, String responseStatus, Long duration) {
        this.logline = logline;
        this.pattern = pattern;
        this.expectedIp = expectedIp;
        this.request = request;
        this.timestamp = timestamp;
        this.responseStatus = responseStatus;
        this.duration = duration;
    }

    @Test
    public void testParseLine() {
        GrokLogFormatLogLineParser logLineParser = new GrokLogFormatLogLineParser();
        logLineParser.init(pattern, new GrokResultMapper());
        LogLineData logLineData = logLineParser.parseLine(logline);
        Assertions.assertThat(logLineData.getClientip()).isEqualTo(expectedIp);
        Assertions.assertThat(logLineData.getStatusCode()).isEqualTo(responseStatus);
        Assertions.assertThat(logLineData.getDuration()).isEqualTo(duration);
        Assertions.assertThat(logLineData.getRequest()).isEqualTo(request);
        Assertions.assertThat(logLineData.getTime()).isEqualTo(timestamp);
    }
}
