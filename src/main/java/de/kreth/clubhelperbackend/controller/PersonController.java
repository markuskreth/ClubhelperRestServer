package de.kreth.clubhelperbackend.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.kreth.clubhelperbackend.dao.Dao;
import de.kreth.clubhelperbackend.pojo.Person;

/**
 * 
 * @author markus
 */
@Controller
@RequestMapping("/person")
public class PersonController implements ClubController {

	private static final Logger logger = LoggerFactory.getLogger(PersonController.class);
	private Dao<Person> dao;

	@Autowired
	public PersonController(Dao<Person> dao) {
		super();
		this.dao = dao;
	}
    
	/* (non-Javadoc)
	 * @see de.kreth.clubhelperbackend.AbstractController#update(java.lang.String, org.springframework.ui.Model)
	 */
	@Override
	@RequestMapping(value = "/update", method = RequestMethod.GET)
    public String update(@RequestParam String toUpdate, Model m) {
    	Person p = null;

    	logger.debug("updatePerson: " + toUpdate);
    	ObjectMapper mapper = new ObjectMapper();

		try {
			p = mapper.readValue(toUpdate, Person.class);
			p.setChanged(new Date());
			dao.update(p);
			logger.info("Update erfolgreich: " + toUpdate);
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

    /* (non-Javadoc)
	 * @see de.kreth.clubhelperbackend.AbstractController#create(java.lang.String, org.springframework.ui.Model)
	 */
    @Override
	@RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(@RequestParam String toCreate, Model m){
    	Person p = null;

    	ObjectMapper mapper = new ObjectMapper();
    	try {
			p = mapper.readValue(toCreate, Person.class);
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
    	Person p = null;

    	ObjectMapper mapper = new ObjectMapper();
    	try {
			p = mapper.readValue(toDelete, Person.class);
			boolean deleted = dao.delete(p);
			logger.info("delete " + (deleted?"erfolgreich":"nicht erfolgreich") + ": " + toDelete);
			m.addAttribute("output", "Person " + p + " deleted");		
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

}
