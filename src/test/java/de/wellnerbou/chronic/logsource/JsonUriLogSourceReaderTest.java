package de.wellnerbou.chronic.logsource;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public class JsonUriLogSourceReaderTest {

	@Test
	public void testReadFromClasspathFile() throws IOException {
		final String resourceUrlStr = "/elasticsearch-example.json";
		final InputStream is = this.getClass().getResourceAsStream(resourceUrlStr);

		JsonUriLogSourceReader jsonUriLogSourceReader = new JsonUriLogSourceReader(new InputStreamReader(is));
		jsonUriLogSourceReader.close();
	}
}
