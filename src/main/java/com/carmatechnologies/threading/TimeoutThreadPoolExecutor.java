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

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimeoutThreadPoolExecutor extends ThreadPoolExecutor {
	private final long timeout;
	private final TimeUnit timeoutUnit;

	private final ScheduledExecutorService timeoutExecutor = Executors.newSingleThreadScheduledExecutor();
	private final ConcurrentMap<Runnable, ScheduledFuture<?>> runningTasks = new ConcurrentHashMap<Runnable, ScheduledFuture<?>>();

	public TimeoutThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, long timeout,
			TimeUnit timeoutUnit) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		this.timeout = timeout;
		this.timeoutUnit = timeoutUnit;
	}

	public TimeoutThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
			ThreadFactory threadFactory, long timeout, TimeUnit timeoutUnit) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
		this.timeout = timeout;
		this.timeoutUnit = timeoutUnit;
	}

	public TimeoutThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
			RejectedExecutionHandler handler, long timeout, TimeUnit timeoutUnit) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
		this.timeout = timeout;
		this.timeoutUnit = timeoutUnit;
	}

	public TimeoutThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
			ThreadFactory threadFactory, RejectedExecutionHandler handler, long timeout, TimeUnit timeoutUnit) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
		this.timeout = timeout;
		this.timeoutUnit = timeoutUnit;
	}

	@Override
	public void shutdown() {
		timeoutExecutor.shutdown();
		super.shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		timeoutExecutor.shutdownNow();
		return super.shutdownNow();
	}

	@Override
	protected void beforeExecute(final Thread t, final Runnable r) {
		if (timeout > 0) {
			final ScheduledFuture<?> timeoutTask = timeoutExecutor.schedule(new TimeoutTask(t), timeout, timeoutUnit);
			runningTasks.put(r, timeoutTask);
		}
	}

	@Override
	protected void afterExecute(final Runnable r, final Throwable t) {
		final ScheduledFuture<?> timeoutTask = runningTasks.remove(r);
		if (timeoutTask != null) {
			timeoutTask.cancel(false);
		}
	}

	private class TimeoutTask implements Runnable {
		private final Thread thread;

		public TimeoutTask(final Thread thread) {
			this.thread = thread;
		}

		@Override
		public void run() {
			thread.interrupt();
		}
	}
}
