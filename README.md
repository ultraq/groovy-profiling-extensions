
profiling-extensions
====================

[![Build Status](https://travis-ci.com/ultraq/profiling-extensions.svg)](https://travis-ci.com/ultraq/profiling-extensions)
[![GitHub Release](https://img.shields.io/github/release/ultraq/profiling-extensions.svg?maxAge=3600)](https://github.com/ultraq/profiling-extensions/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/nz.net.ultraq.extensions/profiling-extensions.svg?maxAge=3600)](http://search.maven.org/#search|ga|1|g%3A%22nz.net.ultraq.extensions%22%20AND%20a%3A%22profiling-extensions%22)
[![License](https://img.shields.io/github/license/ultraq/profiling-extensions.svg?maxAge=2592000)](https://github.com/ultraq/profiling-extensions/blob/master/LICENSE.txt)

A collection of Groovy extensions to aid with profiling an application.


Installation
------------

Minimum of Java 8 required.

Add a dependency to your project with the following co-ordinates:

 - GroupId: `nz.net.ultraq.extensions`
 - ArtifactId: `profiling-extensions`
 - Version: (check the badge above or [the list of releases](https://github.com/ultraq/profiling-extensions/releases)
   for available versions)


API
---

### time(String actionName, Closure closure)

Capture and log the time it takes to perform the given closure.  Logging is done
via [SLF4J](http://www.slf4j.org/), so some implementation of it is needed to be
able to view the logs later.
