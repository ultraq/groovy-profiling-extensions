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

/**
 * Extensions, often to the main {@code Object} class, for aiding with profiling
 * and performance testing.
 * 
 * @author Emanuel Rabina
 */
class ProfilingExtensions {

	private static final Logger logger = LoggerFactory.getLogger(ProfilingExtensions)

	/**
	 * Capture and log the time it takes to perform the given closure.
	 * 
	 * @param <T>
	 * @param actionName
	 * @param closure
	 * @return
	 */
	static <T> T time(Object self, String actionName, Closure<T> closure) {

		def start = System.currentTimeMillis()
		def result = closure()
		def finish = System.currentTimeMillis()
		logger.debug("${actionName} complete.  Execution time: ${finish - start}ms")
		return result
	}
}
