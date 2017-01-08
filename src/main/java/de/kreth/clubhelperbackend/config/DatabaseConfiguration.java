package de.kreth.clubhelperbackend.config;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kreth.dbmanager.ColumnDefinition;
import de.kreth.dbmanager.DataType;
import de.kreth.dbmanager.Database;
import de.kreth.dbmanager.TableDefinition;

public class DatabaseConfiguration {

	private static final int LATEST_VERSION = 5;

	private final Logger logger;

	private TableDefinition person;
	private TableDefinition contact;
	private TableDefinition relative;
	private TableDefinition adress;
	private TableDefinition attendance;
	private TableDefinition version;
	private TableDefinition deletedEntries;
	private TableDefinition group;
	private TableDefinition persongroup;

	private List<TableDefinition> tablesToCreate;
	private Map<TableDefinition, List<ColumnDefinition>> tablesToAddColumns = null;
	private List<String> insertSql = new ArrayList<>();

	private int fromVersion;

	public DatabaseConfiguration(int fromVersion) {
		this.fromVersion = fromVersion;

		logger = LoggerFactory.getLogger(getClass());

		switch (fromVersion) {
		case 0:
			createAll();
			createWith(person, contact, relative, adress, attendance, version, deletedEntries, group, persongroup);
			break;
		case 1:
			createAll();
			createWith(deletedEntries, group, persongroup);
			addDeletedColumn(person, contact, relative, adress, attendance, version);
			addAuthColumns(person);
			insertSql.add(
					"INSERT INTO `groupDef`(`name`,`changed`,`created`)VALUES('ADMIN',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)");
			addUniquePersonGroup();
			break;
		case 2:
			createAll();
			createWith();
			addDeletedColumn(person, contact, relative, adress, attendance, version, deletedEntries, group,
					persongroup);
			addAuthColumns(person);
			insertSql.add(
					"INSERT INTO `groupDef`(`name`,`changed`,`created`)VALUES('ADMIN',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)");
			addUniqueGroupName();
			addUniquePersonGroup();
			break;
		case 3:
			createAll();
			addAuthColumns(person);
			insertSql.add(
					"INSERT INTO `groupDef`(`name`,`changed`,`created`)VALUES('ADMIN',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)");
			addUniqueGroupName();
			addUniquePersonGroup();
			break;
		case 4:
			addUniqueGroupName();
			addUniquePersonGroup();
		}
	}

	private void addUniqueGroupName() {
		insertSql.add("delete n1 from groupDef n1, groupDef n2 WHERE n1._id >n2._id AND n1.name = n2.name");
		insertSql.add("ALTER TABLE `groupDef` \n" + "ADD UNIQUE INDEX `groupname_UNIQUE` (`name` ASC);");
	}

	private void addUniquePersonGroup() {
		insertSql.add(
				"delete n1 from persongroup n1, persongroup n2 WHERE n1._id >n2._id AND n1.person_id = n2.person_id AND n1.group_id = n2.group_id;");
		insertSql.add("ALTER TABLE `persongroup` \n"
				+ "ADD UNIQUE INDEX `unique_person_group` (`person_id` ASC, `group_id` ASC);");
	}

	private void addAuthColumns(TableDefinition... defs) {

		if (tablesToAddColumns == null) {
			tablesToAddColumns = new HashMap<TableDefinition, List<ColumnDefinition>>();
		}

		List<ColumnDefinition> columns;

		for (TableDefinition t : defs) {
			columns = new ArrayList<ColumnDefinition>();
			columns.add(new ColumnDefinition(DataType.TEXT, "username", "DEFAULT NULL"));
			columns.add(new ColumnDefinition(DataType.TEXT, "password", "DEFAULT NULL"));
			tablesToAddColumns.put(t, columns);
		}
	}

	private void addDeletedColumn(TableDefinition... defs) {
		if (tablesToAddColumns == null) {
			tablesToAddColumns = new HashMap<TableDefinition, List<ColumnDefinition>>();
		}

		List<ColumnDefinition> columns;

		for (TableDefinition t : defs) {
			columns = new ArrayList<ColumnDefinition>();
			addDeleteColumn(columns);
			tablesToAddColumns.put(t, columns);
		}
	}

	private void createWith(TableDefinition... defs) {
		tablesToCreate = new ArrayList<TableDefinition>();
		for (TableDefinition d : defs) {
			tablesToCreate.add(d);
		}
	}

	private void createAll() {

		List<ColumnDefinition> columns = createPersonColumns();
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		person = new TableDefinition("person", columns);

		columns = createContactColumns();
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		contact = new TableDefinition("contact", columns);

		columns = createRelativeColumns();
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		relative = new TableDefinition("relative", columns);

		columns = createAdressColumns();
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		adress = new TableDefinition("adress", columns);

		columns = createAttendanceColumns();
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		attendance = new TableDefinition("ATTENDANCE".toLowerCase(), columns);

		ColumnDefinition colVersion = new ColumnDefinition(DataType.INTEGER, "version", "NOT NULL");
		columns = new ArrayList<ColumnDefinition>();
		columns.add(colVersion);
		version = new TableDefinition("version", columns);

		columns = createDeletedEntriesColumns();
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		deletedEntries = new TableDefinition("deleted_entries", columns);

		columns = createGroupColumns();
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		group = new TableDefinition("groupDef", columns);

		columns = createPersonGroupColumns();
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		persongroup = new TableDefinition("persongroup", columns);

	}

	private List<ColumnDefinition> createGroupColumns() {
		ColumnDefinition colTableName = new ColumnDefinition(DataType.VARCHAR255, "name", "NOT NULL UNIQUE");

		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(colTableName);
		return columns;
	}

	private List<ColumnDefinition> createPersonGroupColumns() {
		ColumnDefinition colPersonId = new ColumnDefinition(DataType.INTEGER, "person_id", "NOT NULL");
		ColumnDefinition colGroupId = new ColumnDefinition(DataType.INTEGER, "group_id", "NOT NULL");

		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(colPersonId);
		columns.add(colGroupId);
		return columns;
	}

	private List<ColumnDefinition> createDeletedEntriesColumns() {
		ColumnDefinition colTableName = new ColumnDefinition(DataType.VARCHAR25, "tablename", "NOT NULL");
		ColumnDefinition colEntryId = new ColumnDefinition(DataType.INTEGER, "entryId", "NOT NULL");

		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(colTableName);
		columns.add(colEntryId);
		return columns;
	}

	private List<ColumnDefinition> createAttendanceColumns() {
		ColumnDefinition colOnDate = new ColumnDefinition(DataType.DATETIME, "on_date");
		ColumnDefinition colPersonId = new ColumnDefinition(DataType.INTEGER, "person_id", "NOT NULL");

		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(colOnDate);
		columns.add(colPersonId);
		return columns;
	}

	private List<ColumnDefinition> createAdressColumns() {
		ColumnDefinition colAdress1 = new ColumnDefinition(DataType.TEXT, "adress1");
		ColumnDefinition colAdress2 = new ColumnDefinition(DataType.TEXT, "adress2");
		ColumnDefinition colPlz = new ColumnDefinition(DataType.TEXT, "plz");
		ColumnDefinition colCity = new ColumnDefinition(DataType.TEXT, "city");
		ColumnDefinition colPersonId = new ColumnDefinition(DataType.INTEGER, "person_id", "NOT NULL");

		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(colAdress1);
		columns.add(colAdress2);
		columns.add(colPlz);
		columns.add(colCity);
		columns.add(colPersonId);
		return columns;
	}

	private List<ColumnDefinition> createRelativeColumns() {
		ColumnDefinition colPerson1 = new ColumnDefinition(DataType.INTEGER, "person1", "NOT NULL");
		ColumnDefinition colPerson2 = new ColumnDefinition(DataType.INTEGER, "person2", "NOT NULL");
		ColumnDefinition colToPerson2 = new ColumnDefinition(DataType.TEXT, "TO_PERSON2_RELATION");
		ColumnDefinition colToPerson1 = new ColumnDefinition(DataType.TEXT, "TO_PERSON1_RELATION");

		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(colPerson1);
		columns.add(colPerson2);
		columns.add(colToPerson1);
		columns.add(colToPerson2);
		return columns;
	}

	private List<ColumnDefinition> createContactColumns() {
		ColumnDefinition colType = new ColumnDefinition(DataType.TEXT, "type", "NOT NULL");
		ColumnDefinition colValue = new ColumnDefinition(DataType.TEXT, "value");
		ColumnDefinition colPerson = new ColumnDefinition(DataType.INTEGER, "person_id", "NOT NULL");

		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(colType);
		columns.add(colValue);
		columns.add(colPerson);
		return columns;
	}

	private List<ColumnDefinition> createPersonColumns() {

		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(new ColumnDefinition(DataType.TEXT, "prename", "NOT NULL"));
		columns.add(new ColumnDefinition(DataType.TEXT, "surname"));
		columns.add(new ColumnDefinition(DataType.TEXT, "type", "NOT NULL"));
		columns.add(new ColumnDefinition(DataType.DATETIME, "birth"));
		columns.add(new ColumnDefinition(DataType.TEXT, "username", "DEFAULT NULL"));
		columns.add(new ColumnDefinition(DataType.TEXT, "password", "DEFAULT NULL"));

		return columns;
	}

	private void addDeleteColumn(List<ColumnDefinition> columns) {
		ColumnDefinition coldeleted = new ColumnDefinition(DataType.DATETIME, "deleted", " DEFAULT null");
		columns.add(coldeleted);
	}

	private void addCreateChangeColumn(List<ColumnDefinition> columns) {

		ColumnDefinition colchanged = new ColumnDefinition(DataType.DATETIME, "changed");
		ColumnDefinition colcreated = new ColumnDefinition(DataType.DATETIME, "created");
		columns.add(colchanged);
		columns.add(colcreated);
	}

	public void executeOn(Database db) throws SQLException {

		if (tablesToCreate != null) {

			for (TableDefinition def : tablesToCreate) {
				String sql = de.kreth.dbmanager.DbManager.createSqlStatement(def);
				logger.debug(sql);
				db.execSQL(sql);
			}

		}

		if (tablesToAddColumns != null) {

			for (Entry<TableDefinition, List<ColumnDefinition>> e : tablesToAddColumns.entrySet()) {
				String sql = de.kreth.dbmanager.DbManager.createSqlAddColumns(e.getKey(), e.getValue());
				logger.debug(sql);
				try {
					db.execSQL(sql);
				} catch (SQLException ex) {
					throw new SQLException("Error on: " + sql, ex);
				}
			}
		}

		for (String sql : insertSql) {
			logger.debug(sql);
			try {
				db.execSQL(sql);
			} catch (SQLException ex) {
				throw new SQLException("Error on: " + sql, ex);
			}
		}

		String sql;
		if (fromVersion == 0) {
			sql = "INSERT INTO version(version) VALUES (" + LATEST_VERSION + ")";
		} else {
			sql = "UPDATE version SET version=" + LATEST_VERSION;
		}

		if (fromVersion != LATEST_VERSION) {
			logger.debug(sql);
			db.execSQL(sql);
		} else {
			logger.info("Database was up to date.");
		}
	}

}
