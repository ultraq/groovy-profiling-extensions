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
import spock.lang.Specification

/**
 * Tests for the profiling extension methods.
 *
 * @author Emanuel Rabina
 */
class ProfilingExtensionsTests extends Specification {

	def 'average - Log the average of all executions after the given amount of time'() {
		given:
			var logger = Mock(Logger) {
				getName() >> 'AverageTimeLogger'
				isDebugEnabled() >> true
			}
		when:
			var start = System.currentTimeMillis()
			var result
			while (true) {
				result = average('Average: {}ms', 1f, logger) { ->
					return 'Hi!'
				}
				if ((System.currentTimeMillis() - start) / 1000 >= 1) {
					break
				}
				Thread.sleep(1000)
			}
		then:
			1 * logger.debug(Profiler.marker, 'Average: {}ms', _ as String)
			result == 'Hi!'
	}

	def "average - Logs nothing if the specified time hasn't elapsed"() {
		given:
			var logger = Mock(Logger) {
				getName() >> 'AverageTimeNotMetLogger'
				isDebugEnabled() >> true
			}
		when:
			var result = average('Average: {}ms', 1f, logger) { ->
				return 'Hi!'
			}
		then:
			0 * logger.debug(*_)
			result == 'Hi!'
	}

	def "average - Logs the average time after the specified number of executions"() {
		given:
			var logger = Mock(Logger) {
				getName() >> 'AverageSamplesLogger'
				isDebugEnabled() >> true
			}
		when:
			var result
			2.times { i ->
				result = average('Average: {}ms', 2, logger) { ->
					return 'Hi!'
				}
			}
		then:
			1 * logger.debug(Profiler.marker, 'Average: {}ms', _ as String)
			result == 'Hi!'
	}

	def "average - Logs nothing if the specified number of executions hasn't been reached"() {
		given:
			var logger = Mock(Logger) {
				getName() >> 'AverageSamplesNotMetLogger'
				isDebugEnabled() >> true
			}
		when:
			var result = average('Average: {}ms', 2, logger) { ->
				return 'Hi!'
			}
		then:
			0 * logger.debug(*_)
			result == 'Hi!'
	}

	def "average - Logs nothing if debug is not enabled on the logger"() {
		given:
			var logger = Mock(Logger) {
				getName() >> 'AverageNotEnabledLogger'
			}
		when:
			var result = average('Average: {}ms', 1f, logger) { ->
				return 'Hi!'
			}
		then:
			0 * logger.debug(*_)
			result == 'Hi!'
	}

	def "time - Return the time it takes to execute an acion"() {
		when:
			var result = time() { ->
				Thread.sleep(100)
			}
		then:
			result > 0
	}

	def "time - Log the time taken to execute an action"() {
		given:
			var logger = Mock(Logger) {
				getName() >> 'TimeLogger'
				isDebugEnabled() >> true
			}
		when:
			var result = time('Time: {}ms', logger) { ->
				return 'Hi!'
			}
		then:
			1 * logger.debug(Profiler.marker, 'Time: {}ms', _ as Number)
			result == 'Hi!'
	}

	def "time - Logs nothing if debug is not enabled on the logger"() {
		given:
			var logger = Mock(Logger) {
				getName() >> 'TimeNotEnabledLogger'
			}
		when:
			var result = time('Time: {}ms', logger) { ->
				return 'Hi!'
			}
		then:
			0 * logger.debug(*_)
			result == 'Hi!'
	}
}
