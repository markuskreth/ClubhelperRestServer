package de.kreth.clubhelperbackend.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import de.kreth.clubhelperbackend.aspects.MysqlDbCheckAspect;
import de.kreth.clubhelperbackend.config.SqlForMysql;
import de.kreth.clubhelperbackend.pojo.Contact;

//@RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration(locations = { "/services-test-config.xml" })
public class DeleteTest {

	private ContactDao contactDao;
	private DeletedEntriesDao deletedEnriesDao;
	private MysqlConnectionPoolDataSource dataSource;

	@Before
	public void setUp() throws Exception {

		dataSource = new MysqlConnectionPoolDataSource();
		dataSource.setUser("markus");
		dataSource.setPassword("0773");
		dataSource.setServerName("localhost");
		dataSource.setPort(3306);
		dataSource.setDatabaseName("testdb");

		PlatformTransactionManager man = new DataSourceTransactionManager(dataSource);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		deletedEnriesDao = new DeletedEntriesDao();
		deletedEnriesDao.setJdbcTemplate(jdbcTemplate);
		SqlForMysql sqlDialect = new SqlForMysql(dataSource);
		deletedEnriesDao.setSqlDialect(sqlDialect);
		deletedEnriesDao.setPlatformTransactionManager(man);

		contactDao = new ContactDao();
		contactDao.setJdbcTemplate(jdbcTemplate);
		contactDao.setSqlDialect(sqlDialect);
		contactDao.setPlatformTransactionManager(man);
		contactDao.setDeletedEntriesDao(deletedEnriesDao);

		MysqlDbCheckAspect mysqlCheck = new MysqlDbCheckAspect(dataSource);
		mysqlCheck.checkDb();
	}

	@After
	public void tearDown() throws SQLException {

		if (dataSource != null) {
			Connection conn = dataSource.getConnection();
			deleteTables(conn);
			conn.close();
		}

	}

	private void deleteTables(Connection conn) throws SQLException {

		ResultSet rs = conn.getMetaData().getTables(conn.getCatalog(), null, "%", null);
		List<String> allSql = new ArrayList<String>();

		while (rs.next()) {
			String tableName = rs.getString("TABLE_NAME");
			String type = rs.getString("TABLE_TYPE");
			String sql = String.format("DROP %s %s", type, tableName);
			allSql.add(sql);
		}
		rs.close();
		Statement stm = conn.createStatement();
		for (String sql : allSql) {
			stm.execute(sql);
		}
	}

	@Test
	public void testDelete() throws SQLException {
		Date created = new Date();
		Contact c = new Contact(-1L, "MOBILE", "555123456", 1L, created, created);
		c = contactDao.insert(c);
		assertEquals(1L, c.getId().longValue());
		c = new Contact(-1L, "MOBILE", "12345678", 1L, created, created);
		c = contactDao.insert(c);
		assertEquals(2L, c.getId().longValue());

		assertTrue(c + " not deleted!", contactDao.delete(c));

		Connection con = dataSource.getConnection();
		Statement stm = con.createStatement();
		ResultSet rs = stm.executeQuery("SELECT * from contact");
		assertTrue(rs.next());
		assertTrue(rs.next());
		assertEquals(2L, rs.getLong("_id"));
		Date deleted = rs.getDate("deleted");
		assertNotNull(deleted);
		assertTrue(deleted.getTime() > 0);

		rs = stm.executeQuery("SELECT * from deleted_entries");
		assertTrue("No Entries found in table", rs.next());
		assertEquals(ContactDao.tableName, rs.getString("tablename"));

		assertEquals(2L, rs.getLong("entryId"));
	}

	@Test
	public void queryAllwithoutDeleted() {
		assertEquals(0, contactDao.getAll().size());
		Date created = new Date();
		Contact c1 = contactDao.insert(new Contact(-1L, "Test", "5555", 1, created, created));
		Contact c2 = contactDao.insert(new Contact(-1L, "Test2", "6666", 1, created, created));

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
