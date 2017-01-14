package de.kreth.clubhelperbackend.aspects;

import org.aspectj.lang.JoinPoint;

public class AbstractLoggerAspect {

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