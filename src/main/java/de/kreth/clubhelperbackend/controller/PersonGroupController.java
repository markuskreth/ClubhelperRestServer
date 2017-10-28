package de.kreth.clubhelperbackend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

}
