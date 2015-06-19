package de.kreth.clubhelperbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.kreth.clubhelperbackend.dao.Dao;
import de.kreth.clubhelperbackend.pojo.Adress;

@Controller
@RequestMapping("/adress")
public class AdressController extends AbstractController<Adress> {

	@Autowired
	public AdressController(Dao<Adress> dao) {
		super(dao, Adress.class);
	}

}
