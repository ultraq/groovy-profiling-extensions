/*
 * Copyright 2021, Emanuel Rabina (http://www.ultraq.net.nz/)
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

import groovy.transform.CompileStatic

/**
 * Extensions on the {@code Object} class for aiding with profiling and
 * performance testing.  These extensions are simply convenience methods to
 * building the appropriate {@link Profiler}.
 *
 * @author Emanuel Rabina
 */
@CompileStatic
class ProfilingExtensions {

	private static final Map<String, Profiler> profilers = [:]

	/**
	 * Log the average time it takes to complete the given closure, using the
	 * values of the last {@code samples} executions and emitting a log only after
	 * every {@code samples} calls.
	 * <p>
	 * If debug-level logging is disabled for {@code logger}, then this method
	 * just calls the closure.
	 *
	 * @param self
	 * @param format
	 *   A logging format string into which to emit the average time.  Should
	 *   contain one {@code {}} placeholder.
	 * @param samples
	 *   The number of previous executions to include in average calculation.
	 * @param logger
	 * @param closure
	 * @return
	 *   The value returned from the closure.
	 */
	static <T> T average(Object self, String format, int samples, Logger logger, Closure<T> closure) {

		return profilers
			.computeIfAbsent(logger.name + format, key -> {
				return new Profiler(logger, format, new SampleLoggingStrategy(samples))
			})
			.average(closure)
	}

	/**
	 * Log the average time it takes to complete the given closure, using the
	 * values obtained within the last {@code seconds} seconds of execution and
	 * emitting a log only after samples have been generated for the last
	 * {@code seconds} seconds.
	 * <p>
	 * If debug-level logging is disabled for {@code logger}, then this method
	 * just calls the closure.
	 *
	 * @param self
	 * @param format
	 *   A logging format string into which to emit the average time.  Should
	 *   contain one {@code {}} placeholder.
	 * @param seconds
	 * @param logger
	 * @param closure
	 * @return
	 *   The value returned from the closure.
	 */
	static <T> T average(Object self, String format, float seconds, Logger logger, Closure<T> closure) {

		return profilers
			.computeIfAbsent(logger.name + format, key -> {
				return new Profiler(logger, format, new TimedLoggingStrategy(seconds))
			})
			.average(closure)
	}

	/**
	 * The same as {@link #average(Object, String, int, Logger, Closure)} but with
	 * nanosecond precision.
	 *
	 * @param self
	 * @param format
	 *   A logging format string into which to emit the average time.  Should
	 *   contain one {@code {}} placeholder.
	 * @param samples
	 *   The number of previous executions to include in average calculation.
	 * @param logger
	 * @param closure
	 * @return
	 *   The value returned from the closure.
	 */
	static <T> T averageNanos(Object self, String format, int samples, Logger logger, Closure<T> closure) {

		return profilers
			.computeIfAbsent(logger.name + format, key -> {
				return new Profiler(logger, format, new SampleLoggingStrategy(samples), new NanosecondTimer())
			})
			.average(closure)
	}

	/**
	 * The same as {@link #average(Object, String, float, Logger, Closure)} but
	 * with nanosecond precision.
	 *
	 * @param self
	 * @param format
	 *   A logging format string into which to emit the average time.  Should
	 *   contain one {@code {}} placeholder.
	 * @param seconds
	 * @param logger
	 * @param closure
	 * @return
	 *   The value returned from the closure.
	 */
	static <T> T averageNanos(Object self, String format, float seconds, Logger logger, Closure<T> closure) {

		return profilers
			.computeIfAbsent(logger.name + format, key -> {
				return new Profiler(logger, format, new TimedLoggingStrategy(seconds), new NanosecondTimer())
			})
			.average(closure)
	}

	/**
	 * Capture and return the time it takes, in milliseconds, to perform the given
	 * closure.
	 */
	static long time(Object self, Closure closure) {

		var start = System.currentTimeMillis()
		closure()
		return System.currentTimeMillis() - start
	}

	/**
	 * Capture and log the time it takes to perform the given closure.
	 * <p>
	 * If debug-level logging is disabled for {@code logger}, then this method
	 * just calls the closure.
	 *
	 * @param self
	 * @param format
	 *   A logging format string into which to emit the time.  Should contain one
	 *   {@code {}} placeholder.
	 * @param closure
	 * @param logger
	 * @return
	 *   The value returned from the closure.
	 */
	static <T> T time(Object self, String format, Logger logger, Closure<T> closure) {

		return profilers
			.computeIfAbsent(logger.name + format, key -> {
				return new Profiler(logger, format)
			})
			.time(closure)
	}

	/**
	 * The same as {@link #time(Object, String, Logger, Closure)} but with
	 * nanosecond precision.
	 *
	 * @param self
	 * @param format
	 *   A logging format string into which to emit the time.  Should contain one
	 *   {@code {}} placeholder.
	 * @param logger
	 * @param closure
	 * @return
	 *   The value returned from the closure.
	 */
	static <T> T timeNanos(Object self, String format, Logger logger, Closure<T> closure) {

		return profilers
			.computeIfAbsent(logger.name + format, key -> {
				return new Profiler(logger, format, null, new NanosecondTimer())
			})
			.time(closure)
	}
}
