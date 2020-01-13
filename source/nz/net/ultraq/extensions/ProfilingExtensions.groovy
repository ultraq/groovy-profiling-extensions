/* 
 * Copyright 2019, Emanuel Rabina (http://www.ultraq.net.nz/)
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

package nz.net.ultraq.extensions

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import groovy.transform.CompileStatic

/**
 * Extensions, often to the main {@code Object} class, for aiding with profiling
 * and performance testing.
 * 
 * @author Emanuel Rabina
 */
@CompileStatic
class ProfilingExtensions {

	private static final Logger logger = LoggerFactory.getLogger(ProfilingExtensions)
	private static final Map<String,Integer> executionsPerAction = [:]
	private static final Map<String,List<Long>> executionTimesPerAction = [:]

	/**
	 * Log the average time it takes to complete the given closure, using the
	 * values of the last {@code samples} executions and emitting a log only after
	 * every {@code samples} calls.
	 * 
	 * @param self
	 * @param actionName
	 * @param samples
	 *   The number of previous executions to include in average calculation.
	 * @return
	 */
	static <T> T average(Object self, String actionName, int samples, Closure<T> closure) {

		def start = System.currentTimeMillis()
		def result = closure()
		def finish = System.currentTimeMillis()
		def executionTime = finish - start

		def executionTimes = executionTimesPerAction.getOrCreate(actionName) { ->
			return new ArrayList<Long>(samples)
		}
		if (executionTimes.size() == samples) {
			executionTimes.remove(0)
		}
		executionTimes << executionTime

		def executions = (executionsPerAction[actionName] ?: 0) + 1
		if (executions % samples == 0) {
			def averageTime = (Long)executionTimes.sum() / executionTimes.size()
			logger.debug('{} average time: {}ms.', actionName, String.format('%.2f', averageTime))
		}
		executionsPerAction[actionName] = executions

		return result
	}

	/**
	 * Capture and log the time it takes to perform the given closure.
	 * 
	 * @param self
	 * @param actionName
	 * @param closure
	 * @return
	 */
	static <T> T time(Object self, String actionName, Closure<T> closure) {

		def start = System.currentTimeMillis()
		def result = closure()
		def finish = System.currentTimeMillis()

		def executionTime = finish - start
		logger.debug('{} complete.  Execution time: {}ms.', actionName, executionTime)

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
	 * @param closure
	 * @return
	 */
	static <T> T timeWithAverage(Object self, String actionName, int samples, Closure<T> closure) {

		def start = System.currentTimeMillis()
		def result = closure()
		def finish = System.currentTimeMillis()
		def executionTime = finish - start

		def executionTimes = executionTimesPerAction.getOrCreate(actionName) { ->
			return new ArrayList<Long>(samples)
		}
		if (executionTimes.size() == samples) {
			executionTimes.remove(0)
		}
		executionTimes << executionTime

		def averageTime = (Long)executionTimes.sum() / executionTimes.size()
		logger.debug('{} complete.  Execution time: {}ms.  Average time: {}ms.', actionName, executionTime, String.format('%.2f', averageTime))

		return result
	}
}
