package de.kreth.clubhelperbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.kreth.clubhelperbackend.dao.Dao;
import de.kreth.clubhelperbackend.pojo.Group;

@Controller
@RequestMapping("/group")
public class GroupController extends AbstractController<Group> {

	@Autowired()
	public GroupController(Dao<Group> groupDao) {
		super(groupDao, Group.class);
	}

}
