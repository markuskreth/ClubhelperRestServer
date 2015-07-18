package de.kreth.clubhelperbackend.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

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

public abstract class AbstractController<T extends Data> implements ClubController<T> {

//	private final Logger logger;
	protected Dao<T> dao;
	private Class<T> elementClass;

	public AbstractController(Dao<T> dao, Class<T> element) {
		super();
		this.dao = dao;
		this.elementClass = element;
//		logger = LoggerFactory.getLogger(getClass());
	}

	@Override
	@RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(@RequestParam String toCreate, Model m){
    	T p = null;

    	ObjectMapper mapper = new ObjectMapper();
    	try {
			p = mapper.readValue(toCreate, elementClass);
			if(p.getId() == null || p.getId()<0) {
	    		Date now = new Date();
				p.setChanged(now);
				p.setCreated(now);
			}
			p = dao.insert(p);
//			logger.info("insert erfolgreich: " + toCreate);
		} catch (IOException e) {
//			logger.error("create Error: " + toCreate, e);
			p=null;
		}
    	
		String output = null;
		try {
			output = mapper.writeValueAsString(p);
			m.addAttribute("output", output);
		} catch (JsonProcessingException e) {
//			logger.error("create output Error: " + toCreate, e);
		}
    	return "output";
    }

	@Override
	@RequestMapping(value="/get/{id}", method=RequestMethod.GET)
	public String get(@PathVariable("id") long id, Model m) {
		String mapping = elementClass.getSimpleName();
		m.addAttribute(mapping, getObject(id));
		return mapping + ".get";
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public String getAll(Model m) {
		String mapping = elementClass.getSimpleName();
		m.addAttribute(mapping + "List", getAll());
		return mapping + ".all";
	}
	
	@Override
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	@ResponseBody
	public T getObject(@PathVariable("id") long id) {
		T obj = dao.getById(id);
//		logger.debug("GET " + getClass().getSimpleName() + "." + id + ": " + obj);
		return obj;
	}

	@Override
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	public void updateObject(@PathVariable("id") long id, @RequestBody T toUpdate, Model m) {
		toUpdate.setChanged(new Date());
		dao.update(id, toUpdate);
//		logger.debug("PUT " + getClass().getSimpleName() + "." + id + ": " + toUpdate);
		m.addAttribute(toUpdate);
	}

	@Override
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public T delete(@PathVariable("id") long id) {
		T obj = getObject(id);
		dao.delete(id);
//		logger.debug("DELETE " + getClass().getSimpleName() + "." + id + ": " + obj);
		return obj;
	}

	@Override
	@RequestMapping(value={"/", ""}, method=RequestMethod.GET)
	@ResponseBody
	public List<T> getAll() {
		return dao.getAll();
	}

	@Override
	@RequestMapping(value="/for/{id}", method=RequestMethod.GET)
	@ResponseBody
	public List<T> getForId(@PathVariable("id") long id) {
		return dao.getByWhere("person_id=" + id);
	}

	@Override
	@RequestMapping(value="/changed/{changed}", method=RequestMethod.GET)
	@ResponseBody
	public List<T> getChangedSince(@PathVariable("changed") long changed) {
		return dao.getChangedSince(new Date(changed));
	}

}