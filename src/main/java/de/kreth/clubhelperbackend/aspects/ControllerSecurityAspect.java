package de.kreth.clubhelperbackend.aspects;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class ControllerSecurityAspect {

	private Encryptor encryptor = null;

	@Pointcut("execution (public * de.kreth.clubhelperbackend.dao..*(..))")
	private void invocation() {
	}

	@Before("invocation()")
	public void logDao2(JoinPoint joinPoint) throws Throwable {

		if (encryptor == null) {
			encryptor = new Encryptor();
		}

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();

		String time = request.getHeader("localtime");
		String userAgent = request.getHeader("user-agent");
		String token = request.getHeader("token");

		if (time == null || userAgent == null || token == null)
			throw new HttpClientErrorException(HttpStatus.EXPECTATION_FAILED,
					"Header expected: Some Header Values are missing.");

		Date remoteTime = new Date(Long.parseLong(time));
		String encrypted = encryptor.encrypt(remoteTime, userAgent);

		if (token.equals(encrypted)) {
			System.out.println("authenticated");
		} else {
			throw new SecurityException("Request not allowed!");
		}

	}

}
