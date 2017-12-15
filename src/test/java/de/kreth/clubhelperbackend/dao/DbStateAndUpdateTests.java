package de.kreth.clubhelperbackend.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.kreth.clubhelperbackend.pojo.Adress;
import de.kreth.clubhelperbackend.pojo.Attendance;
import de.kreth.clubhelperbackend.pojo.Contact;
import de.kreth.clubhelperbackend.pojo.Person;
import de.kreth.clubhelperbackend.pojo.PersonGroup;
import de.kreth.clubhelperbackend.pojo.Relative;

public class DbStateAndUpdateTests  extends AbstractMysqlDatabaseTests {

	@Test
	public void testDatabaseUpdateFromVersion5To5() throws SQLException {
		DatabaseConfigVersion5 v5 = new DatabaseConfigVersion5();
		Connection conn = dataSource.getConnection();
		deleteTables(conn);
		v5.installOn(conn);
		
		DatabaseMetaData metaData = conn.getMetaData();
		ResultSet rs = metaData.getColumns(conn.getCatalog(), null, "person", "type");
		assertTrue(rs.next());
		assertEquals("type", rs.getString("COLUMN_NAME"));
		assertFalse(rs.next());
		
	}

	@Test
	public void testPersonTableComplete() throws SQLException {

		dbCheck.checkDb();

		Connection conn = dataSource.getConnection();
		DatabaseMetaData metaData = conn.getMetaData();
		ResultSet rs = metaData.getColumns(conn.getCatalog(), null, "person", "%");

		List<String> cols = new ArrayList<String>();
		while (rs.next()) {
			cols.add(rs.getString("COLUMN_NAME") + " (" + rs.getString("TYPE_NAME") + ")");
		}

		assertEquals("Found Columns: " + cols, 9, cols.size());
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

}
