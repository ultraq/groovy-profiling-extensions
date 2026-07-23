/*
 * Copyright 2026, Emanuel Rabina (http://www.ultraq.net.nz/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nz.net.ultraq.groovy.profilingextensions

import org.slf4j.Logger
import org.slf4j.Marker
import org.slf4j.MarkerFactory

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor

/**
 * A class for capturing and logging various aspects of performance of a single
 * or repeated action.
 *
 * @author Emanuel Rabina
 */
@CompileStatic
@TupleConstructor
class Profiler {

	static final Marker marker = MarkerFactory.getMarker("Profiling")

	final Logger logger
	final String format
	final LoggingStrategy loggingStrategy
	final Timer timer = new MillisecondTimer()
	private final List<Long> executionTimes = new ArrayList<Long>()

	/**
	 * Log the average time it takes to complete the given closure, only when the
	 * given {@link #loggingStrategy} says one should be emitted.
	 * <p>
	 * If debug-level logging is disabled for {@code logger}, then this method
	 * just calls the closure.
	 */
	@SuppressWarnings('GrUnnecessaryPublicModifier')
	public <T> T average(Closure<T> closure) {

		if (logger.debugEnabled) {
			var start = timer.now()
			var result = closure()
			executionTimes << timer.now() - start

			if (loggingStrategy.shouldLog()) {
				logger.debug(marker, format, String.format('%.2f', executionTimes.average()))
				executionTimes.clear()
			}

			return result
		}

		return closure()
	}

	/**
	 * Log the time it takes to complete the given closure.
	 */
	<T> T time(Closure<T> closure) {

		if (logger.debugEnabled) {
			var start = timer.now()
			var result = closure()
			var executionTime = timer.now() - start

			logger.debug(marker, format, executionTime)

			return result
		}

		return closure()
	}
}
