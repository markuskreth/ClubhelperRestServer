package de.kreth.clubhelperbackend.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.kreth.clubhelperbackend.dao.Dao;
import de.kreth.clubhelperbackend.pojo.Data;

public class AbstractController<T extends Data> implements ClubController<T> {

	private final Logger logger;
	private Dao<T> dao;
	private Class<T> elementClass;

	public AbstractController(Dao<T> dao, Class<T> element) {
		super();
		this.dao = dao;
		this.elementClass = element;
		logger = LoggerFactory.getLogger(getClass());
	}

	@Override
	@RequestMapping(value = "/update", method = RequestMethod.GET)
    public String update(@RequestParam String toUpdate, Model m) {
    	T p = null;

    	logger.debug("update" + elementClass.getSimpleName() + ": " + toUpdate);
    	ObjectMapper mapper = new ObjectMapper();

		try {
			p = mapper.readValue(toUpdate, elementClass);
			p.setChanged(new Date());
			boolean update = dao.update(p);
			logger.info("Update " + (update?"erfolgreich":"nicht erfolgreich") + " erfolgreich: " + toUpdate);
		} catch (IOException e) {
			logger.error("updatePerson Error: " + toUpdate, e);
		}
		String output = null;
		try {
			output = mapper.writeValueAsString(p);
			m.addAttribute("output", output);			
		} catch (JsonProcessingException e) {
			logger.error("updatePerson Error: " + toUpdate, e);
		}
    	return "output";
    }
	
	@Override
	@RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(@RequestParam String toCreate, Model m){
    	T p = null;

    	ObjectMapper mapper = new ObjectMapper();
    	try {
    		Date now = new Date();
			p = mapper.readValue(toCreate, elementClass);
			p.setChanged(now);
			p.setCreated(now);
			p = dao.insert(p);
			logger.info("insert erfolgreich: " + toCreate);
		} catch (IOException e) {
			logger.error("create Error: " + toCreate, e);
			p=null;
		}
    	
		String output = null;
		try {
			output = mapper.writeValueAsString(p);
			m.addAttribute("output", output);
		} catch (JsonProcessingException e) {
			logger.error("create output Error: " + toCreate, e);
		}
    	return "output";
    }

	@Override
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public String delete(String toDelete, Model m) {
    	T p = null;

    	ObjectMapper mapper = new ObjectMapper();
    	try {
			p = mapper.readValue(toDelete, elementClass);
			boolean deleted = dao.delete(p);
			logger.info("delete " + (deleted?"erfolgreich":"nicht erfolgreich") + ": " + toDelete);
			m.addAttribute("output", "Person " + p + (deleted?"":"not")  + " deleted");		
		} catch (IOException e) {
			logger.error("create output Error: " + toDelete, e);
			StringWriter wr = new StringWriter();
			PrintWriter out = new PrintWriter(wr);
			e.printStackTrace(out);
			m.addAttribute("output", "Person " + toDelete + " not deleted" + wr.toString());
			p=null;
		}

    	return "output";
	}

	@Override
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	@ResponseBody
	public T getObject(@PathVariable("id") long id) {
		T obj = dao.getById(id);
		logger.debug("GET " + getClass().getSimpleName() + "." + id + ": " + obj);
		return obj;
	}

	@Override
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	public void updateObject(@PathVariable("id") long id, @RequestBody T toUpdate, Model m) {
		toUpdate.setChanged(new Date());
		dao.update(toUpdate);
		logger.debug("PUT " + getClass().getSimpleName() + "." + id + ": " + toUpdate);
		m.addAttribute(toUpdate);
	}

	@Override
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public void delete(@PathVariable("id") long id, Model m) {
		T obj = getObject(id);
		dao.delete(id);
		logger.debug("DELETE " + getClass().getSimpleName() + "." + id + ": " + obj);
		m.addAttribute(obj);
	}

}