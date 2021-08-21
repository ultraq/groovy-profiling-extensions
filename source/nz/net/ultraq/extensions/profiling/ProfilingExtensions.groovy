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

package nz.net.ultraq.extensions.profiling

import org.slf4j.Logger

import groovy.transform.CompileStatic

/**
 * Extensions, often to the main {@code Object} class, for aiding with profiling
 * and performance testing.
 * 
 * @author Emanuel Rabina
 */
@CompileStatic
class ProfilingExtensions {

	private static final Map<String,Integer> executionsPerAction = [:]
	private static final Map<String,List<Long>> executionTimesPerAction = [:]
	private static final Map<String,Long> lastExecutionTimePerAction = [:]

	/**
	 * Log the average time it takes to complete the given closure, using the
	 * values of the last {@code samples} executions and emitting a log only after
	 * every {@code samples} calls.
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

		def result = sample(actionName, samples, closure)
		def executionTimes = executionTimesPerAction[actionName]
		if (executionTimes.size() > samples) {
			executionTimes.remove(0)
		}

		def executions = (executionsPerAction[actionName] ?: 0) + 1
		if (executions % samples == 0) {
			logger.debug('{} average time: {}ms.', actionName, String.format('%.2f', executionTimes.average()))
		}
		executionsPerAction[actionName] = executions

		return result
	}

	/**
	 * Log the average time it takes to complete the given closure, using the
	 * values obtained within the last {@code seconds} seconds of execution and
	 * emitting a log only after samples have been generated for the last
	 * {@code seconds} seconds.
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

		def result = sample(actionName, 0, closure)
		def executionTimes = executionTimesPerAction[actionName]
		def lastExecutionTime = lastExecutionTimePerAction.getOrCreate(actionName) { ->
			return System.currentTimeSeconds()
		}

		def currentExecutionTime = System.currentTimeSeconds()
		if (currentExecutionTime - lastExecutionTime >= seconds) {
			logger.debug('{} average time: {}ms.', actionName, String.format('%.2f', executionTimes.average()))
			lastExecutionTimePerAction[actionName] = currentExecutionTime
			executionTimes.clear()
		}

		return result
	}

	/**
	 * The same as {@link #average} but with nanosecond precision.
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

		def result = sampleNanos(actionName, samples, closure)
		def executionTimes = executionTimesPerAction[actionName]
		if (executionTimes.size() > samples) {
			executionTimes.remove(0)
		}

		def executions = (executionsPerAction[actionName] ?: 0) + 1
		if (executions % samples == 0) {
			logger.debug('{} average time: {}ns.', actionName, String.format('%.2f', executionTimes.average()))
		}
		executionsPerAction[actionName] = executions

		return result
	}

	/**
	 * Log the average time it takes to complete the given closure, using the
	 * values obtained within the last {@code seconds} seconds of execution and
	 * emitting a log only after samples have been generated for the last
	 * {@code seconds} seconds.
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

		def result = sampleNanos(actionName, 0, closure)
		def executionTimes = executionTimesPerAction[actionName]
		def lastExecutionTime = lastExecutionTimePerAction.getOrCreate(actionName) { ->
			return System.currentTimeSeconds()
		}

		def currentExecutionTime = System.currentTimeSeconds()
		if (currentExecutionTime - lastExecutionTime >= seconds) {
			logger.debug('{} average time: {}ns.', actionName, String.format('%.2f', executionTimes.average()))
			lastExecutionTimePerAction[actionName] = currentExecutionTime
			executionTimes.clear()
		}

		return result
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

		def start = System.currentTimeMillis()
		def result = closure()
		def finish = System.currentTimeMillis()
		def executionTime = finish - start

		def executionTimes = executionTimesPerAction.getOrCreate(actionName) { ->
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

		def start = System.nanoTime()
		def result = closure()
		def finish = System.nanoTime()
		def executionTime = finish - start

		def executionTimes = executionTimesPerAction.getOrCreate(actionName) { ->
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

		def start = System.currentTimeMillis()
		closure()
		def finish = System.currentTimeMillis()
		return finish - start
	}

	/**
	 * Capture and return the time it takes to perform the given closure.
	 * 
	 * @param self
	 * @param closure
	 * @return
	 *   The time the closure took, in nanoseconds, to complete.
	 */
	static long timeNanos(Object self, Closure closure) {

		def start = System.nanoTime()
		closure()
		def finish = System.nanoTime()
		return finish - start
	}

	/**
	 * Capture and log the time it takes to perform the given closure.
	 * 
	 * @param self
	 * @param actionName
	 * @param closure
	 * @param logger
	 * @return
	 *   The value returned from the closure.
	 */
	static <T> T time(Object self, String actionName, Logger logger, Closure<T> closure) {

		def start = System.currentTimeMillis()
		def result = closure()
		def finish = System.currentTimeMillis()
		def executionTime = finish - start

		logger.debug('{} complete.  Execution time: {}ms.', actionName, executionTime)

		return result
	}

	/**
	 * The same as {@link #time} but with nanosecond precision.
	 * 
	 * @param self
	 * @param actionName
	 * @param logger
	 * @param closure
	 * @return
	 *   The value returned from the closure.
	 */
	static <T> T timeNanos(Object self, String actionName, Logger logger, Closure<T> closure) {

		def start = System.nanoTime()
		def result = closure()
		def finish = System.nanoTime()
		def executionTime = finish - start

		logger.debug('{} complete.  Execution time: {}ns.', actionName, executionTime)

		return result
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

		def result = sample(actionName, samples, closure)
		def executionTimes = executionTimesPerAction[actionName]
		if (executionTimes.size() > samples) {
			executionTimes.remove(0)
		}

		logger.debug('{} complete.  Execution time: {}ms.  Average time: {}ms.',
			actionName, executionTimes.last(), String.format('%.2f', executionTimes.average()))

		return result
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

		def result = sampleNanos(actionName, samples, closure)
		def executionTimes = executionTimesPerAction[actionName]
		if (executionTimes.size() > samples) {
			executionTimes.remove(0)
		}

		logger.debug('{} complete.  Execution time: {}ns.  Average time: {}ns.',
			actionName, executionTimes.last(), String.format('%.2f', executionTimes.average()))

		return result
	}
}
