package de.kreth.clubhelperbackend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class ClubLoginHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	public ClubLoginHandler() {
		super();
		setUseReferer(true);
	}

	// @Override
	// public void onAuthenticationSuccess(HttpServletRequest request,
	// HttpServletResponse response,
	// Authentication authentication) throws IOException, ServletException {
	// log.debug("Successfully authenticated on " + request);
	//
	// if(response.isCommitted()) {
	// return;
	// }
	//
	// redirectStrategy.sendRedirect(request, response, "/person");
	// log.trace("Redirected?" + request);
	//
	//
	//// response.sendRedirect("/clubhelperbackend/");
	// }

}
