package de.kreth.clubhelperbackend.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class LoggerAspect extends AbstractLoggerAspect {

	private Logger logger = LoggerFactory.getLogger(LoggerAspect.class);

	@Around("execution (* de.kreth.clubhelperbackend.dao.*.*(..))")
	public void logDao(ProceedingJoinPoint joinPoint) throws Throwable {
		joinPoint.proceed();
		logger.info(generateLogMessage(joinPoint).toString());
	}
	
	@Around("execution (* de.kreth.clubhelperbackend.controller.*.*(..))")
	public void logController(ProceedingJoinPoint joinPoint) throws Throwable {
		joinPoint.proceed();
		logger.info(generateLogMessage(joinPoint).toString());
	}
	
	@AfterThrowing(
	   pointcut = "execution(* de.kreth.clubhelperbackend.*.*(..))",
	   throwing= "e")
	public void logExceptions(JoinPoint joinPoint, Throwable e) {
		logger.error(generateLogMessage(joinPoint).toString(), e);
	}
}
