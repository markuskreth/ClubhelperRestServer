package de.kreth.clubhelperbackend.dao;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import de.kreth.clubhelperbackend.config.SqlForDialect;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao.RowMapper;
import de.kreth.clubhelperbackend.dao.abstr.DaoPackageMemberAccessor;
import de.kreth.clubhelperbackend.pojo.Data;

public abstract class AbstractDaoTest<T extends Data> {

	protected JdbcTemplate jdbcTemplate;
	protected RowMapper<T> mapper;
	protected AbstractDao<T> dao;
	protected SqlForDialect dialect;
	protected PlatformTransactionManager transMan;
	protected DeletedEntriesDao deletedEnriesDao;
	protected Long objectId = 100L;
	protected ArgumentCaptor<String> sqlCaptor;
	protected ArgumentCaptor<Long> idCaptor;

	public AbstractDaoTest() {
		super();
	}

	@Before
	public void setUp() throws Exception {
		jdbcTemplate = mock(JdbcTemplate.class);
		transMan = mock(PlatformTransactionManager.class);
		deletedEnriesDao = mock(DeletedEntriesDao.class);
		dialect = mock(SqlForDialect.class);

		dao = configureDao();

		when(dialect.queryForIdentity(DaoPackageMemberAccessor.getTableName(dao))).thenReturn("queryForIdentity");
		when(jdbcTemplate.queryForObject("queryForIdentity", null, Long.class)).thenReturn(objectId);
		
		dao.setJdbcTemplate(jdbcTemplate);
		dao.setPlatformTransactionManager(transMan);
		dao.setDeletedEntriesDao(deletedEnriesDao);
		dao.setSqlDialect(dialect);
		sqlCaptor = ArgumentCaptor.forClass(String.class);

		idCaptor = ArgumentCaptor.forClass(Long.class);
		
	}

	@After
	public void increaseObjectIds() {
		objectId++;
	}
	
	protected abstract AbstractDao<T> configureDao();

}