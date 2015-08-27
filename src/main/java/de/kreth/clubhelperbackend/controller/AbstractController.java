package de.kreth.clubhelperbackend.controller;

import java.util.Date;
import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
	@RequestMapping(value="/", method=RequestMethod.POST)
	@ResponseBody
	public T post(@RequestBody T toCreate) {
		return post(-1, toCreate);
	}
	
	@Override
	@RequestMapping(value="/{id}", method=RequestMethod.POST)
	@ResponseBody
	public T post(@PathVariable("id") long id, @RequestBody T toCreate) {
		toCreate.setId(id);
		Date now = new Date();
		toCreate.setChanged(now);
		toCreate.setCreated(now);
		return dao.insert(toCreate);
	}
	
	@Override
	@RequestMapping(value="/get/{id}", method=RequestMethod.GET)
	public String getAsView(@PathVariable("id") long id, Model m) {
		String mapping = elementClass.getSimpleName();
		m.addAttribute(mapping, getById(id));
		return mapping + ".get";
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public String getAllAsView(Model m) {
		String mapping = elementClass.getSimpleName();
		m.addAttribute(mapping + "List", getAll());
		return mapping + ".all";
	}
	
	@Override
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	@ResponseBody
	public T getById(@PathVariable("id") long id) {
		T obj = dao.getById(id);
//		logger.debug("GET " + getClass().getSimpleName() + "." + id + ": " + obj);
		return obj;
	}

	@Override
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	@ResponseBody
	public T put(@PathVariable("id") long id, @RequestBody T toUpdate) {
		toUpdate.setChanged(new Date());
		dao.update(id, toUpdate);
//		logger.debug("PUT " + getClass().getSimpleName() + "." + id + ": " + toUpdate);
		return toUpdate;
	}

	@Override
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public T delete(@PathVariable("id") long id) {
		T obj = getById(id);
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
	public List<T> getByParentId(@PathVariable("id") long id) {
		return dao.getByWhere("person_id=" + id);
	}

	@Override
	@RequestMapping(value="/changed/{changed}", method=RequestMethod.GET)
	@ResponseBody
	public List<T> getChangedSince(@PathVariable("changed") long changed) {
		return dao.getChangedSince(new Date(changed));
	}

}