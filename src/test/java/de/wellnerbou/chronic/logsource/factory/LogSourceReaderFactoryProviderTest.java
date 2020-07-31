package de.wellnerbou.chronic.logsource.factory;

import de.wellnerbou.chronic.plugins.NoImplementationFoundException;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class LogSourceReaderFactoryProviderTest {

    @Test(expected = NoImplementationFoundException.class)
    public void testGetNoImplementation() {
        new LogSourceReaderFactoryProvider().getImplementation("");
    }

    @Test
    public void getFileImplementation() {
		final LogSourceReaderFactory readerFactory = new LogSourceReaderFactoryProvider().getImplementation("file");
		Assertions.assertThat(readerFactory).isInstanceOf(LogFileReaderFactory.class);
	}
}
