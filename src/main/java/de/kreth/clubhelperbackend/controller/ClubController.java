package de.kreth.clubhelperbackend.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

public interface ClubController<T> {

	public abstract String update(String toUpdate, Model m);
	public abstract String create(String toCreate, Model m);
	public abstract String delete(String toCreate, Model m);

	/* REST Methoden */

	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	void updateObject(@PathVariable("id") long id, T toUpdate, Model m);

	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	@ResponseBody
	T getObject(@PathVariable("id") long id);
	
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	void delete(@PathVariable("id") long id, Model m);

}