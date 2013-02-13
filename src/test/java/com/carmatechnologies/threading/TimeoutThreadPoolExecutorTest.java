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

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class TimeoutThreadPoolExecutorTest {
	/**
	 * Main thread is: main
	 * Job #1 runs on: pool-1-thread-1
	 * Job #1 interrupted! Clean-up done on: pool-1-thread-1
	 * Job #2 runs on: pool-1-thread-1
	 */
	@Test
	public void taskSentOnTimeoutThreadPoolShouldBeAutomaticallyCancelledAfterTimeout() {
		final AtomicInteger counter = new AtomicInteger(0);
		ExecutorService threadPool = AdvancedExecutors.newFixedTimeoutThreadPool(1, 5, TimeUnit.SECONDS);
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("Job #1 runs on: " + Thread.currentThread().getName());
					counter.incrementAndGet();
					Thread.sleep(10000);
					counter.incrementAndGet();
				} catch (InterruptedException e) {
					System.out.println("Job #1 interrupted! Clean-up done on: " + Thread.currentThread().getName());
					counter.decrementAndGet();
				}
			}
		});

		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("Job #2 runs on: " + Thread.currentThread().getName());
					Thread.sleep(1000);
					counter.incrementAndGet();
				} catch (InterruptedException e) {
					System.out.println("Job #2 interrupted! Clean-up done on: " + Thread.currentThread().getName());
					counter.decrementAndGet();
				}
			}
		});

		try {
			System.out.println("Main thread is: " + Thread.currentThread().getName());
			threadPool.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertEquals(1, counter.get());
	}
}
