package de.kreth.clubhelperbackend.dao;

import static de.kreth.clubhelperbackend.string.String.join;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import de.kreth.clubhelperbackend.config.SqlForDialect;
import de.kreth.clubhelperbackend.pojo.Data;

/**
 * Default implementation for database access with all common query methods.
 * 
 * @author markus
 *
 * @param <T>
 */
public abstract class AbstractDao<T extends Data> extends JdbcDaoSupport implements Dao<T> {

	private SqlForDialect sqlDialect;
	private String SQL_QUERY_BY_ID;
	private String SQL_QUERY_CHANGED;
	private String SQL_UPDATE;
	private String SQL_DELETE;
	private String SQL_QUERY_ALL;
	private String SQL_INSERTWithoutId;
	private String SQL_INSERTWithId;
	private RowMapper<T> mapper;

	/**
	 * Constructs this {@link Dao} implemetation.
	 * 
	 * @param config
	 */
	public AbstractDao(DaoConfig<T> config) {
		super();

		List<String> columnNames = new ArrayList<String>(Arrays.asList(config.columnNames));
		columnNames.add("changed");
		this.SQL_UPDATE = "update `" + config.tableName + "` set " + join("=?, ", columnNames) + "=? WHERE _id=?";

		columnNames.add("created");
		SQL_INSERTWithoutId = "insert into `" + config.tableName + "` (" + join(", ", columnNames) + ") values ("
				+ generateQuestionMarkList(config.columnNames.length + 2) + ")";

		columnNames.add(0, "_id");
		this.SQL_INSERTWithId = "insert into `" + config.tableName + "` (" + join(", ", columnNames) + ") values ("
				+ generateQuestionMarkList(config.columnNames.length + 3) + ")";

		this.SQL_DELETE = "delete from `" + config.tableName + "` where _id=?";

		this.SQL_QUERY_ALL = "select * from `" + config.tableName + "`";
		this.SQL_QUERY_BY_ID = SQL_QUERY_ALL + " where _id=?";
		this.SQL_QUERY_CHANGED = SQL_QUERY_ALL + " where changed>?";
		this.mapper = config.mapper;
	}

	private String generateQuestionMarkList(int length) {
		StringBuilder bld = new StringBuilder("?");
		for (int i = 1; i < length; i++)
			bld.append(",?");
		return length > 0 ? bld.toString() : "";
	}

	/**
	 * Configuration Class to construct a {@link Dao} implementation.
	 * 
	 * @author markus
	 *
	 * @param <Y>
	 */
	public static class DaoConfig<Y extends Data> {
		private String tableName;
		private String[] columnNames;
		private RowMapper<Y> mapper;

		/**
		 * Defines table structure for this dao
		 * 
		 * @param tableName
		 *            name of the database table
		 * @param columnNames
		 *            column names without id and timestamps (added
		 *            automatically)
		 * @param mapper
		 *            maps the object from ResultSet and do a value object.
		 */
		public DaoConfig(String tableName, String[] columnNames, RowMapper<Y> mapper) {
			super();
			this.tableName = tableName;
			this.columnNames = columnNames;
			this.mapper = mapper;
		}

	}

	/**
	 * Class to map an Object to a value Collection.
	 * 
	 * @author markus
	 * @param <X>
	 */
	public interface RowMapper<X extends Data> extends org.springframework.jdbc.core.RowMapper<X> {

		/**
		 * Maps the given object to a value array.
		 * 
		 * @param obj
		 *            Object to map Values from
		 * @return values of the object in correct order of the table columns.
		 */
		Collection<Object> mapObject(X obj);
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

	@Override
	public T getById(long id) {
		try {
			return getJdbcTemplate().queryForObject(SQL_QUERY_BY_ID, mapper, id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<T> getByWhere(String where) {
		return getJdbcTemplate().query(SQL_QUERY_ALL + " WHERE " + where, mapper);
	}

	@Override
	public List<T> getChangedSince(Date changed) {
		Object[] args = { changed };
		return getJdbcTemplate().query(SQL_QUERY_CHANGED, args, mapper);
	}

	@Override
	public T insert(T obj) {
		boolean withId = obj.getId() != null && obj.getId() >= 0;
		ArrayList<Object> values = new ArrayList<Object>(mapper.mapObject(obj));

		values.add(obj.getChanged());
		values.add(obj.getCreated());

		if (withId)
			values.add(0, obj.getId());

		String sql = withId ? SQL_INSERTWithId : SQL_INSERTWithoutId;
		Object[] valueArr = values.toArray();

		int inserted = getJdbcTemplate().update(sql, valueArr);

		if (inserted == 1) {
			if (!withId)
				obj.setId(sqlDialect.queryForIdentity());
		} else
			throw new IllegalStateException("insert could not successfully create the database entity");

		return obj;
	}

	@Override
	public boolean update(T obj) {
		Collection<Object> values = mapper.mapObject(obj);
		values.add(obj.getChanged());
		values.add(obj.getId());

		logger.debug("sql=" + SQL_UPDATE + "; ValueSize=" + values.size() + "; Values=" + values);
		int updateCount = getJdbcTemplate().update(SQL_UPDATE, values.toArray());
		return updateCount == 1;
	}

	@Override
	public boolean update(long id, T obj) {
		obj.setId(id);
		return update(obj);
	}

	@Override
	public boolean delete(T obj) {
		return delete(obj.getId());
	}

	@Override
	public final boolean delete(long id) {
		int inserted = getJdbcTemplate().update(SQL_DELETE, id);
		return inserted == 1;
	}

	@Override
	public List<T> getAll() {
		return getJdbcTemplate().query(SQL_QUERY_ALL, mapper);
	}
}