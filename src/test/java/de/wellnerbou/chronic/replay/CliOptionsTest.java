package de.wellnerbou.chronic.replay;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.Cli;
import com.lexicalscope.jewel.cli.CliFactory;
import org.fest.assertions.api.Assertions;
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
		final CliOptions options = cli.parseArguments("--logreader", "simple", "--host", "http://localhost", "--logfile", "");
		Assertions.assertThat(options.getFollowRedirects()).isFalse();
		Assertions.assertThat(options.getHeader()).isEmpty();
		Assertions.assertThat(options.getWaitForTermination()).isFalse();
	}

	@Test
	public void testFollowRedirectsFlag() {
		final CliOptions options = cli.parseArguments("--logreader", "simple", "--host", "http://localhost", "--logfile", "", "--followRedirects");
		Assertions.assertThat(options.getFollowRedirects()).isTrue();
	}

	@Test
	public void testWaitForTerminationFlag() {
		final CliOptions options = cli.parseArguments("--logreader", "simple", "--host", "http://localhost", "--logfile", "", "--wait-for-termination");
		Assertions.assertThat(options.getWaitForTermination()).isTrue();
	}

	@Test
	public void testNoDelayFlag() {
		final CliOptions options = cli.parseArguments("--logreader", "simple", "--host", "http://localhost", "--logfile", "", "--no-delay");
		Assertions.assertThat(options.getNoDelay()).isTrue();
	}

	@Test
	public void testRequestFilter() {
		final CliOptions options = cli.parseArguments("--logreader", "simple", "--host", "http://localhost", "--logfile", "", "--request-filter=\"^/req.*\"");
		Assertions.assertThat(options.getRequestFilter()).isEqualTo("^/req.*");
	}

	@Test
	public void testHeaderList() {
		final CliOptions options = cli.parseArguments("--logreader", "simple", "--host", "http://localhost", "--logfile", "", "--header", "Accept: nothing", "--header", "Host: example.com");
		Assertions.assertThat(options.getHeader()).hasSize(2);
	}
}
