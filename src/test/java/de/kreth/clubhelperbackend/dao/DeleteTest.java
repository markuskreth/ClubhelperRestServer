package de.kreth.clubhelperbackend.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import de.kreth.clubhelperbackend.aspects.DbCheckAspect;
import de.kreth.clubhelperbackend.config.SqlForDialect;
import de.kreth.clubhelperbackend.config.SqlForHsqlDb;
import de.kreth.clubhelperbackend.pojo.Contact;
import de.kreth.clubhelperbackend.pojo.Person;
import de.kreth.dbmanager.DatabaseType;

//@RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration(locations = { "/services-test-config.xml" })
public class DeleteTest {

	private ContactDao contactDao;
	private DeletedEntriesDao deletedEnriesDao;
	private DataSource dataSource;
	private StringWriter log;

	@Before
	public void setUp() throws Exception {

		log = new StringWriter();

		JDBCDataSource ds = new JDBCDataSource();
		ds.setUrl("jdbc:hsqldb:mem:testdb");
		ds.setUser("sa");
		ds.setLogWriter(new PrintWriter(log));
		dataSource = ds;

		PlatformTransactionManager man = new DataSourceTransactionManager(
				dataSource);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		deletedEnriesDao = new DeletedEntriesDao();
		deletedEnriesDao.setJdbcTemplate(jdbcTemplate);
		SqlForDialect sqlDialect = new SqlForHsqlDb(dataSource);
		deletedEnriesDao.setSqlDialect(sqlDialect);
		deletedEnriesDao.setPlatformTransactionManager(man);

		contactDao = new ContactDao();
		contactDao.setJdbcTemplate(jdbcTemplate);
		contactDao.setSqlDialect(sqlDialect);
		contactDao.setPlatformTransactionManager(man);
		contactDao.setDeletedEntriesDao(deletedEnriesDao);

		DbCheckAspect mysqlCheck = new DbCheckAspect(dataSource,
				DatabaseType.HSQLDB);
		mysqlCheck.checkDb();

		insertPersons(4L, man, deletedEnriesDao);

	}

	public void insertPersons(long untilId, PlatformTransactionManager transMan,
			DeletedEntriesDao deletedEntriesDao) {
		PersonDao pDao = new PersonDao();
		pDao.setDataSource(dataSource);

		pDao.setPlatformTransactionManager(transMan);
		pDao.setDeletedEntriesDao(deletedEntriesDao);

		pDao.setSqlDialect(new SqlForHsqlDb(dataSource));
		for (long id = 1; id < untilId; id++) {
			Person p = new Person(id, "test" + id, "test" + id, null);
			pDao.insert(p);
		}
	}

	@After
	public void closeDb() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement stm = connection.createStatement();
		stm.execute(
				"TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK");
		stm.execute("DROP SCHEMA PUBLIC CASCADE");
		connection.close();
	}

	@Test
	public void testDelete() throws SQLException {
		Date created = new Date();
		Contact c = new Contact(-1L, "MOBILE", "555123456", 1L);
		c.setCreated(created);
		c.setChanged(created);

		c = contactDao.insert(c);
		long longValue = c.getId().longValue();
		assertTrue(longValue >= 0);
		c = new Contact(-1L, "MOBILE", "12345678", 1L);
		c.setCreated(created);
		c.setChanged(created);
		c = contactDao.insert(c);
		assertEquals(longValue + 1, c.getId().longValue());

		assertTrue(c + " not deleted!", contactDao.delete(c));

		Connection con = dataSource.getConnection();
		Statement stm = con.createStatement();
		ResultSet rs = stm.executeQuery("SELECT * from contact");
		assertTrue(rs.next());
		assertTrue(rs.next());
		assertEquals(longValue + 1, rs.getLong("id"));
		Date deleted = rs.getDate("deleted");
		assertNotNull(deleted);
		assertTrue(deleted.getTime() > 0);

		rs = stm.executeQuery("SELECT * from deleted_entries");
		assertTrue("No Entries found in table", rs.next());
		assertEquals(ContactDao.tableName, rs.getString("tablename"));

		assertEquals(longValue + 1, rs.getLong("entryId"));
	}

	@Test
	public void queryAllwithoutDeleted() {
		assertEquals(0, contactDao.getAll().size());
		Date created = new Date();
		Contact c1 = contactDao.insert(new Contact(-1L, "Test", "5555", 1));
		c1.setCreated(created);
		c1.setChanged(created);
		Contact c2 = contactDao.insert(new Contact(-1L, "Test2", "6666", 1));
		c2.setCreated(created);
		c2.setChanged(created);

		assertEquals(2, contactDao.getAll().size());
		contactDao.delete(c1);
		List<Contact> all = contactDao.getAll();
		assertEquals(1, all.size());

		Contact actual = all.get(0);
		assertEquals(c2.getId(), actual.getId());
		assertEquals(c2.getPersonId(), actual.getPersonId());
		assertEquals(c2.getType(), actual.getType());
		assertEquals(c2.getValue(), actual.getValue());
	}
}
