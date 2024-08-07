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
import org.slf4j.Marker
import org.slf4j.MarkerFactory

import groovy.transform.CompileStatic

/**
 * Extensions, often to the main {@code Object} class, for aiding with profiling
 * and performance testing.
 *
 * @author Emanuel Rabina
 */
@CompileStatic
class ProfilingExtensions {

	static final Marker profilingMarker = MarkerFactory.getMarker("Profiling")

	private static final Map<String,Integer> executionsPerAction = [:]
	private static final Map<String,List<Long>> executionTimesPerAction = [:]
	private static final Map<String,Long> lastExecutionTimePerAction = [:]

	/**
	 * Log the average time it takes to complete the given closure, using the
	 * values of the last {@code samples} executions and emitting a log only after
	 * every {@code samples} calls.
	 * <p>
	 * If debug-level logging is disabled for {@code logger}, then this method
	 * just calls the closure.
	 *
	 * @param self
	 * @param actionName
	 * @param samples
	 *   The number of previous executions to include in average calculation.
	 * @param logger
	 * @param closure
	 * @return
	 *   The value returned from the closure.
	 */
	static <T> T average(Object self, String actionName, int samples, Logger logger, Closure<T> closure) {

		if (logger.debugEnabled) {
			var result = sample(actionName, samples, closure)
			var executionTimes = executionTimesPerAction[actionName]
			if (executionTimes.size() > samples) {
				executionTimes.remove(0)
			}

			var executions = (executionsPerAction[actionName] ?: 0) + 1
			if (executions % samples == 0) {
				logger.debug(profilingMarker, '{} average time: {}ms', actionName, String.format('%.2f', executionTimes.average()))
			}
			executionsPerAction[actionName] = executions

			return result
		}

		return closure()
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
	 * @param actionName
	 * @param seconds
	 * @param logger
	 * @param closure
	 * @return
	 *   The value returned from the closure.
	 */
	static <T> T average(Object self, String actionName, float seconds, Logger logger, Closure<T> closure) {

		if (logger.debugEnabled) {
			var result = sample(actionName, 0, closure)
			var executionTimes = executionTimesPerAction[actionName]
			var lastExecutionTime = lastExecutionTimePerAction.getOrCreate(actionName) { ->
				return System.currentTimeMillis()
			}

			var currentExecutionTime = System.currentTimeMillis()
			if ((currentExecutionTime - lastExecutionTime) / 1000 >= seconds) {
				logger.debug(profilingMarker, '{} average time: {}ms', actionName, String.format('%.2f', executionTimes.average()))
				lastExecutionTimePerAction[actionName] = currentExecutionTime
				executionTimes.clear()
			}

			return result
		}

		return closure()
	}

	/**
	 * The same as {@link #average(Object, String, int, Logger, Closure)} but with
	 * nanosecond precision.
	 *
	 * @param self
	 * @param actionName
	 * @param samples
	 *   The number of previous executions to include in average calculation.
	 * @param logger
	 * @param closure
	 * @return
	 *   The value returned from the closure.
	 */
	static <T> T averageNanos(Object self, String actionName, int samples, Logger logger, Closure<T> closure) {

		if (logger.debugEnabled) {
			var result = sampleNanos(actionName, samples, closure)
			var executionTimes = executionTimesPerAction[actionName]
			if (executionTimes.size() > samples) {
				executionTimes.remove(0)
			}

			var executions = (executionsPerAction[actionName] ?: 0) + 1
			if (executions % samples == 0) {
				logger.debug(profilingMarker, '{} average time: {}ns', actionName, String.format('%.2f', executionTimes.average()))
			}
			executionsPerAction[actionName] = executions

			return result
		}

		return closure()
	}

	/**
	 * The same as {@link #average(Object, String, float, Logger, Closure)} but
	 * with nanosecond precision.
	 *
	 * @param self
	 * @param actionName
	 * @param seconds
	 * @param logger
	 * @param closure
	 * @return
	 *   The value returned from the closure.
	 */
	static <T> T averageNanos(Object self, String actionName, float seconds, Logger logger, Closure<T> closure) {

		if (logger.debugEnabled) {
			var result = sampleNanos(actionName, 0, closure)
			var executionTimes = executionTimesPerAction[actionName]
			var lastExecutionTime = lastExecutionTimePerAction.getOrCreate(actionName) { ->
				return System.currentTimeMillis()
			}

			var currentExecutionTime = System.currentTimeMillis()
			if ((currentExecutionTime - lastExecutionTime) / 1000 >= seconds) {
				logger.debug(profilingMarker, '{} average time: {}ns', actionName, String.format('%.2f', executionTimes.average()))
				lastExecutionTimePerAction[actionName] = currentExecutionTime
				executionTimes.clear()
			}

			return result
		}

		return closure()
	}

	/**
	 * Sample, with millisecond precision, the amount of time it takes to complete
	 * the given closure.
	 *
	 * @param actionName
	 * @param samples
	 * @param closure
	 * @return
	 *   The value returned from the closure.
	 */
	private static <T> T sample(String actionName, int samples, Closure<T> closure) {

		var start = System.currentTimeMillis()
		var result = closure()
		var finish = System.currentTimeMillis()
		var executionTime = finish - start

		var executionTimes = executionTimesPerAction.getOrCreate(actionName) { ->
			return new ArrayList<Long>(samples)
		}
		executionTimes << executionTime

		return result
	}

	/**
	 * Sample, with nanosecond precision, the amount of time it takes to complete
	 * the given closure.
	 *
	 * @param actionName
	 * @param samples
	 * @param closure
	 * @return
	 *   The value returned from the closure.
	 */
	private static <T> T sampleNanos(String actionName, int samples, Closure<T> closure) {

		var start = System.nanoTime()
		var result = closure()
		var finish = System.nanoTime()
		var executionTime = finish - start

		var executionTimes = executionTimesPerAction.getOrCreate(actionName) { ->
			return new ArrayList<Long>(samples)
		}
		executionTimes << executionTime

		return result
	}

	/**
	 * Capture and return the time it takes to perform the given closure.
	 *
	 * @param self
	 * @param closure
	 * @return
	 *   The time the closure took, in milliseconds, to complete.
	 */
	static long time(Object self, Closure closure) {

		var start = System.currentTimeMillis()
		closure()
		var finish = System.currentTimeMillis()
		return finish - start
	}

	/**
	 * The same as {@link #time(Object, Closure)} but with nanosecond precision.
	 *
	 * @param self
	 * @param closure
	 * @return
	 *   The time the closure took, in nanoseconds, to complete.
	 */
	static long timeNanos(Object self, Closure closure) {

		var start = System.nanoTime()
		closure()
		var finish = System.nanoTime()
		return finish - start
	}

	/**
	 * Capture and log the time it takes to perform the given closure.
	 * <p>
	 * If debug-level logging is disabled for {@code logger}, then this method
	 * just calls the closure.
	 *
	 * @param self
	 * @param actionName
	 * @param closure
	 * @param logger
	 * @return
	 *   The value returned from the closure.
	 */
	static <T> T time(Object self, String actionName, Logger logger, Closure<T> closure) {

		if (logger.debugEnabled) {
			var start = System.currentTimeMillis()
			var result = closure()
			var finish = System.currentTimeMillis()
			var executionTime = finish - start

			logger.debug(profilingMarker, '{} execution time: {}ms', actionName, executionTime)

			return result
		}

		return closure()
	}

	/**
	 * The same as {@link #time(Object, String, Logger, Closure)} but with
	 * nanosecond precision.
	 *
	 * @param self
	 * @param actionName
	 * @param logger
	 * @param closure
	 * @return
	 *   The value returned from the closure.
	 */
	static <T> T timeNanos(Object self, String actionName, Logger logger, Closure<T> closure) {

		if (logger.debugEnabled) {
			var start = System.nanoTime()
			var result = closure()
			var finish = System.nanoTime()
			var executionTime = finish - start

			logger.debug(profilingMarker, '{} execution time: {}ns', actionName, executionTime)

			return result
		}

		return closure()
	}

	/**
	 * Capture and log the time it takes to perform the given closure, and the
	 * average of the last {@code samples} executions of the specific action.
	 *
	 * @param self
	 * @param actionName
	 * @param samples
	 *   The number of previous executions to include in average calculation.
	 * @param logger
	 * @param closure
	 * @return
	 *   The value returned from the closure.
	 */
	static <T> T timeWithAverage(Object self, String actionName, int samples, Logger logger, Closure<T> closure) {

		if (logger.debugEnabled) {
			var result = sample(actionName, samples, closure)
			var executionTimes = executionTimesPerAction[actionName]
			if (executionTimes.size() > samples) {
				executionTimes.remove(0)
			}

			logger.debug(profilingMarker, '{} execution time: {}ms, average time: {}ms',
				actionName, executionTimes.last(), String.format('%.2f', executionTimes.average()))

			return result
		}

		return closure()
	}

	/**
	 * The same as {@link #timeWithAverage} but with nanosecond precision.
	 *
	 * @param self
	 * @param actionName
	 * @param samples
	 *   The number of previous executions to include in average calculation.
	 * @param logger
	 * @param closure
	 * @return
	 *   The value returned from the closure.
	 */
	static <T> T timeWithAverageNanos(Object self, String actionName, int samples, Logger logger, Closure<T> closure) {

		if (logger.debugEnabled) {
			var result = sampleNanos(actionName, samples, closure)
			var executionTimes = executionTimesPerAction[actionName]
			if (executionTimes.size() > samples) {
				executionTimes.remove(0)
			}

			logger.debug(profilingMarker, '{} execution time: {}ns, average time: {}ns',
				actionName, executionTimes.last(), String.format('%.2f', executionTimes.average()))

			return result
		}

		return closure()
	}
}
