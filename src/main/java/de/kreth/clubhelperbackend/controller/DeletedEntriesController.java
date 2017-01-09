package de.kreth.clubhelperbackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.kreth.clubhelperbackend.controller.abstr.AbstractController;
import de.kreth.clubhelperbackend.dao.abstr.Dao;
import de.kreth.clubhelperbackend.pojo.DeletedEntries;

@Controller
@RequestMapping("/deletedentries")
public class DeletedEntriesController extends AbstractController<DeletedEntries> {

	@Autowired
	public DeletedEntriesController(Dao<DeletedEntries> dao) {
		super(dao, DeletedEntries.class);
	}

	@Override
	public List<DeletedEntries> getByParentId(long id) {
		return null;
	}
}
