package de.kreth.clubhelperbackend.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class LoggerAspect {

	private Logger logger = LoggerFactory.getLogger(LoggerAspect.class);

	@Around("execution (* de.kreth.clubhelperbackend.dao.*.*(..))")
	public void logDao(ProceedingJoinPoint joinPoint) throws Throwable {
		joinPoint.proceed();

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
		logger.info(logMessage.toString());
	}
	
	@Around("execution (* de.kreth.clubhelperbackend.controller.*.*(..))")
	public void logController(ProceedingJoinPoint joinPoint) throws Throwable {
		joinPoint.proceed();

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
		logger.info(logMessage.toString());
	}
	
	@AfterThrowing(
	   pointcut = "execution(* de.kreth.clubhelperbackend.*.*(..))",
	   throwing= "e")
	public void logExceptions(JoinPoint p, Throwable e) {

		StringBuffer logMessage = new StringBuffer();
		logMessage.append(p.getTarget().getClass().getName());
		logMessage.append(".");
		logMessage.append(p.getSignature().getName());
		logMessage.append("(");
		// append args
		Object[] args = p.getArgs();
		for (int i = 0; i < args.length; i++) {
			logMessage.append(args[i]).append(",");
		}
		if (args.length > 0) {
			logMessage.deleteCharAt(logMessage.length() - 1);
		}

		logMessage.append(")");
		logger.error(logMessage.toString(), e);
	}
}
