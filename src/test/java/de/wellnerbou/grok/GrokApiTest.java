package de.wellnerbou.grok;

import io.thekraken.grok.api.Grok;
import io.thekraken.grok.api.Match;
import io.thekraken.grok.api.exception.GrokException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Map;

public class GrokApiTest {

	@Test
	public void testGrokApi() throws GrokException {
		/* Create a new grok instance */
		Grok grok = Grok.create();

		/* Grok pattern to compile, here httpd logs */
		grok.compile("%{COMBINEDAPACHELOG}");

		/* Line of log to match */
		String log = "112.169.19.192 - - [06/Mar/2013:01:36:30 +0900] \"GET / HTTP/1.1\" 200 44346 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.152 Safari/537.22\"";

		Match gm = grok.match(log);
		gm.captures();

		for(Map.Entry<String, Object> entry : gm.toMap().entrySet()) {
			System.out.println(entry.getKey() + " => " + entry.getValue());
		}
	}
}
