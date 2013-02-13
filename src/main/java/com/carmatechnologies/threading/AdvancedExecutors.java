/*******************************************************************************
 * Copyright 2013 Marc CARRE <https://github.com/marccarre/commons-threading>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.carmatechnologies.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class AdvancedExecutors {
	public static ExecutorService newTimeoutThreadPool(final long timeout, final TimeUnit timeoutUnit) {
		return new TimeoutThreadPoolExecutor(0, Integer.MAX_VALUE, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), timeout, timeoutUnit);
	}

	public static ExecutorService newTimeoutThreadPool(final long timeout, final TimeUnit timeoutUnit, final ThreadFactory threadFactory) {
		return new TimeoutThreadPoolExecutor(0, Integer.MAX_VALUE, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory, timeout,
				timeoutUnit);
	}

	public static ExecutorService newFixedTimeoutThreadPool(final int nThreads, final long timeout, final TimeUnit timeoutUnit) {
		return new TimeoutThreadPoolExecutor(0, nThreads, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), timeout, timeoutUnit);
	}

	public static ExecutorService newFixedTimeoutThreadPool(final int nThreads, final long timeout, final TimeUnit timeoutUnit,
			final ThreadFactory threadFactory) {
		return new TimeoutThreadPoolExecutor(0, nThreads, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory, timeout, timeoutUnit);
	}
}
