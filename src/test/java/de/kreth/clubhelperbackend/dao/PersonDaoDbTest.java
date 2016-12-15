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

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import de.kreth.clubhelperbackend.aspects.MysqlDbCheckAspect;
import de.kreth.clubhelperbackend.pojo.Adress;
import de.kreth.clubhelperbackend.pojo.Attendance;
import de.kreth.clubhelperbackend.pojo.Contact;
import de.kreth.clubhelperbackend.pojo.Person;
import de.kreth.clubhelperbackend.pojo.PersonGroup;
import de.kreth.clubhelperbackend.pojo.Relative;

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

		ResultSet rs = conn.getMetaData().getTables(null, null, "%", null);
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
	public void testInitDB() throws SQLException {
		dbCheck.checkDb();

		Connection conn = dataSource.getConnection();
		ResultSet rs = conn.getMetaData().getTables(null, null, "%", null);

		List<String> tables = new ArrayList<String>();

		while (rs.next()) {
			String tableName = rs.getString("TABLE_NAME");
			tables.add(tableName);
		}

		rs.close();

		assertEquals(9, tables.size());

		assertTrue("Person Table not found!", tables.contains(Person.class.getSimpleName().toLowerCase()));
		assertTrue("Adress Table not found!", tables.contains(Adress.class.getSimpleName().toLowerCase()));
		assertTrue("Contact Table not found!", tables.contains(Contact.class.getSimpleName().toLowerCase()));
		assertTrue("Relative Table not found!", tables.contains(Relative.class.getSimpleName().toLowerCase()));
		assertTrue("Attendance Table not found!", tables.contains(Attendance.class.getSimpleName().toLowerCase()));
		assertTrue("Group Table not found!", tables.contains(GroupDao.TABLE_NAME));
		assertTrue("PersonGroup Table not found!", tables.contains(PersonGroup.class.getSimpleName().toLowerCase()));
		assertTrue("DeletedEntries Table not found!", tables.contains(DeletedEntriesDao.TABLE_NAME));
		assertTrue("version Table not found!", tables.contains("version"));

	}

	@Test
	public void testPersonTableComplete() throws SQLException {

		dbCheck.checkDb();

		Connection conn = dataSource.getConnection();
		DatabaseMetaData metaData = conn.getMetaData();
		ResultSet rs = metaData.getColumns(null, null, "person", "%");

		List<String> cols = new ArrayList<String>();
		while (rs.next()) {
			cols.add(rs.getString("COLUMN_NAME"));
		}

		assertEquals(8, cols.size());
	}

}
