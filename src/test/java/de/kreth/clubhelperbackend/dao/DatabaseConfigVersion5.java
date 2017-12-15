package de.kreth.clubhelperbackend.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.kreth.dbmanager.ColumnDefinition;
import de.kreth.dbmanager.DataType;
import de.kreth.dbmanager.TableDefinition;

public class DatabaseConfigVersion5 {
	
	public final TableDefinition person;
	public final TableDefinition contact;
	public final TableDefinition relative;
	public final TableDefinition adress;
	public final TableDefinition attendance;
	public final TableDefinition version;
	public final TableDefinition deletedEntries;
	public final TableDefinition group;
	public final TableDefinition persongroup;

	public DatabaseConfigVersion5() {

		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(new ColumnDefinition(DataType.TEXT, "prename", "NOT NULL"));
		columns.add(new ColumnDefinition(DataType.TEXT, "surname"));
		columns.add(new ColumnDefinition(DataType.TEXT, "type", "NOT NULL"));
		columns.add(new ColumnDefinition(DataType.DATETIME, "birth"));
		columns.add(new ColumnDefinition(DataType.TEXT, "username", "DEFAULT NULL"));
		columns.add(new ColumnDefinition(DataType.TEXT, "password", "DEFAULT NULL"));
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		person = new TableDefinition("person", columns);

		columns = new ArrayList<ColumnDefinition>();
		columns.add(new ColumnDefinition(DataType.TEXT, "type", "NOT NULL"));
		columns.add(new ColumnDefinition(DataType.TEXT, "value"));
		columns.add(new ColumnDefinition(DataType.INTEGER, "person_id", "NOT NULL"));
		
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		contact = new TableDefinition("contact", columns);
		columns = new ArrayList<ColumnDefinition>();
		columns.add(new ColumnDefinition(DataType.INTEGER, "person1", "NOT NULL"));
		columns.add(new ColumnDefinition(DataType.INTEGER, "person2", "NOT NULL"));
		columns.add(new ColumnDefinition(DataType.TEXT, "TO_PERSON2_RELATION"));
		columns.add(new ColumnDefinition(DataType.TEXT, "TO_PERSON1_RELATION"));
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		relative = new TableDefinition("relative", columns);

		columns = new ArrayList<ColumnDefinition>();
		columns.add(new ColumnDefinition(DataType.TEXT, "adress1"));
		columns.add(new ColumnDefinition(DataType.TEXT, "adress2"));
		columns.add(new ColumnDefinition(DataType.TEXT, "plz"));
		columns.add(new ColumnDefinition(DataType.TEXT, "city"));
		columns.add(new ColumnDefinition(DataType.INTEGER, "person_id", "NOT NULL"));
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		adress = new TableDefinition("adress", columns);

		columns = new ArrayList<ColumnDefinition>();
		columns.add(new ColumnDefinition(DataType.DATETIME, "on_date"));
		columns.add(new ColumnDefinition(DataType.INTEGER, "person_id", "NOT NULL"));
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		attendance = new TableDefinition("ATTENDANCE".toLowerCase(), columns);
		
		columns = new ArrayList<ColumnDefinition>();
		columns.add(new ColumnDefinition(DataType.INTEGER, "version", "NOT NULL"));
		version = new TableDefinition("version", columns);

		columns = new ArrayList<ColumnDefinition>();
		columns.add(new ColumnDefinition(DataType.VARCHAR25, "tablename", "NOT NULL"));
		columns.add(new ColumnDefinition(DataType.INTEGER, "entryId", "NOT NULL"));
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		deletedEntries = new TableDefinition("deleted_entries", columns);

		columns = new ArrayList<ColumnDefinition>();
		columns.add(new ColumnDefinition(DataType.VARCHAR255, "name", "NOT NULL UNIQUE"));
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		group = new TableDefinition("groupDef", columns);

		columns = new ArrayList<ColumnDefinition>();
		columns.add(new ColumnDefinition(DataType.INTEGER, "person_id", "NOT NULL"));
		columns.add(new ColumnDefinition(DataType.INTEGER, "group_id", "NOT NULL"));
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		persongroup = new TableDefinition("persongroup", columns);
	}

	private void addCreateChangeColumn(List<ColumnDefinition> columns) {

		columns.add(new ColumnDefinition(DataType.DATETIME, "changed"));
		columns.add(new ColumnDefinition(DataType.DATETIME, "created"));
	}

	private void addDeleteColumn(List<ColumnDefinition> columns) {
		columns.add(new ColumnDefinition(DataType.DATETIME, "deleted", " DEFAULT null"));
	}

	public void installOn(Connection connection) throws SQLException {
		Statement stm = connection.createStatement();
		stm.execute(de.kreth.dbmanager.DbManager.createSqlStatement(person));
		stm.execute(de.kreth.dbmanager.DbManager.createSqlStatement(contact));
		stm.execute(de.kreth.dbmanager.DbManager.createSqlStatement(relative));
		stm.execute(de.kreth.dbmanager.DbManager.createSqlStatement(adress));
		stm.execute(de.kreth.dbmanager.DbManager.createSqlStatement(attendance));
		stm.execute(de.kreth.dbmanager.DbManager.createSqlStatement(version));
		stm.execute(de.kreth.dbmanager.DbManager.createSqlStatement(deletedEntries));
		stm.execute(de.kreth.dbmanager.DbManager.createSqlStatement(group));
		stm.execute(de.kreth.dbmanager.DbManager.createSqlStatement(persongroup));
	}
}
