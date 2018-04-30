package de.kreth.clubhelperbackend.dao.abstr;

import static org.apache.commons.lang3.StringUtils.join;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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

import de.kreth.clubhelperbackend.config.DatabaseConfiguration;
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
 *            object type this dao is used for.
 */
public abstract class AbstractDao<T extends Data> extends JdbcDaoSupport
		implements
			Dao<T> {

	protected static final DatabaseConfiguration dbConfig = new DatabaseConfiguration(0);
	
	final String tableName;
	private SqlForDialect sqlDialect;

	protected String SQL_QUERY_ALL;
	protected String SQL_QUERY_ALL_WITHDELETED;

	private final String SQL_QUERY_BY_ID;
	private final String SQL_QUERY_CHANGED;
	private final String SQL_UPDATE;
	private final String SQL_DELETE;
	private final String SQL_INSERTWithId;
	private final RowMapper<T> mapper;
	protected final Logger log;

	private DeletedEntriesDao deletedEntriesDao;
	private TransactionTemplate transactionTemplate;

	final String SQL_INSERTWithoutId;
	private final String[] columnNames;

	/**
	 * Constructs this {@link Dao} implemetation.
	 * 
	 * @param config
	 *            configuration for this dao.
	 */
	public AbstractDao(DaoConfig<T> config) {
		super();
		log = LoggerFactory.getLogger(getClass());

		this.mapper = config.mapper;
		this.mapper.setLog(log);

		this.tableName = config.tableName;
		this.columnNames = config.columnNames;

		List<String> columnNames = new ArrayList<>(
				Arrays.asList(config.columnNames));

		columnNames.add("changed");
		StringBuilder stringBuilder = new StringBuilder().append("update ")
				.append(config.tableName).append(" set ")
				.append(join(columnNames, "=?, ")).append("=? WHERE id=?");
		this.SQL_UPDATE = stringBuilder.toString();

		columnNames.add("created");
		SQL_INSERTWithoutId = "insert into " + config.tableName + " ("
				+ join(columnNames, ", ") + ") values ("
				+ generateQuestionMarkList(config.columnNames.length + 2) + ")";

		columnNames.add(0, "id");
		this.SQL_INSERTWithId = "insert into " + config.tableName + " ("
				+ join(columnNames, ", ") + ") values ("
				+ generateQuestionMarkList(config.columnNames.length + 3) + ")";

		this.SQL_DELETE = "update " + config.tableName
				+ " set deleted=? where id=?";

		this.SQL_QUERY_ALL = "select * from " + config.tableName
				+ " WHERE deleted is null";

		this.SQL_QUERY_ALL_WITHDELETED = "select * from " + config.tableName;

		this.SQL_QUERY_BY_ID = SQL_QUERY_ALL_WITHDELETED + " WHERE id=?";
		this.SQL_QUERY_CHANGED = SQL_QUERY_ALL_WITHDELETED + " WHERE changed>?";

		if (config.orderBy != null) {
			this.SQL_QUERY_ALL += " ORDER BY " + join(config.orderBy, ", ");
			this.SQL_QUERY_ALL_WITHDELETED += " ORDER BY "
					+ join(config.orderBy, ", ");
		}
	}

	@Autowired
	public void setPlatformTransactionManager(
			PlatformTransactionManager transMan) {
		Assert.notNull(transMan,
				"The 'transactionManager' argument must not be null.");
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
	 *            object type this dao config is for.
	 */
	public static class DaoConfig<Y extends Data> {
		private String tableName;
		private String[] columnNames;
		private RowMapper<Y> mapper;
		private String[] orderBy;

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
		 * @param orderBy
		 *            column names for ordering list. null for no order clause
		 */
		public DaoConfig(String tableName, String[] columnNames,
				RowMapper<Y> mapper, String[] orderBy) {
			super();
			this.tableName = tableName;
			this.columnNames = columnNames;
			this.mapper = mapper;
			this.orderBy = orderBy;
		}

		public RowMapper<Y> getMapper() {
			return mapper;
		}
	}

	/**
	 * Class to map an Object to a value Collection.
	 * 
	 * @author markus
	 * @param <X>
	 *            Obect type to be mapped
	 */
	public static class RowMapper<X extends Data>
			implements
				org.springframework.jdbc.core.RowMapper<X> {

		private Logger log;

		private Class<? extends X> itemClass;

		public static String ID_COLUMN = "id";
		public static String DELETE_COLUMN = "deleted";
		public static String CREATED_COLUMN = "created";
		public static String CHANGED_COLUMN = "changed";

		public RowMapper(Class<? extends X> itemClass) {
			this.itemClass = itemClass;
		}

		public void setLog(Logger log) {
			this.log = log;
		}

		@Override
		public final X mapRow(ResultSet rs, int rowNo) throws SQLException {
			
			try {
				return appendDefault(itemClass.newInstance(), rs);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new SQLException(
						"Unable to instanciate " + itemClass.getName(), e);
			}

		}

		protected X appendDefault(X obj, ResultSet rs) throws SQLException {
			ResultSetMetaData meta = rs.getMetaData();

			for (int i = 0; i < meta.getColumnCount(); i++) {
				String columnName = meta.getColumnName(i + 1);
				if (DELETE_COLUMN.equalsIgnoreCase(columnName)) {
					rs.getTimestamp(DELETE_COLUMN);
					obj.setDeleted(!rs.wasNull());
				} else if (CREATED_COLUMN.equalsIgnoreCase(columnName)) {
					obj.setCreated(rs.getTimestamp(CREATED_COLUMN));
				} else if (CHANGED_COLUMN.equalsIgnoreCase(columnName)) {
					obj.setChanged(rs.getTimestamp(CHANGED_COLUMN));
				} else if (ID_COLUMN.equalsIgnoreCase(columnName)) {
					obj.setId(rs.getLong(ID_COLUMN));
				} else {
					String typeName = meta.getColumnTypeName(i + 1);
					try {
						JDBCType type = JDBCType
								.valueOf(meta.getColumnType(i + 1));

						switch (type) {
							case INTEGER :
								executeSetter(obj, columnName,
										Arrays.asList(Long.class, Integer.class,
												long.class, int.class),
										rs.getLong(columnName));
								break;

							case VARCHAR :
								executeSetter(obj, columnName,
										Arrays.asList(String.class),
										rs.getString(columnName));
								break;

							case DATE :
							case TIMESTAMP :
								executeSetter(obj, columnName,
										Arrays.asList(Date.class,
												java.sql.Date.class),
										rs.getTimestamp(columnName));
								break;

							default :
								break;
						}
					} catch (IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						log.error("Unable to set " + columnName + " with type "
								+ typeName + " on " + obj, e);
					}

				}
			}

			return obj;
		}

		private void executeSetter(final X obj, final String columnName,
				final List<Class<?>> typeClasses, Object value)
				throws IllegalAccessException, IllegalArgumentException,
				InvocationTargetException {

			Predicate<Method> parameterTest = (
					Method m) -> m.getParameterCount() == 1
							&& typeClasses.contains(m.getParameterTypes()[0]);

			Optional<Method> methods = findMethod(obj, columnName, "set",
					parameterTest);
			if (methods.isPresent()) {
				methods.get().invoke(obj, value);
			} else {
				if (log.isWarnEnabled()) {
					log.warn("Unable to find setter for " + columnName
							+ " of type " + typeClasses + " for " + obj);
				}
			}
		}

		private Optional<Method> findMethod(final X obj,
				final String columnName, final String prefix,
				Predicate<Method> parameterTest) {
			final String strippedName = columnName.replace("_", "");
			Optional<Method> methods = Arrays
					.asList(obj.getClass().getMethods()).stream().filter(m -> {
						return m.getName().startsWith(prefix)
								&& parameterTest.test(m)
								&& m.getName().substring(3)
										.equalsIgnoreCase(strippedName);
					}).findFirst();
			return methods;
		}
		/**
		 * Maps the given object to a value array.
		 * 
		 * @param obj
		 *            Object to map Values from
		 * @return values of the object in correct order of the table columns.
		 */
		public Collection<Object> mapObject(X obj, String[] columnNames) {
			List<Object> result = new ArrayList<>();
			for (String columnName : columnNames) {
				Predicate<Method> parameterTest = (
						Method m) -> m.getParameterCount() == 0;
				Optional<Method> method = findMethod(obj, columnName, "get",
						parameterTest);
				if (method.isPresent()) {
					Method m = method.get();
					try {
						result.add(m.invoke(obj));
					} catch (IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						throw new RuntimeException("Unable to execute getter "
								+ m.getName() + " on " + obj, e);
					}
				} else {
					if (log.isWarnEnabled()) {
						log.warn("Unable to find getter for " + columnName
								+ " for " + obj);
					}
				}
			}
			return result;
		}

	}

	public SqlForDialect getSqlDialect() {
		return sqlDialect;
	}

	@Autowired
	public void setSqlDialect(SqlForDialect sqlDialect) {
		this.sqlDialect = sqlDialect;
	}

	@Autowired
	private void initDatasource(DataSource ds) {
		super.setDataSource(ds);
	}

	public static Date normalizeDateToDay(Date date) {
		Calendar onDate = new GregorianCalendar();
		onDate.setTime(date);
		onDate.set(Calendar.MILLISECOND, 0);
		onDate.set(Calendar.SECOND, 0);
		onDate.set(Calendar.MINUTE, 0);
		onDate.set(Calendar.HOUR_OF_DAY, 0);
		Date time = onDate.getTime();
		return time;
	}

	@Override
	public T getById(long id) {
		try {
			T obj = getJdbcTemplate().queryForObject(SQL_QUERY_BY_ID, mapper,
					id);

			return obj;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<T> getByWhere(String where) {
		return getJdbcTemplate().query(SQL_QUERY_ALL_WITHDELETED + " WHERE "
				+ where + " AND deleted is null", mapper);
	}

	@Override
	public List<T> getChangedSince(Date changed) {
		Object[] args = {changed};
		return getJdbcTemplate().query(SQL_QUERY_CHANGED, args, mapper);
	}

	@Override
	public T insert(T obj) {
		Assert.notNull(transactionTemplate,
				"Transaction Template not set yet.");

		boolean withId = obj.getId() != null && obj.getId() >= 0;

		ArrayList<Object> values = new ArrayList<Object>(
				mapper.mapObject(obj, columnNames));

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
						Long id = jdbcTemplate.queryForObject(sql, null,
								Long.class);
						obj.setId(id);
					}
				} else {
					status.setRollbackOnly();
					throw new IllegalStateException(
							"insert could not successfully create the database entity");
				}

				return obj;
			}
		});
		return obj;
	}

	@Override
	public boolean update(T obj) {

		Collection<Object> values = mapper.mapObject(obj, columnNames);

		values.add(obj.getChanged());
		values.add(obj.getId());

		log.debug("sql=" + SQL_UPDATE + "; ValueSize=" + values.size()
				+ "; Values=" + values);

		int updateCount = getJdbcTemplate().update(SQL_UPDATE,
				values.toArray());
		if (updateCount != 1) {
			log.warn("UpdateCount for " + obj + " was " + updateCount
					+ ". Created: " + obj.getCreated());
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

		Assert.notNull(deletedEntriesDao,
				"deletedEntriesDao was not initialized.");

		Date date = new Date();
		int inserted = getJdbcTemplate().update(SQL_DELETE, date, id);
		if (inserted == 1) {
			DeletedEntries deleted = new DeletedEntries(-1L, tableName, id);
			deleted.setChanged(date);
			deleted.setCreated(date);
			deleted = deletedEntriesDao.insert(deleted);
			return deleted.getId() >= 0;
		} else {
			return false;
		}
	}

	@Override
	public boolean undelete(long id) {

		Assert.notNull(deletedEntriesDao,
				"deletedEntriesDao was not initalized.");
		int updated = getJdbcTemplate().update(SQL_DELETE, null, id);

		if (updated == 1) {
			List<DeletedEntries> deleted = deletedEntriesDao.getByWhere("");
			if (deleted.size() == 1) {
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
		return getAll(false);
	}

	public List<T> getAll(boolean withDeleted) {
		return getJdbcTemplate().query(SQL_QUERY_ALL, mapper);
	}
}