package de.kreth.clubhelperbackend.config;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.kreth.dbmanager.ColumnDefinition;
import de.kreth.dbmanager.DataType;
import de.kreth.dbmanager.Database;
import de.kreth.dbmanager.TableDefinition;

public class DatabaseConfiguration {

	private TableDefinition person;
	private TableDefinition contact;
	private TableDefinition relative;
	private TableDefinition adress;
	private TableDefinition attendance;
	private TableDefinition version;

	public DatabaseConfiguration() {
		List<ColumnDefinition> columns = createPersonColumns();
		person = new TableDefinition("person", columns);

		columns = createContactColumns();
		contact = new TableDefinition("contact", columns);

		columns = createRelativeColumns();
		relative = new TableDefinition("relative", columns);

		columns = createAdressColumns();
		adress = new TableDefinition("adress", columns);

		columns = createAttendanceColumns();
		attendance = new TableDefinition("ATTENDANCE".toLowerCase(), columns);
		
		ColumnDefinition colVersion = new ColumnDefinition(DataType.INTEGER, "version", "NOT NULL");
		columns = new ArrayList<ColumnDefinition>();
		columns.add(colVersion);
		version = new TableDefinition("version", columns);
		
	}
	
	private List<ColumnDefinition> createAttendanceColumns() {
		ColumnDefinition colOnDate = new ColumnDefinition(DataType.DATETIME, "on_date");
		ColumnDefinition colPersonId = new ColumnDefinition(DataType.INTEGER, "person_id", "NOT NULL");
		ColumnDefinition colchanged = new ColumnDefinition(DataType.DATETIME, "changed");
		ColumnDefinition colcreated = new ColumnDefinition(DataType.DATETIME, "created");
		
		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(colOnDate);
		columns.add(colPersonId);
		columns.add(colchanged);
		columns.add(colcreated);
		return columns;
	}

	private List<ColumnDefinition> createAdressColumns() {
		ColumnDefinition colAdress1 = new ColumnDefinition(DataType.TEXT, "adress1");
		ColumnDefinition colAdress2 = new ColumnDefinition(DataType.TEXT, "adress2");
		ColumnDefinition colPlz = new ColumnDefinition(DataType.TEXT, "plz");
		ColumnDefinition colCity = new ColumnDefinition(DataType.TEXT, "city");
		ColumnDefinition colPersonId = new ColumnDefinition(DataType.INTEGER, "person_id", "NOT NULL");
		ColumnDefinition colchanged = new ColumnDefinition(DataType.DATETIME, "changed");
		ColumnDefinition colcreated = new ColumnDefinition(DataType.DATETIME, "created");
		
		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(colAdress1);
		columns.add(colAdress2);
		columns.add(colPlz);
		columns.add(colCity);
		columns.add(colPersonId);
		columns.add(colchanged);
		columns.add(colcreated);
		return columns;
	}

	private List<ColumnDefinition> createRelativeColumns() {
		ColumnDefinition colPerson1 = new ColumnDefinition(DataType.INTEGER, "person1", "NOT NULL");
		ColumnDefinition colPerson2 = new ColumnDefinition(DataType.INTEGER, "person2", "NOT NULL");
		ColumnDefinition colToPerson2 = new ColumnDefinition(DataType.TEXT, "TO_PERSON2_RELATION");
		ColumnDefinition colToPerson1 = new ColumnDefinition(DataType.TEXT, "TO_PERSON1_RELATION");
		ColumnDefinition colchanged = new ColumnDefinition(DataType.DATETIME, "changed");
		ColumnDefinition colcreated = new ColumnDefinition(DataType.DATETIME, "created");
		
		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(colPerson1);
		columns.add(colPerson2);
		columns.add(colToPerson1);
		columns.add(colToPerson2);
		columns.add(colchanged);
		columns.add(colcreated);
		return columns;
	}

	private List<ColumnDefinition> createContactColumns() {
		ColumnDefinition colType = new ColumnDefinition(DataType.TEXT, "type", "NOT NULL");
		ColumnDefinition colValue = new ColumnDefinition(DataType.TEXT, "value");
		ColumnDefinition colPerson = new ColumnDefinition(DataType.INTEGER, "person_id", "NOT NULL");
		ColumnDefinition colchanged = new ColumnDefinition(DataType.DATETIME, "changed");
		ColumnDefinition colcreated = new ColumnDefinition(DataType.DATETIME, "created");
		
		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(colType);
		columns.add(colValue);
		columns.add(colPerson);
		columns.add(colchanged);
		columns.add(colcreated);
		return columns;
	}

	private List<ColumnDefinition> createPersonColumns() {
		ColumnDefinition colPreName = new ColumnDefinition(DataType.TEXT, "prename", "NOT NULL");
		ColumnDefinition colSurName = new ColumnDefinition(DataType.TEXT, "surname");
		ColumnDefinition colType = new ColumnDefinition(DataType.TEXT, "type", "NOT NULL");
		ColumnDefinition colBirth = new ColumnDefinition(DataType.DATETIME, "birth");
		ColumnDefinition colchanged = new ColumnDefinition(DataType.DATETIME, "changed");
		ColumnDefinition colcreated = new ColumnDefinition(DataType.DATETIME, "created");
		
		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(colPreName);
		columns.add(colSurName);
		columns.add(colType);
		columns.add(colBirth);
		columns.add(colchanged);
		columns.add(colcreated);
		return columns;
	}

	public void executeOn(Database db) throws SQLException {
		List<TableDefinition> tables = new ArrayList<TableDefinition>();
		tables.add(person);
		tables.add(contact);
		tables.add(relative);
		tables.add(adress);
		tables.add(attendance);
		tables.add(version);
		
		for(TableDefinition def: tables) {
			String sql = de.kreth.dbmanager.DbManager.createSqlStatement(def);
			db.execSQL(sql);
		}
		String sql = "INSERT INTO version(version) VALUES (1)";
		db.execSQL(sql);
	}

	
}
