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
	String getLogreader();

	@Option
	boolean getFollowRedirects();

	@Option(longName="no-delay")
	boolean getNoDelay();
}
