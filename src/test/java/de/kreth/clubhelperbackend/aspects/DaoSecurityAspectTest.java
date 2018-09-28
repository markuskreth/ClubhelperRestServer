package de.kreth.clubhelperbackend.aspects;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import de.kreth.clubhelperbackend.config.Encryptor;

public class DaoSecurityAspectTest {

	private final Encryptor enc = new Encryptor();
	private DaoSecurityAspect securityAspect;
	private JoinPoint joinPoint;
	private ServletRequestAttributes attributes;
	private HttpServletRequest request;

	@Before
	public void initAspect() {
		securityAspect = new DaoSecurityAspect();
		joinPoint = mock(JoinPoint.class);
		request = mock(HttpServletRequest.class);

		attributes = new ServletRequestAttributes(request);
		
		RequestContextHolder.setRequestAttributes(attributes);
		
	}

	@Test(expected=HttpClientErrorException.class)
	public void testLogDao2Missing() throws Throwable {

		when(request.getHeader("localtime")).thenReturn("12345345235");
		when(request.getHeader("token")).thenReturn("token");
		securityAspect.logDao2(joinPoint);
	}

	@Test(expected=SecurityException.class)
	public void testLogDao2Reject() throws Throwable {

		when(request.getHeader("localtime")).thenReturn("12345345235");
		when(request.getHeader("user-agent")).thenReturn("user-agent");
		when(request.getHeader("token")).thenReturn("token");
		securityAspect.logDao2(joinPoint);
	}

	@Test
	public void testLogDaoAccept() throws Throwable {

		when(request.getHeader("localtime")).thenReturn("12345345235");
		when(request.getHeader("user-agent")).thenReturn("user-agent");
		when(request.getHeader("token")).thenReturn(enc.encrypt(new Date(12345345235L), "user-agent"));
		securityAspect.logDao2(joinPoint);
	}
}
