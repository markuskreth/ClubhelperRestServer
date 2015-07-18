package de.kreth.clubhelperbackend.config;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.kreth.dbmanager.*;

public class DbManager {

	private TableDefinition person;
	private TableDefinition version;

	public DbManager() {
		ColumnDefinition colPreName = new ColumnDefinition(DataType.TEXT, "prename", "NOT NULL");
		ColumnDefinition colSurName = new ColumnDefinition(DataType.TEXT, "surname");
		ColumnDefinition colType = new ColumnDefinition(DataType.TEXT, "type", "NOT NULL");
		ColumnDefinition colBirth = new ColumnDefinition(DataType.DATETIME, "birth");
		
		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(colPreName);
		columns.add(colSurName);
		columns.add(colType);
		columns.add(colBirth);
		person = new TableDefinition("person", columns);
		
		ColumnDefinition colVersion = new ColumnDefinition(DataType.INTEGER, "version", "NOT NULL");
		columns = new ArrayList<ColumnDefinition>();
		columns.add(colVersion);
		version = new TableDefinition("version", columns);
		
	}
	
	public void executeOn(Database db) throws SQLException {
		List<TableDefinition> tables = new ArrayList<TableDefinition>();
		tables.add(person);
		tables.add(version);
		
		for(TableDefinition def: tables) {
			db.execSQL(de.kreth.dbmanager.DbManager.createSqlStatement(def));
		}
	}

	
}
