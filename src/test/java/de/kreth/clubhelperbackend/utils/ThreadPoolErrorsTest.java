package de.kreth.clubhelperbackend.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;

public class ThreadPoolErrorsTest {

	private ThreadPoolErrors exec;

	@Before
	public void setUp() throws Exception {
		exec = new ThreadPoolErrors(3);
	}

	@Test
	public void testCatchedErrors() {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				throw new NullPointerException("Test");
			}
		};

		exec.execute(r);
		Throwable throwable = exec.myAwaitTermination();
		assertNotNull(throwable);
		assertEquals("Test", throwable.getMessage());
	}

	@Test
	public void testNoExceptions() {
		final AtomicBoolean wasExecuted = new AtomicBoolean(false);
		exec.execute(new Runnable() {

			@Override
			public void run() {
				wasExecuted.set(true);
			}
		});
		Throwable myAwaitTermination = exec.myAwaitTermination();
		assertNull(myAwaitTermination);
		assertTrue(wasExecuted.get());
	}

}
