package de.kreth.clubhelperbackend.aspects;

import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractLoggerAspect {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	public enum LogLevel {
		WARN, DEBUG, INFO;

		boolean enabled(Logger logger) {
			switch (this) {
				case WARN :
					return logger.isWarnEnabled();
				case DEBUG :
					return logger.isDebugEnabled();
				case INFO :
					return logger.isInfoEnabled();

				default :
					return true;
			}
		}
	}

	protected void log(LogLevel level, JoinPoint joinPoint) {
		log(level, joinPoint, null);
	}

	protected void log(LogLevel level, JoinPoint joinPoint, Object result) {

		if (level.enabled(logger)) {

			StringBuffer msg = generateLogMessage(joinPoint);
			Exception exception = null;

			if (result != null) {
				if (result instanceof Exception) {
					exception = (Exception) result;
				} else {
					msg.append(" ==> ").append(result);
				}
			}

			switch (level) {
				case WARN :
					if (exception == null) {
						logger.warn(msg.toString());
					} else {
						logger.warn(msg.toString(), exception);
					}
					break;
				case DEBUG :
					if (exception == null) {
						logger.debug(msg.toString());
					} else {
						logger.debug(msg.toString(), exception);
					}
					break;
				case INFO :
					if (exception == null) {
						logger.info(msg.toString());
					} else {
						logger.info(msg.toString(), exception);
					}
					break;
				default :
					if (exception == null) {
						logger.trace(msg.toString());
					} else {
						logger.trace(msg.toString(), exception);
					}
			}
		}
	}

	protected StringBuffer generateLogMessage(JoinPoint joinPoint) {
		StringBuffer logMessage = new StringBuffer();
		logMessage.append(joinPoint.getTarget().getClass().getName());
		logMessage.append(".");
		logMessage.append(joinPoint.getSignature().getName());
		logMessage.append("(");
		// append args
		Object[] args = joinPoint.getArgs();
		for (int i = 0; i < args.length; i++) {
			logMessage.append(args[i]).append(",");
		}
		if (args.length > 0) {
			logMessage.deleteCharAt(logMessage.length() - 1);
		}

		logMessage.append(")");
		return logMessage;
	}

}