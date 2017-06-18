package de.kreth.clubhelperbackend.controller;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.kreth.clubhelperbackend.controller.abstr.AbstractController;
import de.kreth.clubhelperbackend.dao.abstr.Dao;
import de.kreth.clubhelperbackend.pojo.PersonGroup;

@Controller
@RequestMapping("/persongroup")
public class PersonGroupController extends AbstractController<PersonGroup> {

	final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired()
	public PersonGroupController(Dao<PersonGroup> personGroupDao) {
		super(personGroupDao, PersonGroup.class);
	}

	@Override
	public PersonGroup put(long id, PersonGroup toUpdate) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
		    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		    log.info(authentication + " is updating " + toUpdate + " and has authorities: " + authorities);
		}
		return super.put(id, toUpdate);
	}
	
	@Override
	public PersonGroup post(long id, PersonGroup toCreate) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
		    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		    log.info(authentication + " is updating " + toCreate + " and has authorities: " + authorities);
		}
		return super.post(id, toCreate);
	}
}
