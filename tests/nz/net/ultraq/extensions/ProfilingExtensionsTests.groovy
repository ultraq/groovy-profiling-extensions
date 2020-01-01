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

	def "Logs the time the closure took to execute"() {
		when:
			ProfilingExtensions.time(null, 'Test') {
				// Nothing happening here
			}
		then:
			_ * mockLogger.debug({ it ==~ /Test complete\.  Execution time: [\d]+ms\./ })
	}

	def "Logs the current and average time the closure took to execute"() {
		when:
			ProfilingExtensions.timeWithAverage(null, 'Test for average', 1) {
				// Nothing happening here
			}
		then:
			_ * mockLogger.debug({ it ==~ /Test for average complete\.  Execution time: [\d]+ms\.  Average time: [\d]+.\d\dms\./ })
	}
}
