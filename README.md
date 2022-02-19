
groovy-profiling-extensions
===========================

[![Build Status](https://github.com/ultraq/groovy-profiling-extensions/actions/workflows/build.yml/badge.svg)](https://github.com/ultraq/groovy-profiling-extensions/actions)
[![codecov](https://codecov.io/gh/ultraq/groovy-profiling-extensions/branch/main/graph/badge.svg?token=FOLRRS9CNJ)](https://codecov.io/gh/ultraq/groovy-profiling-extensions)
[![Maven Central](https://img.shields.io/maven-central/v/nz.net.ultraq.groovy/groovy-profiling-extensions.svg?maxAge=3600)](http://search.maven.org/#search|ga|1|g%3A%22nz.net.ultraq.groovy%22%20AND%20a%3A%22groovy-profiling-extensions%22)

A collection of Groovy extensions to aid with profiling an application.


Installation
------------

Minimum of Java 8 required.  Logging is done via [SLF4J](http://www.slf4j.org/),
so some implementation of it is needed to pass to the methods to emit logs.

Add a dependency to your project with the following co-ordinates:

 - GroupId: `nz.net.ultraq.groovy`
 - ArtifactId: `groovy-profiling-extensions`
 - Version: `0.6.1`

Check the [project releases](https://github.com/ultraq/groovy-profiling-extensions/releases)
for a list of available versions.


API
---

These methods are applied as static extensions of the `Object` class, so are
available everywhere.

### average(String actionName, int samples, Logger logger, Closure closure)

Log the average time it takes to complete the given closure, using the values of
the last `samples` executions and emitting a log only after every `samples`
calls.

Also comes in an `averageNanos` variant which uses nanosecond precision.

### average(String actionName, float seconds, Logger logger, Closure closure)

Log the average time it takes to complete the given closure, using the values
obtained within the last `seconds` seconds of execution and emitting a log only
after samples have been generated for the last `seconds` seconds.

Also comes in an `averageNanos` variant which uses nanosecond precision.

### time(Closure closure)

Capture and return the time it takes to perform the given closure.

Also comes in a `timeNanos` variant which uses nanosecond precision.

### time(String actionName, Logger logger, Closure closure)

Capture and log the time it takes to perform the given closure.

Also comes in a `timeNanos` variant which uses nanosecond precision.

### timeWithAverage(String actionName, int samples, Logger logger, Closure closure)

Capture and log the time it takes to perform the given closure, and the average
of the last `samples` executions of the specific action.

Also comes in a `timeWithAverageNanos` variant which uses nanosecond precision.
