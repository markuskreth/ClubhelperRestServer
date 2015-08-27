package de.kreth.clubhelperbackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.kreth.clubhelperbackend.dao.Dao;
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
		
		return null;
	}

}
