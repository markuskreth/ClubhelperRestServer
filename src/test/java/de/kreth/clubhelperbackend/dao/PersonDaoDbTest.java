package de.kreth.clubhelperbackend.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import de.kreth.clubhelperbackend.aspects.MysqlDbCheckAspect;
import de.kreth.clubhelperbackend.config.SqlForMysql;
import de.kreth.clubhelperbackend.pojo.Adress;
import de.kreth.clubhelperbackend.pojo.Attendance;
import de.kreth.clubhelperbackend.pojo.Contact;
import de.kreth.clubhelperbackend.pojo.Person;
import de.kreth.clubhelperbackend.pojo.PersonGroup;
import de.kreth.clubhelperbackend.pojo.Relative;
import de.kreth.clubhelperbackend.testutils.TestDataPerson;

public class PersonDaoDbTest {

	private static final String db_file_name_prefix = "TestDatabase";

	private MysqlDbCheckAspect dbCheck;

	private MysqlConnectionPoolDataSource dataSource;

	@Before
	public void setUp() throws Exception {

		dataSource = new MysqlConnectionPoolDataSource();
		dataSource.setUser("markus");
		dataSource.setPassword("0773");
		dataSource.setServerName("localhost");
		dataSource.setPort(3306);
		dataSource.setDatabaseName("testdb");

		deleteTables(dataSource.getConnection());

		dbCheck = new MysqlDbCheckAspect(dataSource);

	}

	@After
	public void tearDown() throws SQLException {

		Connection conn = dataSource.getConnection();
		deleteTables(conn);

		if (dataSource != null) {
			conn.close();
		}
		File dbFile = new File(db_file_name_prefix);
		if (dbFile.exists()) {
			dbFile.delete();
		}
	}

	private void deleteTables(Connection conn) throws SQLException {

		String[] types = { "TABLE", "VIEW" };
		DatabaseMetaData metaData = conn.getMetaData();
		String catalog = conn.getCatalog();

		ResultSet rs = metaData.getTables(catalog, null, "%", types);
		List<String> allSql = new ArrayList<String>();

		while (rs.next()) {
			String type = rs.getString("TABLE_TYPE");

			if (type != null && type.startsWith("SYSTEM") == false) {
				String tableName = rs.getString("TABLE_NAME");
				String sql = String.format("DROP %s %s", type, tableName);
				allSql.add(sql);
			}
		}
		rs.close();
		Statement stm = conn.createStatement();
		for (String sql : allSql) {
			try {
				stm.execute(sql);
			} catch (SQLException e) {
				throw new SQLException("SQL: " + sql, e);
			}
		}
	}

	@Test
	public void testInitDB() throws SQLException {
		dbCheck.checkDb();

		Connection conn = dataSource.getConnection();
		ResultSet rs = conn.getMetaData().getTables(conn.getCatalog(), null, "%", null);

		List<String> tables = new ArrayList<String>();

		while (rs.next()) {
			String tableName = rs.getString("TABLE_NAME");
			tables.add(tableName.toLowerCase());
		}

		rs.close();

		assertEquals(9, tables.size());

		assertTrue("Person Table not found!", tables.contains(Person.class.getSimpleName().toLowerCase()));
		assertTrue("Adress Table not found!", tables.contains(Adress.class.getSimpleName().toLowerCase()));
		assertTrue("Contact Table not found!", tables.contains(Contact.class.getSimpleName().toLowerCase()));
		assertTrue("Relative Table not found!", tables.contains(Relative.class.getSimpleName().toLowerCase()));
		assertTrue("Attendance Table not found!", tables.contains(Attendance.class.getSimpleName().toLowerCase()));
		assertTrue("Group Table not found!", tables.contains(GroupDao.TABLE_NAME.toLowerCase()));
		assertTrue("PersonGroup Table not found!", tables.contains(PersonGroup.class.getSimpleName().toLowerCase()));
		assertTrue("DeletedEntries Table not found!", tables.contains(DeletedEntriesDao.TABLE_NAME.toLowerCase()));
		assertTrue("version Table not found!", tables.contains("version"));

	}

	@Test
	public void testPersonTableComplete() throws SQLException {

		dbCheck.checkDb();

		Connection conn = dataSource.getConnection();
		DatabaseMetaData metaData = conn.getMetaData();
		ResultSet rs = metaData.getColumns(conn.getCatalog(), null, "person", "%");

		List<String> cols = new ArrayList<String>();
		while (rs.next()) {
			cols.add(rs.getString("COLUMN_NAME"));
		}

		assertEquals(10, cols.size());
	}

	@Test
	public void insertDelete() throws Exception {
		dbCheck.checkDb();
		DataSourceTransactionManager transMan = new DataSourceTransactionManager(dataSource);

		DeletedEntriesDao deletedEntriesDao = new DeletedEntriesDao();
		deletedEntriesDao.setDataSource(dataSource);
		deletedEntriesDao.setPlatformTransactionManager(transMan);
		deletedEntriesDao.setSqlDialect(new SqlForMysql(dataSource));

		PersonDao dao = new PersonDao();
		dao.setDataSource(dataSource);
		dao.setPlatformTransactionManager(transMan);
		dao.setDeletedEntriesDao(deletedEntriesDao);

		dao.setSqlDialect(new SqlForMysql(dataSource));
		Person p1 = TestDataPerson.INSTANCE.person;
		Person p2 = (Person) p1.clone();

		assertEquals(p1, p2);
		p2.setId(p1.getId() + 1);
		p2.setSurname("AAAAA");
		p2.setPrename("AAAAAA");

		Person p3 = (Person) p1.clone();
		p3.setId(p1.getId() + 2);
		p3.setPrename("AAAAAAA");
		p3.setSurname("ZZZZZZZZZ");

		dao.insert(p3);
		dao.insert(p2);
		dao.insert(p1);

		assertEquals(3, dao.getAll().size());
		dao.delete(p3);
		dao.delete(p2);
		assertEquals(1, dao.getAll().size());
	}

	@Test
	public void personListIsSorted() throws Exception {

		dbCheck.checkDb();
		PersonDao dao = new PersonDao();
		dao.setDataSource(dataSource);
		dao.setPlatformTransactionManager(new DataSourceTransactionManager(dataSource));

		dao.setSqlDialect(new SqlForMysql(dataSource));
		Person p1 = TestDataPerson.INSTANCE.person;
		Person p2 = (Person) p1.clone();

		assertEquals(p1, p2);
		p2.setId(p1.getId() + 1);
		p2.setSurname("AAAAA");
		p2.setPrename("AAAAAA");

		Person p3 = (Person) p1.clone();
		p3.setId(p1.getId() + 2);
		p3.setPrename("AAAAAAA");
		p3.setSurname("ZZZZZZZZZ");

		dao.insert(p3);
		dao.insert(p2);
		dao.insert(p1);

		List<Person> all = dao.getAll();

		assertEquals(p2.getId(), all.get(0).getId());
		assertEquals(p1.getId(), all.get(1).getId());
		assertEquals(p3.getId(), all.get(2).getId());
	}
}
