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
}
