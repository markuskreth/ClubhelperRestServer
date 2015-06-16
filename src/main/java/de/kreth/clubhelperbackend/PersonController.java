package de.kreth.clubhelperbackend;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.kreth.clubhelperbackend.pojo.Person;

/**
 * 
 * @author markus
 */
@Controller
public class PersonController {

	private Dao<Person> dao;

	@Autowired
	public PersonController(Dao<Person> dao) {
		super();
		this.dao = dao;
	}
    
	@RequestMapping(value = "/updateperson", method = RequestMethod.GET)
    public String updatePerson(@RequestParam String toUpdate, Model m) {
    	Person p = null;

    	ObjectMapper mapper = new ObjectMapper();

		try {
			p = mapper.readValue(toUpdate, Person.class);
			p.setChanged(new Date());
			dao.update(p);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		String output = null;
		try {
			output = mapper.writeValueAsString(p);
			m.addAttribute("output", output);			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    	return "output";
    }

    @RequestMapping(value = "/createperson", method = RequestMethod.GET)
    public String createPerson(@RequestParam String toCreate, Model m){
    	Person p = null;

    	ObjectMapper mapper = new ObjectMapper();
    	try {
			p = mapper.readValue(toCreate, Person.class);
			p = dao.insert(p);
		} catch (IOException e) {
			e.printStackTrace();
			p=null;
		}
    	
		String output = null;
		try {
			output = mapper.writeValueAsString(p);
			m.addAttribute("output", output);			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    	return "output";
    }

}
