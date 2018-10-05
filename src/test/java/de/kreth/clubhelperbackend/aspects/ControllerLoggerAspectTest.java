package de.kreth.clubhelperbackend.aspects;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Before;
import org.junit.Test;

import de.kreth.clubhelperbackend.pojo.Group;
import de.kreth.clubhelperbackend.testutils.MockedLogger;

public class ControllerLoggerAspectTest {

	private ControllerLoggerAspect logger;
	private JoinPoint joinPoint;

	@Before
	public void initLogger() {
		this.logger = new ControllerLoggerAspect();
		logger.logger = MockedLogger.mock();

		joinPoint = mock(JoinPoint.class);

		Signature sig = mock(Signature.class);
		when(sig.getName()).thenReturn("methodName");
		
		when(joinPoint.getTarget()).thenReturn(this);
		when(joinPoint.getSignature()).thenReturn(sig);
		when(joinPoint.getArgs()).thenReturn(new Object[] {"text", 1});
	}
	
	@Test
	public void testLogDao() throws Throwable {
		logger.logDao(joinPoint);
		verify(logger.logger, times(1)).info(getClass().getName() + ".methodName(text,1)");
	}

	@Test
	public void testLogCallJoinPointException() throws Throwable {
		UnsupportedOperationException t = new UnsupportedOperationException("Exception Message");
		logger.logCall(joinPoint, t);
		verify(logger.logger, times(1)).error(getClass().getName() + ".methodName(text,1)", t);
	}

	@Test
	public void testLogCallJoinPointObject() throws Throwable {
		Group g = new Group(12L, "GroupName", null, null);
		logger.logCall(joinPoint, g);
		verify(logger.logger, times(1)).debug(getClass().getName() + ".methodName(text,1) ==> " + g.toString());
	}

	@Test
	public void testLogDeleteSuccess() throws Throwable {
		Group g = new Group(12L, "GroupName", null, null);
		logger.logDeleteSuccess(joinPoint, g);
		verify(logger.logger, times(1)).warn(getClass().getName() + ".methodName(text,1) ==> " + g.toString());
	}

	@Test
	public void testLogDeleteInvocation() throws Throwable {
		logger.logDeleteInvocation(joinPoint);
		verify(logger.logger, times(1)).debug(anyString());
	}

}
