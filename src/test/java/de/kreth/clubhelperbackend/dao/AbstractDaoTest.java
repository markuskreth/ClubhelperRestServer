package de.kreth.clubhelperbackend.dao;

import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.util.Date;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import de.kreth.clubhelperbackend.config.SqlForDialect;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao.ClubhelperRowMapper;
import de.kreth.clubhelperbackend.dao.abstr.DaoPackageMemberAccessor;
import de.kreth.clubhelperbackend.pojo.Data;
import de.kreth.clubhelperbackend.utils.TimeProvider;

public abstract class AbstractDaoTest<T extends Data> {

	@Mock
	protected JdbcTemplate jdbcTemplate;
	protected ClubhelperRowMapper<T> mapper;
	protected AbstractDao<T> dao;
	@Mock
	protected SqlForDialect dialect;
	@Mock
	protected PlatformTransactionManager transMan;
	@Mock
	protected DeletedEntriesDao deletedEnriesDao;
	@Mock
	protected DataSource dataSource;
	@Mock
	protected Connection connection;
	@Mock
	protected TimeProvider timeProvider;
	
	protected Long objectId = 100L;
	protected Date now = new Date(1540058119L);

	public AbstractDaoTest() {
		super();
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		dao = configureDao();

		when(dialect.queryForIdentity(eq(DaoPackageMemberAccessor.getTableName(dao)))).thenReturn("queryForIdentity");
		when(jdbcTemplate.queryForObject("queryForIdentity", null, Long.class)).thenReturn(objectId);
		when(dataSource.getConnection()).thenReturn(connection);
		when(dataSource.getConnection(anyString(), anyString())).thenReturn(connection);
		when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
		when(timeProvider.getNow()).thenReturn(now);
		
		dao.setDataSource(dataSource);
		dao.setJdbcTemplate(jdbcTemplate);
		dao.setPlatformTransactionManager(transMan);
		dao.setDeletedEntriesDao(deletedEnriesDao);
		dao.setSqlDialect(dialect);
		dao.setTimeProvider(timeProvider);
	}

	@After
	public void increaseObjectIds() {
		objectId++;
	}
	
	protected abstract AbstractDao<T> configureDao();

}