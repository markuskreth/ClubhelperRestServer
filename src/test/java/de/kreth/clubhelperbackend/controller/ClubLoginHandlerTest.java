package de.kreth.clubhelperbackend.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

public class ClubLoginHandlerTest {

	private ClubLoginHandler handler;
	
	@Mock
	public RedirectStrategy redirectStrategy;

	@Mock
	public HttpServletRequest request;

	@Mock
	public HttpServletResponse response;

	@Mock
	public Authentication authentication;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		handler = new ClubLoginHandler(redirectStrategy);
	}

	@Test
	public void initDefaultConstructor() {
		handler = new ClubLoginHandler();
		assertNotNull(handler.redirectStrategy);
		assertEquals(DefaultRedirectStrategy.class, handler.redirectStrategy.getClass());
	}

	@Test
	public void testOnAuthenticationSuccessHttpServletRequestHttpServletResponseAuthentication() throws IOException, ServletException {

		when(response.isCommitted()).thenReturn(false);
		handler.onAuthenticationSuccess(request, response, authentication);
		verify(redirectStrategy).sendRedirect(request, response, "/");
	}

	@Test
	public void dontRepeat() throws IOException, ServletException {

		when(response.isCommitted()).thenReturn(true);
		handler.onAuthenticationSuccess(request, response, authentication);
		verify(redirectStrategy, never()).sendRedirect(any(), any(), anyString());
	}

}
