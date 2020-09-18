package de.wellnerbou.grok;

import io.thekraken.grok.api.Grok;
import io.thekraken.grok.api.GrokCompiler;
import io.thekraken.grok.api.Match;
import io.thekraken.grok.api.exception.GrokException;
import org.junit.Test;

import java.util.Map;

public class GrokApiTest {

	@Test
	public void testGrokApi() throws GrokException {
		GrokCompiler grokCompiler = GrokCompiler.newInstance();
		grokCompiler.registerDefaultPatterns();
		final Grok grok = grokCompiler.compile("%{COMBINEDAPACHELOG}");
		String log = "112.169.19.192 - - [06/Mar/2013:01:36:30 +0900] \"GET / HTTP/1.1\" 200 44346 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.152 Safari/537.22\"";
		Match match = grok.match(log);
		final Map<String, Object> capture = match.capture();

		for(Map.Entry<String, Object> entry : capture.entrySet()) {
			System.out.println(entry.getKey() + " => " + entry.getValue());
		}
	}

	@Test
	public void test_customLogGrokApi() throws GrokException {
		GrokCompiler grokCompiler = GrokCompiler.newInstance();
		grokCompiler.registerDefaultPatterns();
		final Grok grok = grokCompiler.compile("(%{IPORHOST:clientip},|-) %{IPORHOST:clientip2} - \\[%{HTTPDATE:timestamp}\\] \"(?:%{WORD:verb} %{NOTSPACE:request}(?: HTTP/%{NUMBER:httpversion})?|%{DATA:rawrequest})\" %{NUMBER:response} (?:%{NUMBER:bytes}|-) \"(%{NOTSPACE:referrer}|-)\" \"(?:%{GREEDYDATA:useragent})\" \"(?:%{WORD:scheme})\"");
		String log = "84.139.236.225, 192.168.191.5 - [18/Sep/2020:03:45:01 +0200] \"GET http://www.example.com/path/to/any.woff2?ksjhfkh&hkjjh HTTP/1.1\" 304 0 \"https://www.example.com/referrer?h&h\" \"Mozilla/5.0 (Linux; Android 10; SM-A750FN) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.101 Mobile Safari/537.36\" \"https\"";
		Match match = grok.match(log);
		final Map<String, Object> capture = match.capture();

		for(Map.Entry<String, Object> entry : capture.entrySet()) {
			System.out.println(entry.getKey() + " => " + entry.getValue());
		}
	}
}
