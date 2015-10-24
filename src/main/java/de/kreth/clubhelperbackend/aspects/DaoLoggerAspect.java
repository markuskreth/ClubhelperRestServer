package de.kreth.clubhelperbackend.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class DaoLoggerAspect extends AbstractLoggerAspect {

	private Logger logger = LoggerFactory.getLogger(DaoLoggerAspect.class);

	@Pointcut("execution (public * de.kreth.clubhelperbackend.dao..*(..))")
	private void invocation() {
	}

	@Before("invocation()")
	public void logDao(JoinPoint joinPoint) throws Throwable {
		logger.info(generateLogMessage(joinPoint).toString());
	}

	@AfterThrowing(pointcut = "invocation()", throwing = "ex")
	public void logDao(JoinPoint joinPoint, Exception ex) throws Throwable {
		logger.error(generateLogMessage(joinPoint).toString(), ex);
	}

	@AfterReturning(pointcut = "invocation()", returning = "result")
	public void logDao(JoinPoint joinPoint, Object result) throws Throwable {
		logger.warn(generateLogMessage(joinPoint).append(" ==> ")
				.append(result).toString());
	}
}
