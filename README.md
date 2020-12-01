chronicreplay
=============

Replays Logfiles (especially Apache logfiles) against a given target host considering the original time delays between requests

How to run
------------

Download the [latest release](https://github.com/paulwellnerbou/chronicreplay/releases/latest), unzip it and run it:

```
wget https://github.com/paulwellnerbou/chronicreplay/releases/download/v1.5.0/chronicreplay-1.5.0.zip
unzip chronicreplay*.zip
./chronicreplay-*/bin/chronicreplay --host=host.to.replay.against --logfile=/path/to/your/logfile --logparser=combined
```

Where "combined" is the name of the logfile reader exptecting Apache's combined log format
(see [Apache's documentation](http://httpd.apache.org/docs/current/mod/mod_log_config.html) and
[CombinedLogFormatLogLineReader](src/main/java/de/wellnerbou/chronic/logreader/CombinedLogFormatLogLineReader.java)'s JavaDoc).

Standard Log Level is set as environment variable, named LOG_LEVEL, default value is DEBUG. 
To change the Log Level, that environment variable needs to be altered.

How to build
------------

	./gradlew build

How to run from source directory with gradle
---------------------------------------------

	./gradlew run -Pargs='--host=http://localhost --logfile=src/test/resources/combined-log-example.log --logparser=combined'

How to build a binary distribution
----------------------------------

	./gradlew installDist
	
The distribution will be put into build/install. For more options about packaging and distribution, see
http://www.gradle.org/docs/current/userguide/application_plugin.html.

To run the application, you will find the start scripts for Windows and Unix/Linux in build/install/chronicreplay/bin.

How to write a custom log line parser
------------------------------------

The selection of the logfile reader (implementation of LogLineReader) is done using Java's ServiceLoader.
See http://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html for more information.

The steps you have to follow:

* Create your own implementation of LogLineParser
* Add the classname to src/main/resources/META-INF/services/de.wellnerbou.chronic.logparser.LogLineParser
* Use --logparser=ID-OF-YOUR-IMPLEMENTATION starting the program

You don't have to modifiy the original services file for this purpose, you can create a separate project which depends on chronicreplay and
add your services file in this project. Or, the other way round, you add a (runtime) dependency to your custom LogLineReader implementation project.
 
For a basic example, have a look at de.wellnerbou.chronic.logparser.CommonLogFormatLogLineParser.

How to get duration comparisons in your chronicreplayer's logfiles
------------------------------------------------------------------

Apache does not log the duration of the requests in the default log formats. To achieve this, you have to add '%D' to your LogFormat.
See (http://httpd.apache.org/docs/current/mod/mod_log_config.html) for more information of customizing your LogFormat.

To parse the microseconds you will get then, you can use the code in `CombinedWithDurationLogLineParser` or you
can extend from this class, overwriting the extractDuration method or/and the parseLine method.

Plotting the results with gnuplot
---------------------------------

The CSV files created contain all data you will need to plot. The file is tab separated, which is the default data format for gnuplot.

In most cases you are mainly interested in the results containing the same status code. That's why the responses with the same status as
the original ones are logged additionally in another file, ending with <code>.sameStatus.csv</code>.

Plotting the durations over time with gnuplot:

```
gnuplot> set timefmt "%s"
gnuplot> set format x "%H:%M:%S"
gnuplot> set xdata time
gnuplot> plot "chronicreplay-host_2015-08-21_08-48-42.csv" using 1:6
```

Overlaying results from different servers in one plot:

```
gnuplot> set timefmt "%s"
gnuplot> set format x "%H:%M:%S"
gnuplot> set xdata time
gnuplot> plot "chronicreplay-host1_2015-08-21_08-48-42.csv" using 1:6, "chronicreplay-host2_2015-08-21_08-48-42.csv" using 1:6
```
