package de.kreth.clubhelperbackend.aspects;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

public class ControllerLoggerAspectTest {

	private Logger logger;
	private ControllerLoggerAspect aspect;
	private JoinPoint joinPoint;
	private Signature signature;
	private Object[] args;
	
	@Before
	public void setUp() throws Exception {
		logger = mock(Logger.class);
		when(logger.isDebugEnabled()).thenReturn(true);
		when(logger.isWarnEnabled()).thenReturn(true);
		when(logger.isInfoEnabled()).thenReturn(true);
		aspect = new TestLoggerClass(logger);
		signature = mock(Signature.class);
		when(signature.getName()).thenReturn("theMethod");
		joinPoint = mock(JoinPoint.class);
		when(joinPoint.getTarget()).thenReturn(this);
		when(joinPoint.getSignature()).thenReturn(signature);
		args = new Object[]{"String", 1L};
		when(joinPoint.getArgs()).thenReturn(args);
	}

	@Test
	public void test() throws Throwable {
		aspect.logDao(joinPoint);
		
	}

	protected class TestLoggerClass extends ControllerLoggerAspect {
		public TestLoggerClass(Logger logger) {
			this.logger = logger;
		}
	}
}
