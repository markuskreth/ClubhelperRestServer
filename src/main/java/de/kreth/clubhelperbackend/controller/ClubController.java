package de.kreth.clubhelperbackend.controller;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

public interface ClubController<T> {

	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
	public abstract String get(@PathVariable("id") long id, Model m);

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public abstract String getAll(Model m);
	
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public abstract String create(String toCreate, Model m);
	
//	@RequestMapping(value = "/update", method = RequestMethod.GET)
//	public abstract String update(String toUpdate, Model m);
//	
//	@RequestMapping(value = "/delete", method = RequestMethod.GET)
//	public abstract String delete(String toCreate, Model m);

	/* REST Methoden */

	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	void updateObject(@PathVariable("id") long id, T toUpdate, Model m);

	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	@ResponseBody
	T getObject(@PathVariable("id") long id);

	@RequestMapping(value="/", method=RequestMethod.GET)
	@ResponseBody
	List<T> getAll();
	
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	void delete(@PathVariable("id") long id, Model m);

}