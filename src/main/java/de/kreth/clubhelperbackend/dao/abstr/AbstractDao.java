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
import de.kreth.clubhelperbackend.utils.TimeProvider;

/**
 * Default implementation for database access with all common query methods.
 * 
 * @author markus
 *
 * @param <T> object type this dao is used for.
 */
public abstract class AbstractDao<T extends Data> extends JdbcDaoSupport implements Dao<T> {

	public static final String ID_COLUMN = "id";
	public static final String DELETE_COLUMN = "deleted";
	public static final String CREATED_COLUMN = "created";
	public static final String CHANGED_COLUMN = "changed";

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
	private final ClubhelperRowMapper<T> mapper;
	protected final Logger log;

	private DeletedEntriesDao deletedEntriesDao;
	private TransactionTemplate transactionTemplate;

	final String SQL_INSERTWithoutId;
	private final String[] columnNames;
	protected TimeProvider timeProvider;

	/**
	 * Constructs this {@link Dao} implemetation.
	 * 
	 * @param config configuration for this dao.
	 */
	public AbstractDao(DaoConfig<T> config) {
		super();
		log = LoggerFactory.getLogger(getClass());

		this.mapper = config.mapper;
		this.mapper.setLog(log);

		this.tableName = config.tableName;
		this.columnNames = config.columnNames;

		List<String> localColumList = new ArrayList<>(Arrays.asList(config.columnNames));

		localColumList.add(CHANGED_COLUMN);
		StringBuilder stringBuilder = new StringBuilder().append("update ").append(config.tableName).append(" set ")
				.append(join(localColumList, "=?, ")).append("=? WHERE id=?");
		this.SQL_UPDATE = stringBuilder.toString();

		localColumList.add(CREATED_COLUMN);
		SQL_INSERTWithoutId = "insert into " + config.tableName + " (" + join(localColumList, ", ") + ") values ("
				+ generateQuestionMarkList(config.columnNames.length + 2) + ")";

		localColumList.add(0, ID_COLUMN);
		this.SQL_INSERTWithId = "insert into " + config.tableName + " (" + join(localColumList, ", ") + ") values ("
				+ generateQuestionMarkList(config.columnNames.length + 3) + ")";

		this.SQL_DELETE = "update " + config.tableName + " set deleted=? where id=?";

		this.SQL_QUERY_ALL = "select * from " + config.tableName + " WHERE deleted is null";

		this.SQL_QUERY_ALL_WITHDELETED = "select * from " + config.tableName;

		this.SQL_QUERY_BY_ID = SQL_QUERY_ALL_WITHDELETED + " WHERE id=?";
		this.SQL_QUERY_CHANGED = SQL_QUERY_ALL_WITHDELETED + " WHERE changed>?";

		if (config.orderBy != null) {
			this.SQL_QUERY_ALL += " ORDER BY " + join(config.orderBy, ", ");
			this.SQL_QUERY_ALL_WITHDELETED += " ORDER BY " + join(config.orderBy, ", ");
		}
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

	@Autowired
	public void setTimeProvider(TimeProvider timeProvider) {
		this.timeProvider = timeProvider;
	}
	
	@SuppressWarnings("unchecked")
	public Class<T> forDataType() {
		return (Class<T>) mapper.itemClass;
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
	 * @param <Y> object type this dao config is for.
	 */
	public static class DaoConfig<Y extends Data> {
		private String tableName;
		private String[] columnNames;
		private ClubhelperRowMapper<Y> mapper;
		private String[] orderBy;

		/**
		 * Defines table structure for this dao
		 * 
		 * @param tableName   name of the database table
		 * @param columnNames column names without id and timestamps (added
		 *                    automatically)
		 * @param mapper      maps the object from ResultSet and do a value object.
		 * @param orderBy     column names for ordering list. null for no order clause
		 */
		public DaoConfig(String tableName, String[] columnNames, ClubhelperRowMapper<Y> mapper, String[] orderBy) {
			super();
			this.tableName = tableName;
			this.columnNames = columnNames;
			this.mapper = mapper;
			this.orderBy = orderBy;
		}

		public ClubhelperRowMapper<Y> getMapper() {
			return mapper;
		}
	}

	/**
	 * Class to map an Object to a value Collection.
	 * 
	 * @author markus
	 * @param <X> Obect type to be mapped
	 */
	public static class ClubhelperRowMapper<X extends Data> implements org.springframework.jdbc.core.RowMapper<X> {

		private Logger log;

		private Class<? extends X> itemClass;

		public ClubhelperRowMapper(Class<? extends X> itemClass) {
			this.itemClass = itemClass;
		}

		public void setLog(Logger log) {
			this.log = log;
		}

		@Override
		public final X mapRow(ResultSet rs, int rowNo) throws SQLException {

			try {
				return appendDefault(itemClass.getDeclaredConstructor().newInstance(), rs);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new SQLException("Unable to instanciate " + itemClass.getName(), e);
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
						JDBCType type = JDBCType.valueOf(meta.getColumnType(i + 1));

						switch (type) {
						case INTEGER:
							executeSetter(obj, columnName,
									Arrays.asList(Long.class, Integer.class, long.class, int.class),
									rs.getLong(columnName));
							break;

						case VARCHAR:
							executeSetter(obj, columnName, Arrays.asList(String.class), rs.getString(columnName));
							break;

						case DATE:
						case TIMESTAMP:
							executeSetter(obj, columnName, Arrays.asList(Date.class, java.sql.Date.class),
									rs.getTimestamp(columnName));
							break;

						default:
							break;
						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						log.error("Unable to set " + columnName + " with type " + typeName + " on " + obj, e);
					}

				}
			}

			return obj;
		}

		private void executeSetter(final X obj, final String columnName, final List<Class<?>> typeClasses, Object value)
				throws IllegalAccessException, InvocationTargetException {

			Predicate<Method> parameterTest = (Method m) -> m.getParameterCount() == 1
					&& typeClasses.contains(m.getParameterTypes()[0]);

			Optional<Method> methods = findMethod(obj, columnName, "set", parameterTest);
			if (methods.isPresent()) {
				methods.get().invoke(obj, value);
			} else {
				if (log.isWarnEnabled()) {
					log.warn("Unable to find setter for {} of type {} for {}", columnName, typeClasses, obj);
				}
			}
		}

		private Optional<Method> findMethod(final X obj, final String columnName, final String prefix,
				Predicate<Method> parameterTest) {
			final String strippedName = columnName.replace("_", "");

			return Arrays
					.asList(obj.getClass().getMethods()).stream().filter(m -> 
						m.getName().startsWith(prefix)
						&& parameterTest.test(m) 
						&& m.getName().substring(3).equalsIgnoreCase(strippedName))
					.findFirst();
		}

		/**
		 * Maps the given object to a value array.
		 * 
		 * @param obj Object to map Values from
		 * @return values of the object in correct order of the table columns.
		 */
		public Collection<Object> mapObject(X obj, String[] columnNames) {
			List<Object> result = new ArrayList<>();
			for (String columnName : columnNames) {
				Predicate<Method> parameterTest = (Method m) -> m.getParameterCount() == 0;
				Optional<Method> method = findMethod(obj, columnName, "get", parameterTest);
				if (method.isPresent()) {
					Method m = method.get();
					try {
						result.add(m.invoke(obj));
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new RuntimeException("Unable to execute getter " + m.getName() + " on " + obj, e);
					}
				} else {
					if (log.isWarnEnabled()) {
						log.warn("Unable to find getter for {} for {}", columnName, obj);
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
		return onDate.getTime();
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
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(SQL_QUERY_ALL_WITHDELETED);
		stringBuilder.append(" WHERE ");
		stringBuilder.append(where);
		stringBuilder.append(" AND deleted is null");
		return getJdbcTemplate().query(stringBuilder.toString(), mapper);
	}

	@Override
	public List<T> getChangedSince(Date changed) {
		Object[] args = { changed };
		return getJdbcTemplate().query(SQL_QUERY_CHANGED, args, mapper);
	}

	@Override
	public T insert(T obj) {
		Assert.notNull(transactionTemplate, "Transaction Template not set yet.");

		boolean withId = obj.getId() != null && obj.getId() >= 0;

		ArrayList<Object> values = new ArrayList<>(mapper.mapObject(obj, columnNames));

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
					if (status != null) {
						status.setRollbackOnly();
					}
					throw new IllegalStateException("insert could not successfully create the database entity");
				}

				return obj;
			}
		});
		return obj;
	}

	@Override
	public boolean update(T obj) {

		Collection<Object> values = mapper.mapObject(obj, columnNames);
		obj.setChanged(timeProvider.getNow());

		values.add(obj.getChanged());
		values.add(obj.getId());

		log.debug("sql={}; ValueSize={}; Values={}", SQL_UPDATE, values.size(), values);

		int updateCount = getJdbcTemplate().update(SQL_UPDATE, values.toArray());
		if (updateCount != 1) {
			log.warn("UpdateCount for {} was {}. Created: {}", obj, updateCount, obj.getCreated());
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

		Assert.notNull(deletedEntriesDao, "deletedEntriesDao was not initialized.");

		Date date = timeProvider.getNow();
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

		Assert.notNull(deletedEntriesDao, "deletedEntriesDao was not initalized.");
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
		if (withDeleted) {
			return getJdbcTemplate().query(SQL_QUERY_ALL_WITHDELETED, mapper);
		} else {
			return getJdbcTemplate().query(SQL_QUERY_ALL, mapper);
		}
	}
}