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

	Logger logger

	def setup() {
		logger = Mock(Logger)
		logger.debugEnabled >> true
	}

	def "#average - Log the average of all executions after the given number of samples"() {
		given:
			def actionName = 'Test average'
			def samples = 2
			def profiledAction = {
				average(actionName, samples, logger) {
					// Nothing happening here
				}
			}
		when:
			samples.times {
				profiledAction()
			}
		then:
			1 * logger.debug('{} average time: {}ms.', actionName, { it ==~ /\d+\.\d{2}/ })
	}

	def "#averageNanos - Log the average of all executions after the given number of samples"() {
		given:
			def actionName = 'Test averageNanos'
			def samples = 2
			def profiledAction = {
				averageNanos(actionName, samples, logger) {
					// Nothing happening here
				}
			}
		when:
			samples.times {
				profiledAction()
			}
		then:
			1 * logger.debug('{} average time: {}ns.', actionName, { it ==~ /\d+\.\d{2}/ })
	}

	def "#time - Logs the time the closure took to execute, returning its result"() {
		given:
			def actionName = 'Test time'
			def profiledAction = {
				time(actionName, logger) {
					return 'Hi!'
				}
			}
		when:
			def result = profiledAction()
		then:
			1 * logger.debug('{} complete.  Execution time: {}ms.', actionName, _ as Long)
			result == 'Hi!'
	}

	def "#timeNanos - Logs the time the closure took to execute, returning its result"() {
		given:
			def actionName = 'Test timeNanos'
			def profiledAction = {
				timeNanos(actionName, logger) {
					return 'Hi!'
				}
			}
		when:
			def result = profiledAction()
		then:
			1 * logger.debug('{} complete.  Execution time: {}ns.', actionName, _ as Long)
			result == 'Hi!'
	}

	def "#timeWithAverage - Logs the current and average time the closure took to execute"() {
		given:
			def actionName = 'Test timeWithAverage'
			def samples = 2
			def profiledAction = {
				timeWithAverage(actionName, samples, logger) {
					// Nothing happening here
				}
			}
		when:
			samples.times {
				profiledAction()
			}
		then:
			2 * logger.debug('{} complete.  Execution time: {}ms.  Average time: {}ms.', actionName, _ as Long, { it ==~ /\d+\.\d{2}/ })
	}

	def "#timeWithAverageNanos - Logs the current and average time the closure took to execute"() {
		given:
			def actionName = 'Test timeWithAverageNanos'
			def samples = 2
			def profiledAction = {
				timeWithAverageNanos(actionName, samples, logger) {
					// Nothing happening here
				}
			}
		when:
			samples.times {
				profiledAction()
			}
		then:
			2 * logger.debug('{} complete.  Execution time: {}ns.  Average time: {}ns.', actionName, _ as Long, { it ==~ /\d+\.\d{2}/ })
	}
}
