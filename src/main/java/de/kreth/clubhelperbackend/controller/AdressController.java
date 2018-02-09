package de.kreth.clubhelperbackend.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.kreth.clubhelperbackend.controller.abstr.AbstractController;
import de.kreth.clubhelperbackend.dao.abstr.Dao;
import de.kreth.clubhelperbackend.pojo.Adress;

@Controller
@RequestMapping("/adress")
public class AdressController extends AbstractController<Adress> {

	@Autowired
	public AdressController(Dao<Adress> adressDao) {
		super(adressDao, Adress.class);
	}

	@Override
	public List<Adress> getByParentId(long id) {
		return Collections.emptyList();
	}

}
