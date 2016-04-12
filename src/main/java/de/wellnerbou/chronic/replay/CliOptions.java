package de.wellnerbou.chronic.replay;

import com.lexicalscope.jewel.cli.Option;

import java.util.List;

/**
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public interface CliOptions {

	@Option
	String getLogfile();

	@Option
	String getHost();

	@Option(defaultToNull = true)
	String getHostheader();

	@Option(defaultValue = {})
	List<String> getHeader();

	@Option
	String getLogparser();

	@Option
	boolean getFollowRedirects();

	@Option(longName="no-delay")
	boolean getNoDelay();

	@Option(defaultValue = "23:59:59")
	String getUntil();

	@Option(defaultValue = "00:00:00")
	String getFrom();

	@Option(longName="wait-for-termination")
	boolean getWaitForTermination();
}
