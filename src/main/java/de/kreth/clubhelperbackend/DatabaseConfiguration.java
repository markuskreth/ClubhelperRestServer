package de.kreth.clubhelperbackend;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.kreth.dbmanager.ColumnDefinition;
import de.kreth.dbmanager.DataType;
import de.kreth.dbmanager.Database;
import de.kreth.dbmanager.TableDefinition;

public class DatabaseConfiguration {

	private TableDefinition person;
	private TableDefinition version;

	public DatabaseConfiguration() {
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
			String sql = de.kreth.dbmanager.DbManager.createSqlStatement(def);
			db.execSQL(sql);
		}
		String sql = "INSERT INTO version(version) VALUES (1)";
		db.execSQL(sql);
	}

	
}
