package de.kreth.clubhelperbackend.dao;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import de.kreth.clubhelperbackend.SqlForDialect;
import de.kreth.clubhelperbackend.pojo.Data;

public abstract class AbstractDao<T extends Data> extends JdbcDaoSupport implements Dao<T> {

	private SqlForDialect sqlDialect;
	private String SQL_QUERY_BY_ID;
	private String SQL_UPDATE;
	private String SQL_DELETE;
	private String SQL_QUERY_ALL;
	private String SQL_INSERT;

	public AbstractDao(
			String SQL_QUERY_BY_ID,
			String SQL_INSERT,
			String SQL_UPDATE,
			String SQL_DELETE,
			String SQL_QUERY_ALL) {
		super();
		this.SQL_QUERY_BY_ID = SQL_QUERY_BY_ID;
		this.SQL_INSERT = SQL_INSERT;
		this.SQL_UPDATE = SQL_UPDATE;
		this.SQL_DELETE = SQL_DELETE;
		this.SQL_QUERY_ALL = SQL_QUERY_ALL;
	}

	public SqlForDialect getSqlDialect() {
		return sqlDialect;
	}

	@Autowired
	public void setSqlDialect(SqlForDialect sqlDialect) {
		this.sqlDialect = sqlDialect;
	}

	@Autowired
	private DataSource source;
	
	@PostConstruct
	private void initialize() {
		setDataSource(source);
	}
	
	public T getById(long id) {
		return getJdbcTemplate().queryForObject(SQL_QUERY_BY_ID, getRowMapper(), id);
	}

	public T insert(T obj) {
		int inserted = getJdbcTemplate().update(SQL_INSERT, getInsertValues(obj));
		if (inserted == 1) {
			obj.setId(sqlDialect.queryForIdentity());
		} else
			obj = null;

		return obj;
	}

	public boolean update(T obj) {
		int inserted = getJdbcTemplate().update(SQL_UPDATE, getUpdateValues(obj));
		return inserted == 1;
	}

	public boolean update(long id, T obj) {
		int inserted = getJdbcTemplate().update(SQL_UPDATE, getUpdateValues(id, obj));
		return inserted == 1;
	}

	public boolean delete(T obj) {
		return delete(obj.getId());
	}

	public final boolean delete(long id) {
		int inserted = getJdbcTemplate().update(SQL_DELETE, id);
		return inserted == 1;
	}

	public List<T> getAll() {
		return getJdbcTemplate().query(SQL_QUERY_ALL, getRowMapper());
	}
	
	protected abstract RowMapper<T> getRowMapper();
	protected abstract Object[] getInsertValues(T obj);
	
	protected final Object[] getUpdateValues(T obj) {
		return getUpdateValues(obj.getId(), obj);
	}
	
	protected abstract Object[] getUpdateValues(long id, T obj);

}