package de.wellnerbou.chronic.replay;

import com.lexicalscope.jewel.cli.Option;

import java.util.List;

public interface CliOptions {

	@Option
	String getLogfile();

	@Option(defaultToNull = true)
	String getHost();

	@Option(defaultToNull = true)
	List<String> getHostmap();

	@Option(defaultToNull = true)
	String getHostheader();

	@Option(defaultToNull = true)
	String getCustomUserAgent();

	@Option(defaultValue = {})
	List<String> getHeader();

	@Option
	String getLogparser();

	@Option(defaultValue = "csv")
	String getLogger();

	@Option
	boolean getFollowRedirects();

	@Option(defaultToNull = true, description = "Delay between requests in milliseconds")
	Long getDelay();

	@Option(defaultValue = "23:59:59")
	String getUntil();

	@Option(defaultValue = "00:00:00")
	String getFrom();

	@Option(longName = "wait-for-termination")
	boolean getWaitForTermination();

	@Option(longName = "logreader", defaultValue = "file")
	String getLogreader();

	@Option(longName = "grokpattern", defaultToNull = true, description = "Grok pattern to parse log lines, used only for --logparser=grok")
	String getGrokPattern();
}
