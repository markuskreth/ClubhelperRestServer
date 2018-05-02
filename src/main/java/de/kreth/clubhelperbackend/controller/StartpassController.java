package de.kreth.clubhelperbackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import de.kreth.clubhelperbackend.controller.abstr.AbstractController;
import de.kreth.clubhelperbackend.dao.StartpassDao;
import de.kreth.clubhelperbackend.dao.abstr.Dao;
import de.kreth.clubhelperbackend.pojo.Startpass;

@Controller
@RequestMapping("/startpass")
public class StartpassController extends AbstractController<Startpass> {

	@Autowired
	public StartpassController(Dao<Startpass> dao) {
		super(dao, Startpass.class);
	}

	@Override
//	@RequestMapping(value = "/for/{id}", method = RequestMethod.GET, produces = "application/json")
//	@ResponseBody
	public List<Startpass> getByParentId(@PathVariable("id") long id) {
		StartpassDao myDao = (StartpassDao) dao;
		return myDao.getForPersonId(id);
	}
}
