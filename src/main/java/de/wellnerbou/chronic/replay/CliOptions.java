package de.wellnerbou.chronic.replay;

import com.lexicalscope.jewel.cli.Option;
import com.ning.http.client.NameResolver;

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

	@Option(defaultToNull = true)
	String getUntil();

	@Option(defaultToNull = true)
	String getFrom();

	@Option(longName = "wait-for-termination")
	boolean getWaitForTermination();

	@Option(longName = "logreader", defaultValue = "file")
	String getLogreader();

	@Option(longName = "grokpattern", defaultToNull = true, description = "Grok pattern to parse log lines, used only for --logparser=grok")
	String getGrokPattern();

	@Option(longName = "resolve", defaultToNull = true)
    String getResolve();

	@Option(longName = "httpProvider", defaultValue = "netty")
	String getHttpProvider();

	@Option(longName = "repeat", defaultValue = "1")
	Integer getRepetitions();

	@Option(longName = "insecure")
	boolean getInsecure();
}
