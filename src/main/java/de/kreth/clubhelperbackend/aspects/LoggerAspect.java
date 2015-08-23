package de.kreth.clubhelperbackend.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggerAspect extends AbstractLoggerAspect {

	private Logger logger = LoggerFactory.getLogger(LoggerAspect.class);

//	@Around("execution (public * de.kreth.clubhelperbackend.dao.*.*(..))")
//	public void logDao(ProceedingJoinPoint joinPoint) throws Throwable {
//		joinPoint.proceed();
//		logger.info(generateLogMessage(joinPoint).toString());
//	}

	@Pointcut("execution (public * de.kreth.clubhelperbackend.controller.PersonController.*(..))")
	private void invocation(){}

//	@Before("invocation()")
//	public void logDao(JoinPoint joinPoint) throws Throwable {
//		logger.info("logInv: " + generateLogMessage(joinPoint).toString());
//	}

	@AfterReturning(pointcut = "invocation()",returning= "result")
	public void logDao(JoinPoint joinPoint, Object result) throws Throwable {
		logger.warn(generateLogMessage(joinPoint).append(" ==> ").append(result).toString());
	}
	
//	@Around("execution (public * de.kreth.clubhelperbackend.controller.*.*(..))")
//	public void logController(ProceedingJoinPoint joinPoint) throws Throwable {
//		joinPoint.proceed();
//		logger.info("logCon: " + generateLogMessage(joinPoint).toString());
//	}
	
//	@AfterThrowing(
//	   pointcut = "execution(* de.kreth.clubhelperbackend.*.*(..))",
//	   throwing= "e")
//	public void logExceptions(JoinPoint joinPoint, Throwable e) {
//		logger.error(generateLogMessage(joinPoint).toString(), e);
//	}
}
