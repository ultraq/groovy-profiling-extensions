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

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor

/**
 * A logging strategy that emits a log every time the given period, in seconds,
 * has elapsed.
 *
 * @author Emanuel Rabina
 */
@CompileStatic
@TupleConstructor(defaults = false)
class TimedLoggingStrategy implements LoggingStrategy {

	final float seconds
	private long lastLogTime = System.currentTimeMillis()

	@Override
	boolean shouldLog() {

		var now = System.currentTimeMillis()
		if ((now - lastLogTime) / 1000 >= seconds) {
			lastLogTime = now
			return true
		}
		return false
	}
}
