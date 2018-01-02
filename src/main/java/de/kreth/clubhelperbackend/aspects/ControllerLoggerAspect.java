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
public class ControllerLoggerAspect extends AbstractLoggerAspect {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Pointcut("execution (public * de.kreth.clubhelperbackend.controller..*.delete(..))")
	private void deleteItem() {
	}

	@Pointcut("execution (public * de.kreth.clubhelperbackend.controller..*(..))")
	private void callAny() {
	}

	@Pointcut("callAny() && (!deleteItem())")
	private void invocation() {
	}

	@Before("invocation()")
	public void logDao(JoinPoint joinPoint) throws Throwable {
		logger.info(generateLogMessage(joinPoint).toString());
	}

	@AfterThrowing(pointcut = "invocation()", throwing = "ex")
	public void logCall(JoinPoint joinPoint, Exception ex) throws Throwable {
		logger.error(generateLogMessage(joinPoint).toString(), ex);
	}

	@AfterReturning(pointcut = "invocation()", returning = "result")
	public void logCall(JoinPoint joinPoint, Object result) throws Throwable {
		logger.debug(generateLogMessage(joinPoint).append(" ==> ")
				.append(result).toString());
	}

	@AfterReturning(pointcut = "deleteItem()", returning = "result")
	public void logDeleteSuccess(JoinPoint joinPoint, Object result)
			throws Throwable {
		logger.warn(generateLogMessage(joinPoint).append(" ==> ").append(result)
				.toString());
	}

	@Before("deleteItem()")
	public void logDeleteInvocation(JoinPoint joinPoint) throws Throwable {
		logger.debug(generateLogMessage(joinPoint).toString());
	}
}
