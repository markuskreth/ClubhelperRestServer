package de.kreth.clubhelperbackend.controller.abstr;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface ClubController<T> {

	/**
	 * View: Returns the name of View and inserts the object with provided Id
	 * into the model
	 * <p>
	 * Mapping: /{id}
	 * 
	 * @param id
	 *            Id of desired Object
	 * @param m
	 *            Model to insert object into.
	 * @return Name of View
	 */
	// @RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public abstract String getAsView(@PathVariable("id") long id, @RequestParam boolean ajax, Model m);

	/**
	 * View: Returns the name of View and inserts the resulting list into the
	 * model
	 * <p>
	 * Mapping: /
	 * 
	 * @param m
	 *            Model to insert the result list into.
	 * @return Name of View
	 */
	// @RequestMapping(value = { "/", "" }, method = RequestMethod.GET)
	public abstract String getAllAsView(@RequestParam boolean ajax, Model m);

	/*
	 * ************* REST Methoden ************
	 */

	/**
	 * Rest: PUT - Change object (update)
	 * <p>
	 * Mapping: /{id}
	 * 
	 * @param id
	 * @param toUpdate
	 * @param m
	 */
	// @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces =
	// "application/json")
	// @ResponseBody
	T put(@PathVariable("id") long id, @RequestBody T toUpdate);

	/**
	 * Rest: POST - Create Object without Id.
	 * 
	 * @param toCreate
	 * @return
	 */
	// @RequestMapping(value = "/", method = RequestMethod.POST, produces =
	// "application/json")
	// @ResponseBody
	T post(@RequestBody T toCreate);

	/**
	 * Rest: POST - Create Object with or without Id.
	 * 
	 * @param id
	 *            -1 for new Id
	 * @param toCreate
	 *            Object to create.
	 * @return created object with updated id and dates.
	 */
	// @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces =
	// "application/json")
	// @ResponseBody
	T post(@PathVariable("id") long id, @RequestBody T toCreate);

	/**
	 * Rest: GET - return object
	 * <p>
	 * Mapping: /{id}
	 * 
	 * @param id
	 * @return
	 */
	// @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces =
	// "application/json")
	// @ResponseBody
	T getById(@PathVariable("id") long id);

	/**
	 * Rest: GET - return List of all objects
	 * <p>
	 * Mapping: /
	 * 
	 * @return
	 */
	// @RequestMapping(value = { "/", "" }, method = RequestMethod.GET, produces
	// = "application/json")
	// @ResponseBody
	List<T> getAll();

	/**
	 * Rest: GET - return List of object having the (parent object) id as
	 * property. <br />
	 * Which property is meant is implementation dependant. Most likely its
	 * personId
	 * <p>
	 * Mapping: /for/{id}
	 * 
	 * @param id
	 *            Id matching all objects property.
	 * @return List of object with certain property matching id.
	 */
	// @RequestMapping(value = "/for/{id}", method = RequestMethod.GET, produces
	// = "application/json")
	// @ResponseBody
	List<T> getByParentId(@PathVariable("id") long id);

	/**
	 * Rest: DELETE - deletes object with the Id.
	 * <p>
	 * Mapping: /{id}
	 * 
	 * @param id
	 *            Id of the object to delete.
	 * @return deleted object.
	 */
	ResponseEntity<T> delete(@PathVariable("id") long id);

	/**
	 * 
	 * @param changed
	 * @return
	 */
	// @RequestMapping(value = "/changed/{changed}", method = RequestMethod.GET,
	// produces = "application/json")
	// @ResponseBody
	List<T> getChangedSince(long changed);

}