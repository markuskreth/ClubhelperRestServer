package de.kreth.clubhelperbackend;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kreth.clubhelperbackend.dao.abstr.Dao;
import de.kreth.clubhelperbackend.pojo.Data;

public class StubDao<T extends Data> implements Dao<T> {

	/**
	 * Used to deliver Testobjects in {@link #getById(long)} and {@link #getAll()}
	 */
	public Map<Long, T> byId = new HashMap<Long, T>();
	public List<T> inserted = new ArrayList<T>();
	public List<T> updated = new ArrayList<T>();
	public List<Long> deleted = new ArrayList<Long>();
	public Long lastInsertId = 0L;
	public List<String> getByWhere = new ArrayList<String>();
	public List<Date> changedSince = new ArrayList<Date>();
	
	@Override
	public T getById(long id) {
		return byId.get(id);
	}

	@Override
	public List<T> getAll() {
		List<T> all = new ArrayList<T>(byId.values());
		return all;
	}

	@Override
	public T insert(T obj) {
		obj.setId(++lastInsertId);
		inserted.add(obj);
		return obj;
	}

	@Override
	public boolean update(T obj) {
		updated.add(obj);
		return true;
	}

	@Override
	public boolean delete(T obj) {
		deleted.add(obj.getId());
		return true;
	}

	@Override
	public boolean delete(long id) {
		deleted.add(id);
		return true;
	}

	@Override
	public boolean undelete(long id) {
		deleted.remove(id);
		return true;
	}
	
	@Override
	public boolean update(long id, T obj) {
		updated.add(obj);
		return true;
	}

	@Override
	public List<T> getByWhere(String where) {
		getByWhere.add(where);
		return new ArrayList<T>();
	}

	@Override
	public List<T> getChangedSince(Date changed) {
		changedSince.add(changed);
		return new ArrayList<T>();
	}

}
