package de.kreth.clubhelperbackend.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.text.IsEqualIgnoringCase;
import org.junit.Test;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.Adress;
import de.kreth.clubhelperbackend.pojo.Attendance;
import de.kreth.clubhelperbackend.pojo.Contact;
import de.kreth.clubhelperbackend.pojo.Person;
import de.kreth.clubhelperbackend.pojo.PersonGroup;
import de.kreth.clubhelperbackend.pojo.Relative;

public class DbStateAndUpdateTests extends AbstractDatabaseTests<Person> {

	@Test
	public void testDatabaseUpdateFromVersion5To5() throws SQLException {
		DatabaseConfigVersion5 v5 = new DatabaseConfigVersion5();
		Connection conn = dataSource.getConnection();
		deleteTables(conn);
		v5.installOn(conn);

		ResultSetMetaData meta = conn.createStatement()
				.executeQuery("select * from person").getMetaData();
		int columnCount = meta.getColumnCount();
		assertEquals(10, columnCount);

		String tableName = meta.getTableName(1);
		Matcher<String> matcher = new IsEqualIgnoringCase("person");
		assertThat(tableName, matcher);

		dbCheck.checkDb(true);
		meta = conn.createStatement().executeQuery("select * from person")
				.getMetaData();
		columnCount = meta.getColumnCount();
		assertEquals(9, columnCount);

	}

	@Test
	public void checkPersonTable() throws SQLException {
		dbCheck.checkDb();
		Connection conn = dataSource.getConnection();
		ResultSet rs = conn.createStatement()
				.executeQuery("select * from person");
		ResultSetMetaData meta = rs.getMetaData();
		int columnCount = meta.getColumnCount();
		List<String> columns = new ArrayList<>();

		for (int i = 0; i < columnCount; i++) {
			columns.add(meta.getColumnName(i + 1));
		}
		assertTrue("No Columns in Person table", columnCount > 0);
	}

	@Test
	public void testPersonTableComplete() throws SQLException {

		dbCheck.checkDb();

		Connection conn = dataSource.getConnection();
		DatabaseMetaData metaData = conn.getMetaData();
		ResultSet rs = metaData.getColumns(null, null, "PERSON", "%");

		List<String> cols = new ArrayList<String>();
		while (rs.next()) {
			cols.add(rs.getString("COLUMN_NAME") + " ("
					+ rs.getString("TYPE_NAME") + ")");
		}

		assertEquals("Found Columns: " + cols, 9, cols.size());
	}

	@Test
	public void testInitDB() throws SQLException {
		dbCheck.checkDb();

		Connection conn = dataSource.getConnection();
		ResultSet rs = conn.getMetaData().getTables(conn.getCatalog(), null,
				"%", new String[]{"TABLE"});

		List<String> tables = new ArrayList<String>();

		while (rs.next()) {
			String tableName = rs.getString("TABLE_NAME");
			StringBuilder txt = new StringBuilder();
			for (int i = 1; i < 6; i++) {
				txt.append(";").append(rs.getString(i));
			}
			System.out.println(txt);
			tables.add(tableName.toLowerCase());
		}

		rs.close();

		assertEquals(11, tables.size());

		assertTrue("Person Table not found!",
				tables.contains(Person.class.getSimpleName().toLowerCase()));
		assertTrue("Adress Table not found!",
				tables.contains(Adress.class.getSimpleName().toLowerCase()));
		assertTrue("Contact Table not found!",
				tables.contains(Contact.class.getSimpleName().toLowerCase()));
		assertTrue("Relative Table not found!",
				tables.contains(Relative.class.getSimpleName().toLowerCase()));
		assertTrue("Attendance Table not found!", tables
				.contains(Attendance.class.getSimpleName().toLowerCase()));
		assertTrue("Group Table not found!",
				tables.contains(GroupDao.TABLE_NAME.toLowerCase()));
		assertTrue("PersonGroup Table not found!", tables
				.contains(PersonGroup.class.getSimpleName().toLowerCase()));
		assertTrue("DeletedEntries Table not found!",
				tables.contains(DeletedEntriesDao.TABLE_NAME.toLowerCase()));
		assertTrue("version Table not found!", tables.contains("version"));
		assertTrue("personcompetition Table not found!",
				tables.contains("personcompetition"));
		assertTrue("startpaesse Table not found!", tables.contains("startpaesse"));
		assertTrue("version Table not found!", tables.contains("startpass_startrechte"));

	}

	@Override
	public AbstractDao<Person> initDao() {
		return new PersonDao();
	}

}
