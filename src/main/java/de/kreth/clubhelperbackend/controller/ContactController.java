package de.kreth.clubhelperbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.kreth.clubhelperbackend.dao.Dao;
import de.kreth.clubhelperbackend.pojo.Contact;

@Controller
@RequestMapping("/contact")
public class ContactController extends AbstractController<Contact> {

	@Autowired
	public ContactController(Dao<Contact> contactDao) {
		super(contactDao, Contact.class);
	}

}
