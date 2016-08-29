package de.kreth.clubhelperbackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.kreth.clubhelperbackend.dao.Dao;
import de.kreth.clubhelperbackend.pojo.Relative;

@Controller
@RequestMapping("/relative")
public class RelativeController extends AbstractController<Relative> {

	@Autowired
	public RelativeController(Dao<Relative> relativeDao) {
		super(relativeDao, Relative.class);
	}

	@Override
	@RequestMapping(value = "/for/{id}", method = RequestMethod.GET)
	@ResponseBody
	public List<Relative> getByParentId(@PathVariable("id") long id) {
		StringBuilder whereClause = new StringBuilder("person1=");
		whereClause.append(id).append(" OR person2=").append(id);
		return dao.getByWhere(whereClause.toString());
	}

}
