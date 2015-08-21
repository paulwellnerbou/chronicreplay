chronicreplay
=============

Replays Apache Logfiles against a given target host considering the original time delays between requests

How to build
------------

	./gradlew build

How to run from source directory with gradle
---------------------------------------------

	./gradlew run -Pargs='--urlprefix=http://localhost --logfile=src/test/resources/combined-log-example.log --logreader=combined'

How to build a binary distribution
----------------------------------

	./gradlew installDist
	
The distribution will be put into build/install. For more options about packaging and distribution, see
http://www.gradle.org/docs/current/userguide/application_plugin.html.

To run the application, you will find the start scripts for Windows and Unix/Linux in build/install/chronicreplay/bin.

How to write a custom logfile reader
------------------------------------

The selection of the logfile reader (implementation of LogLineReader) is done using Java's ServiceLoader.
See http://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html for more information.

The steps you have to follow:

* Create your own implementation of LogLineReader
* Add the classname to src/main/resources/META-INF/services/de.wellnerbou.chronic.logreader.LogLineReader
* Use --logreader=ID-OF-YOUR-IMPLEMENTATION starting the program

You don't have to modifiy the original services file for this purpose, you can create a separate project which depends on chronicreplay and
add your services file in this project. Or, the other way round, you add a (runtime) dependency to your custom LogLineReader implementation project.
 
For a basic example, have a look at de.wellnerbou.chronic.logreader.CommonLogFormatLogLineReader.

How to get duration comparisons in your chronicreplayer's logfiles
------------------------------------------------------------------

Apache does not log the duration of the requests in the default log formats. To achieve this, you have to add '%D' to your LogFormat.
See http://httpd.apache.org/docs/current/mod/mod_log_config.html for more information of customizing your LogFormat.

To parse the microseconds you will get then, you can use the code in de.wellnerbou.chronic.logreader.CombinedWithDurationLogLineReader or you
can extend from this class, overwriting the extractDuration method or/and the parseLine method.

Plotting the results with gnuplot
---------------------------------

The CSV files created contain all data you will need to plot. The file is tab separated, which is the default data format for gnuplot.

In most cases you are mainly interested in the results containing the same status code. In the CSV data file you can not filter the lines by
<code>SameStatus=true</code> as in the log file any more. But as this is the only column containing a boolean value, filtering by '\ttrue\t'
will work.

```
find -name '*.csv' -exec sh -c 'grep -P "\ttrue\t" {} > {}.sameStatus.csv' \;
```

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
