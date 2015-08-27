package de.kreth.clubhelperbackend.controller;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

public interface ClubController<T> {

	/**
	 * View: Returns the name of View and inserts the object with provided Id into the model 
	 * <p>Mapping: /get/{id}
	 * @param id	Id of desired Object
	 * @param m	Model to insert object into.
	 * @return	Name of View
	 */
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
	public abstract String getAsView(@PathVariable("id") long id, Model m);

	/**
	 * View: Returns the name of View and inserts the resulting list into the model 
	 * <p>Mapping: /all
	 * @param m Model to insert the result list into.
	 * @return	Name of View
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public abstract String getAllAsView(Model m);
	
	/* *************
	 * REST Methoden
	 * *************/

	/**
	 * Rest: PUT - Change object (update)
	 * <p>Mapping: /{id}
	 * @param id
	 * @param toUpdate
	 * @param m
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	@ResponseBody
	T put(@PathVariable("id") long id, @RequestBody T toUpdate);

	/**
	 *  Rest: POST - Create Object without Id.
	 * @param toCreate
	 * @return
	 */
	@RequestMapping(value="/", method=RequestMethod.POST)
	@ResponseBody
	T post(@RequestBody T toCreate);

	/**
	 * Rest: POST - Create Object with or without Id.
	 * 
	 * @param id -1 for new Id
	 * @param toCreate Object to create.
	 * @return	created object with updated id and dates.
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.POST)
	@ResponseBody
	T post(@PathVariable("id") long id, @RequestBody T toCreate);
	
	/**
	 * Rest: GET - return object
	 * <p>Mapping: /{id}
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	@ResponseBody
	T getById(@PathVariable("id") long id);

	/**
	 * Rest: GET - return List of all objects
	 * <p>Mapping: /
	 * @return
	 */
	@RequestMapping(value={"/", ""}, method=RequestMethod.GET)
	@ResponseBody
	List<T> getAll();

	/**
	 * Rest: GET - return List of object having the (parent object) id as property.
	 * <br />Which property is meant is implementation dependant. Most likely its personId
	 * <p>Mapping: /for/{id}
	 * @param id	Id matching all objects property.
	 * @return	List of object with certain property matching id.
	 */
	@RequestMapping(value="/for/{id}", method=RequestMethod.GET)
	@ResponseBody
	List<T> getByParentId(@PathVariable("id") long id);
	
	/**
	 * Rest: DELETE - deletes object with the Id.
	 * <p>Mapping: /{id}
	 * @param id	Id of the object to delete.
	 * @return deleted object.
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	@ResponseBody
	T delete(@PathVariable("id") long id);

	/**
	 * 
	 * @param changed
	 * @return
	 */
	@RequestMapping(value="/changed/{changed}", method=RequestMethod.GET)
	@ResponseBody
	List<T> getChangedSince(long changed);

}