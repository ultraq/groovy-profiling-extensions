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
import spock.lang.Specification

/**
 * Tests for the standalone profiler class.
 *
 * @author Emanuel Rabina
 */
class ProfilerTests extends Specification {

	def 'average - Log the average of all executions after the given amount of time'() {
		given:
			var logger = Mock(Logger) {
				isDebugEnabled() >> true
			}
			var profiler = new Profiler(logger, 'Average: {}ms', new TimedLoggingStrategy(1f))
		when:
			var start = System.currentTimeMillis()
			var result
			while (true) {
				result = profiler.average { ->
					return 'Hi!'
				}
				if ((System.currentTimeMillis() - start) / 1000 >= 1) {
					break
				}
				Thread.sleep(100)
			}
		then:
			1 * logger.debug(Profiler.marker, 'Average: {}ms', _ as String)
			result == 'Hi!'
	}

	def "average - Logs nothing if the specified time hasn't elapsed"() {
		given:
			var logger = Mock(Logger) {
				isDebugEnabled() >> true
			}
			var profiler = new Profiler(logger, 'Average: {}ms', new TimedLoggingStrategy(1f))
		when:
			var result = profiler.average { ->
				return 'Hi!'
			}
		then:
			0 * logger.debug(*_)
			result == 'Hi!'
	}

	def "average - Logs the average time after the specified number of executions"() {
		given:
			var logger = Mock(Logger) {
				isDebugEnabled() >> true
			}
			var profiler = new Profiler(logger, 'Average: {}ms', new SampleLoggingStrategy(2))
		when:
			var result
			2.times { i ->
				result = profiler.average { ->
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
				isDebugEnabled() >> true
			}
			var profiler = new Profiler(logger, 'Average: {}ms', new SampleLoggingStrategy(2))
		when:
			var result = profiler.average { ->
				return 'Hi!'
			}
		then:
			0 * logger.debug(*_)
			result == 'Hi!'
	}

	def "average - Logs nothing if debug is not enabled on the logger"() {
		given:
			var logger = Mock(Logger)
			var profiler = new Profiler(logger, 'Average: {}ms', new TimedLoggingStrategy(1f))
		when:
			var result = profiler.average { ->
				return 'Hi!'
			}
		then:
			0 * logger.debug(*_)
			result == 'Hi!'
	}

	def "time - Log the time taken to execute an action"() {
		given:
			var logger = Mock(Logger) {
				isDebugEnabled() >> true
			}
			var profiler = new Profiler(logger, 'Time: {}ms')
		when:
			var result = profiler.time { ->
				return 'Hi!'
			}
		then:
			1 * logger.debug(Profiler.marker, 'Time: {}ms', _ as Number)
			result == 'Hi!'
	}

	def "time - Logs nothing if debug is not enabled on the logger"() {
		given:
			var logger = Mock(Logger)
			var profiler = new Profiler(logger, 'Time: {}ms')
		when:
			var result = profiler.time { ->
				return 'Hi!'
			}
		then:
			0 * logger.debug(*_)
			result == 'Hi!'
	}
}
