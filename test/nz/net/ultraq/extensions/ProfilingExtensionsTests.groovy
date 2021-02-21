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
import spock.lang.Specification

/**
 * Tests for the profiling extension methods.
 * 
 * @author Emanuel Rabina
 */
class ProfilingExtensionsTests extends Specification {

	def mockLogger
	def setup() {
		mockLogger = Mock(Logger)
		GroovyStub(LoggerFactory, global: true).getLogger(_) >> mockLogger
	}

	def "Log the average of all executions after the given number of samples"() {
		when:
			average('Test average', 1) {
				// Nothing happening here
			}
		then:
			_ * mockLogger.debug({ it ==~ /Test average average time: \d+\.\d{2}ms./ })
	}

	def "Log the average of all executions after the given number of samples - nanosecond precision"() {
		when:
			averageNanos('Test averageNanos', 1) {
				// Nothing happening here
			}
		then:
			_ * mockLogger.debug({ it ==~ /Test averageNanos average time: \d+\.\d{2}ns./ })
	}

	def "Logs the time the closure took to execute"() {
		when:
			time('Test time') {
				// Nothing happening here
			}
		then:
			_ * mockLogger.debug({ it ==~ /Test time complete\.  Execution time: \d+ms\./ })
	}

	def "Logs the time the closure took to execute - nanosecond precision"() {
		when:
			timeNanos('Test timeNanos') {
				// Nothing happening here
			}
		then:
			_ * mockLogger.debug({ it ==~ /Test timeNanos complete\.  Execution time: \d+ns\./ })
	}

	def "Logs the current and average time the closure took to execute"() {
		when:
			timeWithAverage('Test timeWithAverage', 1) {
				// Nothing happening here
			}
		then:
			_ * mockLogger.debug({ it ==~ /Test timeWithAverage complete\.  Execution time: \d+ms\.  Average time: \d+\.\d{2}ms\./ })
	}

	def "Logs the current and average time the closure took to execute - nanosecond precision"() {
		when:
			timeWithAverageNanos('Test timeWithAverageNanos', 1) {
				// Nothing happening here
			}
		then:
			_ * mockLogger.debug({ it ==~ /Test timeWithAverageNanos complete\.  Execution time: \d+ns\.  Average time: \d+.d{2}ns\./ })
	}
}
