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

import com.github.valfirst.slf4jtest.TestLogger
import com.github.valfirst.slf4jtest.TestLoggerFactory
import spock.lang.Specification
import static com.github.valfirst.slf4jtest.Assertions.assertThat

import java.util.concurrent.atomic.AtomicInteger

/**
 * Tests for the profiling extension methods.
 *
 * @author Emanuel Rabina
 */
class ProfilingExtensionsTests extends Specification {

	private static AtomicInteger loggerNumber = new AtomicInteger()
	private TestLogger logger

	def setup() {
		logger = TestLoggerFactory.getTestLogger("ProfilingExtensionTestsLogger-${loggerNumber.getAndIncrement()}")
	}

	def "#average - Log the average of all executions after the given number of samples"() {
		given:
			var actionName = 'Test average'
			var samples = 2
		when:
			samples.times {
				average(actionName, samples, logger) {
					// Nothing happening here
				}
			}
		then:
			assertThat(logger).hasLogged { event ->
				return event.message == '{} average time: {}ms' &&
					event.arguments[0] == actionName &&
					event.arguments[1] ==~ /\d+\.\d{2}/
			}
	}

	def "#average - Log the average of all executions after the given amount of time"() {
		given:
			var actionName = 'Test average'
			var seconds = 0.5f
		when:
			var startExecutionTime = System.currentTimeMillis()
			var breakNextLoop = false
			while (!breakNextLoop) {
				var currentExecutionTime = System.currentTimeMillis()
				if ((currentExecutionTime - startExecutionTime) / 1000 > seconds) {
					breakNextLoop = true
				}
				average(actionName, seconds, logger) {
					// Nothing happening here
				}
				Thread.sleep(100)
			}
		then:
			assertThat(logger).hasLogged { event ->
				return event.message == '{} average time: {}ms' &&
					event.arguments[0] == actionName &&
					event.arguments[1] ==~ /\d+\.\d{2}/
			}
	}

	def "#averageNanos - Log the average of all executions after the given number of samples"() {
		given:
			var actionName = 'Test averageNanos'
			var samples = 2
		when:
			samples.times {
				averageNanos(actionName, samples, logger) {
					// Nothing happening here
				}
			}
		then:
			assertThat(logger).hasLogged { event ->
				return event.message == '{} average time: {}ns' &&
					event.arguments[0] == actionName &&
					event.arguments[1] ==~ /\d+\.\d{2}/
			}
	}

	def "#averageNanos - Log the average of all executions after the given amount of time"() {
		given:
			var actionName = 'Test average'
			var seconds = 0.5f
		when:
			var startExecutionTime = System.currentTimeMillis()
			var breakNextLoop = false
			while (!breakNextLoop) {
				var currentExecutionTime = System.currentTimeMillis()
				if ((currentExecutionTime - startExecutionTime) / 1000 > seconds) {
					breakNextLoop = true
				}
				averageNanos(actionName, seconds, logger) {
					// Nothing happening here
				}
				Thread.sleep(100)
			}
		then:
			assertThat(logger).hasLogged { event ->
				return event.message == '{} average time: {}ns' &&
					event.arguments[0] == actionName &&
					event.arguments[1] ==~ /\d+\.\d{2}/
			}
	}

	def "#time - Returns the time it took for the closure to execute"() {
		given:
			var executed = false
		when:
			var result = time() { ->
				executed = true
			}
		then:
			result instanceof Long
			executed == true
	}

	def "#time - Logs the time the closure took to execute, returning its result"() {
		given:
			var actionName = 'Test time'
		when:
			var result = time(actionName, logger) {
				return 'Hi!'
			}
		then:
			assertThat(logger).hasLogged { event ->
				return event.message == '{} execution time: {}ms' &&
					event.arguments[0] == actionName &&
					event.arguments[1] instanceof Long
			}
			result == 'Hi!'
	}

	def "#timeNanos - Returns the time it took for the closure to execute"() {
		given:
			var executed = false
		when:
			var result = timeNanos() { ->
				executed = true
			}
		then:
			result instanceof Long
			executed == true
	}

	def "#timeNanos - Logs the time the closure took to execute, returning its result"() {
		given:
			var actionName = 'Test timeNanos'
		when:
			var result = timeNanos(actionName, logger) {
				return 'Hi!'
			}
		then:
			assertThat(logger).hasLogged { event ->
				return event.message == '{} execution time: {}ns' &&
					event.arguments[0] == actionName &&
					event.arguments[1] instanceof Long
			}
			result == 'Hi!'
	}

	def "#timeWithAverage - Logs the current and average time the closure took to execute"() {
		given:
			var actionName = 'Test timeWithAverage'
			var samples = 2
		when:
			samples.times {
				timeWithAverage(actionName, samples, logger) {
					// Nothing happening here
				}
			}
		then:
			assertThat(logger).hasLogged { event ->
				return event.message == '{} execution time: {}ms, average time: {}ms' &&
					event.arguments[0] == actionName &&
					event.arguments[1] instanceof Long &&
					event.arguments[2] ==~ /\d+\.\d{2}/
			}
	}

	def "#timeWithAverageNanos - Logs the current and average time the closure took to execute"() {
		given:
			var actionName = 'Test timeWithAverageNanos'
			var samples = 2
		when:
			samples.times {
				timeWithAverageNanos(actionName, samples, logger) {
					// Nothing happening here
				}
			}
		then:
			assertThat(logger).hasLogged { event ->
				return event.message == '{} execution time: {}ns, average time: {}ns' &&
					event.arguments[0] == actionName &&
					event.arguments[1] instanceof Long &&
					event.arguments[2] ==~ /\d+\.\d{2}/
			}
	}
}
