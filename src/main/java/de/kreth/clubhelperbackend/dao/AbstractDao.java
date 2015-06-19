package de.kreth.clubhelperbackend.dao;

import java.util.List;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

import de.kreth.clubhelperbackend.SqlForDialect;
import de.kreth.clubhelperbackend.pojo.Data;

public abstract class AbstractDao<T extends Data> extends JdbcDaoSupport {

	private SqlForDialect sqlDialect;
	private Class<T> classOfElements;

	public AbstractDao(Class<T> classOfElements) {
		super();
		this.classOfElements = classOfElements;
	}

	public SqlForDialect getSqlDialect() {
		return sqlDialect;
	}

	public void setSqlDialect(SqlForDialect sqlDialect) {
		this.sqlDialect = sqlDialect;
	}

	protected T getById(String SQL_QUERY_BY_ID, long id) {
		return getJdbcTemplate().queryForObject(SQL_QUERY_BY_ID, classOfElements, id);
	}

	protected T insert(T obj, String SQL_INSERT) {
		int inserted = getJdbcTemplate().update(SQL_INSERT, getInsertValues(obj));
		if (inserted == 1) {
			obj.setId(sqlDialect.queryForIdentity());
		} else
			obj = null;

		return obj;
	}

	protected boolean update(T obj, String SQL_UPDATE) {
		int inserted = getJdbcTemplate().update(SQL_UPDATE, getUpdateValues(obj));
		return inserted == 1;
	}

	protected boolean delete(T obj, String SQL_DELETE) {
		int inserted = getJdbcTemplate().update(SQL_DELETE, obj.getId());
		return inserted == 1;
	}

	protected List<T> getAll(String SQL_QUERY_ALL) {
		return getJdbcTemplate().queryForList(SQL_QUERY_ALL,
				classOfElements);
	}
	
	protected abstract Object[] getInsertValues(T obj);
	protected abstract Object[] getUpdateValues(T obj);

}