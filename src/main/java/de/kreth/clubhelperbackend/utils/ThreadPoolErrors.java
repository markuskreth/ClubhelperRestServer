package de.kreth.clubhelperbackend.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolErrors extends ThreadPoolExecutor {

	public final List<Throwable> exceptions = Collections
			.synchronizedList(new ArrayList<>());

	public ThreadPoolErrors(int threadCount) {
		super(Math.min(3, threadCount), // core threads
				threadCount, // max threads
				30, // timeout
				TimeUnit.SECONDS, // timeout units
				new LinkedBlockingQueue<Runnable>() // work queue
		);
	}

	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		if (t != null) {
			exceptions.add(t);
		}
	}

	public Throwable myAwaitTermination() {

		while (isRunningWithoutException()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				shutdownNow();
				return e;
			}
		}
		if (exceptions.isEmpty() == false) {
			return exceptions.get(0);
		}
		return null;
	}

	private boolean isRunningWithoutException() {
		return isTerminated() == false && getActiveCount() > 0
				&& exceptions.isEmpty();
	}

	public static void main(String[] args)
			throws InterruptedException, ExecutionException {
		ThreadPoolErrors threadPool = new ThreadPoolErrors(1);
		threadPool.execute(new Runnable() {
			public void run() {
				throw new RuntimeException("Ouch! Got an error.");
			}
		});
		threadPool.shutdown();
	}
}