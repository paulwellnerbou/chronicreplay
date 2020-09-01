package de.wellnerbou.chronic.replay;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.Cli;
import com.lexicalscope.jewel.cli.CliFactory;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public class CliOptionsTest {

	private Cli<CliOptions> cli;

	@Before
	public void setUp() throws Exception {
		cli = CliFactory.createCli(CliOptions.class);
	}

	@Test(expected = ArgumentValidationException.class)
	public void testNoArgs() {
		cli.parseArguments();
	}

	@Test
	public void testOptionalOptions() {
		final CliOptions options = cli.parseArguments("--logparser", "simple", "--host", "http://localhost", "--logfile", "");
		Assertions.assertThat(options.getFollowRedirects()).isFalse();
		Assertions.assertThat(options.getHeader()).isEmpty();
		Assertions.assertThat(options.getWaitForTermination()).isFalse();
		Assertions.assertThat(options.getLogreader()).isEqualTo("file");
	}

	@Test
	public void testFollowRedirectsFlag() {
		final CliOptions options = cli.parseArguments("--logparser", "simple", "--host", "http://localhost", "--logfile", "", "--followRedirects");
		Assertions.assertThat(options.getFollowRedirects()).isTrue();
	}

	@Test
	public void testWaitForTerminationFlag() {
		final CliOptions options = cli.parseArguments("--logparser", "simple", "--host", "http://localhost", "--logfile", "", "--wait-for-termination");
		Assertions.assertThat(options.getWaitForTermination()).isTrue();
	}

	@Test
	public void testDelayOption() {
		final CliOptions options = cli.parseArguments("--logparser", "simple", "--host", "http://localhost", "--logfile", "", "--delay=0");
		Assertions.assertThat(options.getDelay()).isEqualTo(0);
	}

	@Test
	public void testLogReaderOption() {
		final CliOptions options = cli.parseArguments("--logparser", "simple", "--host", "http://localhost", "--logfile", "", "--logreader", "url");
		Assertions.assertThat(options.getLogreader()).isEqualTo("url");
	}

	@Test
	public void testHeaderList() {
		final CliOptions options = cli.parseArguments("--logparser", "simple", "--host", "http://localhost", "--logfile", "", "--header", "Accept: nothing", "--header", "Host: example.com");
		Assertions.assertThat(options.getHeader()).hasSize(2);
	}

	@Test
	public void testLoggerDefaultValue() {
		final CliOptions options = cli.parseArguments("--logparser", "simple", "--host", "http://localhost", "--logfile", "", "--followRedirects");
		Assertions.assertThat(options.getLogger()).isEqualTo("csv");
	}

	@Test
	public void testLogger() {
		final CliOptions options = cli.parseArguments("--logger", "json", "--logparser", "simple", "--host", "http://localhost", "--logfile", "", "--followRedirects");
		Assertions.assertThat(options.getLogger()).isEqualTo("json");
	}
}
