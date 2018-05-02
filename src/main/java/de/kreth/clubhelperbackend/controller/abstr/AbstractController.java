package de.kreth.clubhelperbackend.controller.abstr;

import static java.time.temporal.ChronoUnit.MINUTES;
import java.util.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.kreth.clubhelperbackend.dao.abstr.Dao;
import de.kreth.clubhelperbackend.pojo.Data;

/**
 * Default Controller implementing all functionality for all {@link Data} types.
 * 
 * @param <T>
 *            Data type
 */
public abstract class AbstractController<T extends Data>
		implements
			ClubController<T> {

	protected Dao<T> dao;
	private Class<T> elementClass;

	public AbstractController(Dao<T> dao, Class<T> element) {
		super();
		this.dao = dao;
		this.elementClass = element;
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'STAFF')")
	public String getAsView(@PathVariable("id") long id,
			@RequestParam(required = false) boolean ajax, Device device,
			Model m) {
		String mapping = elementClass.getSimpleName();
		m.addAttribute(mapping, getById(id));
		return mapping + "Get" + (ajax ? "Ajax" : "");
	}

	@Override
	@RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'STAFF')")
	public String getAllAsView(@RequestParam(required = false) boolean ajax,
			Device device, Model m) {
		String mapping = elementClass.getSimpleName();
		m.addAttribute(mapping + "List", getAll());
		return mapping + "All" + (ajax ? "Ajax" : "");
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public T getById(@PathVariable("id") long id) {
		T obj = dao.getById(id);
		return obj;
	}

	@Override
	@RequestMapping(value = {"/",
			""}, method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<T> getAll() {
		return dao.getAll();
	}

	@Override
	@RequestMapping(value = "/for/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<T> getByParentId(@PathVariable("id") long id) {
		return dao.getByWhere("person_id=" + id);
	}

	@Override
	@RequestMapping(value = "/changed/{changed}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<T> getChangedSince(@PathVariable("changed") long changed) {
		return dao.getChangedSince(new Date(changed));
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@ResponseBody
	public T put(@PathVariable("id") long id, @RequestBody T toUpdate) {

		Date now = new Date();
		Date created = toUpdate.getCreated();
		Date changed = null;

		if (toUpdate.getChanged() != null) {
			changed = toUpdate.getChanged();
			long minutes = MINUTES.between(created.toInstant(), changed.toInstant());
			if (minutes < 1) {
				toUpdate.setChanged(now);
			}
		} else {
			toUpdate.setChanged(now);
		}

		dao.update(id, toUpdate);
		return toUpdate;
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<T> delete(@PathVariable("id") long id) {
		T byId = getById(id);
		if (byId.isDeleted() == false) {
			dao.delete(id);
		}
		return ResponseEntity.ok(getById(id));
	}

	@Override
	@RequestMapping(value = "/", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public T post(@RequestBody T toCreate) {
		return post(-1L, toCreate);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public T post(@PathVariable("id") Long id, @RequestBody T toCreate) {
		if (id == null) {
			id = -1L;
		}
		toCreate.setId(id);
		Date now = new Date();

		toCreate.setChanged(now);

		if (toCreate.getCreated() == null
				|| toCreate.getCreated().getTime() == 0) {
			toCreate.setCreated(now);
		}

		if (toCreate.getId() < 0) {
			return dao.insert(toCreate);
		} else {
			if (getById(toCreate.getId()) != null) {
				dao.undelete(toCreate.getId());
				return toCreate;
			} else {
				return dao.insert(toCreate);
			}
		}
	}

}