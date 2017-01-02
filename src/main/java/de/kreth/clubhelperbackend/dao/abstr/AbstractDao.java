package de.kreth.clubhelperbackend.dao.abstr;

import static de.kreth.clubhelperbackend.string.String.join;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import de.kreth.clubhelperbackend.config.SqlForDialect;
import de.kreth.clubhelperbackend.dao.DeletedEntriesDao;
import de.kreth.clubhelperbackend.pojo.Data;
import de.kreth.clubhelperbackend.pojo.DeletedEntries;

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
	private String tableName;
	private DeletedEntriesDao deletedEntriesDao;
	private TransactionTemplate transactionTemplate;
	private Logger log;

	/**
	 * Constructs this {@link Dao} implemetation.
	 * 
	 * @param config
	 */
	public AbstractDao(DaoConfig<T> config) {
		super();
		log = LoggerFactory.getLogger(getClass());

		List<String> columnNames = new ArrayList<String>(Arrays.asList(config.columnNames));
		columnNames.add("changed");
		this.SQL_UPDATE = "update `" + config.tableName + "` set " + join("=?, ", columnNames) + "=? WHERE _id=?";

		columnNames.add("created");
		SQL_INSERTWithoutId = "insert into `" + config.tableName + "` (" + join(", ", columnNames) + ") values ("
				+ generateQuestionMarkList(config.columnNames.length + 2) + ")";

		columnNames.add(0, "_id");
		this.SQL_INSERTWithId = "insert into `" + config.tableName + "` (" + join(", ", columnNames) + ") values ("
				+ generateQuestionMarkList(config.columnNames.length + 3) + ")";

		this.SQL_DELETE = "update `" + config.tableName + "` set deleted=? where _id=?";

		this.SQL_QUERY_ALL = "select * from `" + config.tableName + "` WHERE deleted is null";
		this.SQL_QUERY_BY_ID = SQL_QUERY_ALL + " AND _id=?";
		this.SQL_QUERY_CHANGED = SQL_QUERY_ALL + " AND changed>?";
		this.mapper = config.mapper;
		this.tableName = config.tableName;
	}

	@Autowired
	public void setPlatformTransactionManager(PlatformTransactionManager transMan) {
		Assert.notNull(transMan, "The 'transactionManager' argument must not be null.");
		this.transactionTemplate = new TransactionTemplate(transMan);
	}

	@Autowired
	public void setDeletedEntriesDao(DeletedEntriesDao deletedEntriesDao) {
		this.deletedEntriesDao = deletedEntriesDao;
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
		return getJdbcTemplate().query(SQL_QUERY_ALL + " AND " + where, mapper);
	}

	@Override
	public List<T> getChangedSince(Date changed) {
		Object[] args = { changed };
		return getJdbcTemplate().query(SQL_QUERY_CHANGED, args, mapper);
	}

	@Override
	public T insert(T obj) {
		Assert.notNull(transactionTemplate);

		boolean withId = obj.getId() != null && obj.getId() >= 0;

		ArrayList<Object> values = new ArrayList<Object>(mapper.mapObject(obj));

		values.add(obj.getChanged());
		values.add(obj.getCreated());

		if (withId)
			values.add(0, obj.getId());

		Object[] valueArr = values.toArray();

		transactionTemplate.execute(new TransactionCallback<T>() {

			@Override
			public T doInTransaction(TransactionStatus status) {
				JdbcTemplate jdbcTemplate = getJdbcTemplate();

				String sql = withId ? SQL_INSERTWithId : SQL_INSERTWithoutId;

				int inserted = jdbcTemplate.update(sql, valueArr);

				if (inserted == 1) {
					if (!withId) {
						sql = sqlDialect.queryForIdentity(tableName);
						Long id = jdbcTemplate.queryForObject(sql, null, Long.class);
						obj.setId(id);
					}
				} else {
					status.setRollbackOnly();
					throw new IllegalStateException("insert could not successfully create the database entity");
				}

				return obj;
			}
		});
		return obj;
	}

	@Override
	public boolean update(T obj) {
		
		Collection<Object> values = mapper.mapObject(obj);
		
		values.add(obj.getChanged());
		values.add(obj.getId());

		log.debug("sql=" + SQL_UPDATE + "; ValueSize=" + values.size() + "; Values=" + values);
		
		int updateCount = getJdbcTemplate().update(SQL_UPDATE, values.toArray());
		if (updateCount != 1) {
			log.warn("UpdateCount for " + obj + " was " + updateCount + ". Created: " + obj.getCreated());
		}
		
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
	public boolean delete(long id) {

		Assert.notNull(deletedEntriesDao);

		Date date = new Date();
		int inserted = getJdbcTemplate().update(SQL_DELETE, date, id);
		if (inserted == 1) {
			DeletedEntries deleted = deletedEntriesDao.insert(new DeletedEntries(-1L, tableName, id, date, date));
			return deleted.getId() >= 0;
		} else {
			return false;
		}
	}

	@Override
	public boolean undelete(long id) {

		Assert.notNull(deletedEntriesDao);
		int updated = getJdbcTemplate().update(SQL_DELETE, null, id);

		if (updated == 1) {
			List<DeletedEntries> deleted = deletedEntriesDao.getByWhere("");
			if(deleted.size() == 1) {
				deletedEntriesDao.delete(deleted.get(0));
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public List<T> getAll() {
		return getJdbcTemplate().query(SQL_QUERY_ALL, mapper);
	}
}