package de.kreth.clubhelperbackend.testutils;

import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.slf4j.Logger;

public class MockedLogger {

	public static Logger mock() {

		Logger logger = Mockito.mock(Logger.class);
		when(logger.isTraceEnabled()).thenReturn(true);
		when(logger.isDebugEnabled()).thenReturn(true);
		when(logger.isInfoEnabled()).thenReturn(true);
		when(logger.isWarnEnabled()).thenReturn(true);
		when(logger.isErrorEnabled()).thenReturn(true);
		return logger;
	}

}
