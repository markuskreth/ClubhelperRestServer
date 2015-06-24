package de.kreth.clubhelperbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.kreth.clubhelperbackend.dao.Dao;
import de.kreth.clubhelperbackend.pojo.Person;

@Controller
@RequestMapping("/person")
public class PersonController extends AbstractController<Person>{

	@Autowired
	public PersonController(Dao<Person> personDao) {
		super(personDao, Person.class);
	}

	@Override
	protected String getBaseMapping() {
		return "/person";
	}

}
