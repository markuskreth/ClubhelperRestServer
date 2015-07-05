package de.kreth.clubhelperbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.kreth.clubhelperbackend.dao.Dao;
import de.kreth.clubhelperbackend.pojo.Relative;

@Controller
@RequestMapping("/relative")
public class RelativeController extends AbstractController<Relative> {

	@Autowired
	public RelativeController(Dao<Relative> relativeDao) {
		super(relativeDao, Relative.class);
	}

}
