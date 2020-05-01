
profiling-extensions
====================

[![Build Status](https://travis-ci.com/ultraq/profiling-extensions.svg)](https://travis-ci.com/ultraq/profiling-extensions)
[![Coverage Status](https://coveralls.io/repos/github/ultraq/profiling-extensions/badge.svg?branch=master)](https://coveralls.io/github/ultraq/profiling-extensions?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/nz.net.ultraq.extensions/profiling-extensions.svg?maxAge=3600)](http://search.maven.org/#search|ga|1|g%3A%22nz.net.ultraq.extensions%22%20AND%20a%3A%22profiling-extensions%22)

A collection of Groovy extensions to aid with profiling an application.


Installation
------------

Minimum of Java 8 required.  Logging is done via [SLF4J](http://www.slf4j.org/),
so some implementation of it is needed to be able to view the logs later.

Add a dependency to your project with the following co-ordinates:

 - GroupId: `nz.net.ultraq.extensions`
 - ArtifactId: `profiling-extensions`
 - Version: `1.0.0-SNAPSHOT`

Check the [project releases](https://github.com/ultraq/profiling-extensions/releases)
for a list of available versions.


API
---

These methods are applied as static extensions of the `Object` class, so are
available everywhere.

### average(String actionName, int samples, int frequency, Closure closure)

Log the average time it takes to complete the given closure, using the values of
the last `samples` executions and emitting a log only after every `frequency`
calls.

### time(String actionName, Closure closure)

Capture and log the time it takes to perform the given closure.

### timeWithAverage(String actionName, int samples, Closure closure)

Capture and log the time it takes to perform the given closure, and the average
of the last `samples` executions of the specific action.
