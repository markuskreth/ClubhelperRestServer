package de.kreth.clubhelperbackend.config;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kreth.dbmanager.ColumnDefinition;
import de.kreth.dbmanager.DataType;
import de.kreth.dbmanager.Database;
import de.kreth.dbmanager.TableDefinition;

public class DatabaseConfiguration {

	private static final int LATEST_VERSION = 6;

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

	private final List<MyStatement> statements;

	private int fromVersion;

	public DatabaseConfiguration(int fromVersion) {
		this.fromVersion = fromVersion;
		statements = new ArrayList<>();
		
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
			statements.add(new DirectStatement("INSERT INTO `groupDef`(`name`,`changed`,`created`)VALUES('ADMIN',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)"));
			addUniquePersonGroup();
			addDeleteColumnStm(person, new ColumnDefinition(DataType.TEXT, "type", "NOT NULL"));
			break;
		case 2:
			createAll();
			createWith();
			addDeletedColumn(person, contact, relative, adress, attendance, version, deletedEntries, group,
					persongroup);
			addAuthColumns(person);statements.add(new DirectStatement("INSERT INTO `groupDef`(`name`,`changed`,`created`)VALUES('ADMIN',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)"));
			addUniqueGroupName();
			addUniquePersonGroup();
			addDeleteColumnStm(person, new ColumnDefinition(DataType.TEXT, "type", "NOT NULL"));
			break;
		case 3:
			createAll();
			addAuthColumns(person);statements.add(new DirectStatement("INSERT INTO `groupDef`(`name`,`changed`,`created`)VALUES('ADMIN',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)"));
			addUniqueGroupName();
			addUniquePersonGroup();
			addDeleteColumnStm(person, new ColumnDefinition(DataType.TEXT, "type", "NOT NULL"));
			break;
		case 4:
			addUniqueGroupName();
			addUniquePersonGroup();
			addDeleteColumnStm(person, new ColumnDefinition(DataType.TEXT, "type", "NOT NULL"));
			break;
		case 5:
			createAll();
			addDeleteColumnStm(person, new ColumnDefinition(DataType.TEXT, "type", "NOT NULL"));
			break;
			
		}
		if(fromVersion != LATEST_VERSION && logger.isInfoEnabled()) {
			logger.info("Prepared Datebase update from Version " + fromVersion + " to Version " + LATEST_VERSION);
		}
	}

	private void addDeleteColumnStm(TableDefinition table, ColumnDefinition columnDefinition) {
		statements.add(new DropColumnStatement(table, columnDefinition));
	}

	private void addDeleteColumn(List<ColumnDefinition> columns) {
		columns.add(new ColumnDefinition(DataType.DATETIME, "deleted", " DEFAULT null"));
	}

	private void addUniqueGroupName() {
		statements.add(new DirectStatement("delete n1 from groupDef n1, groupDef n2 WHERE n1._id >n2._id AND n1.name = n2.name"));
		statements.add(new DirectStatement("ALTER TABLE `groupDef` \n" + "ADD UNIQUE INDEX `groupname_UNIQUE` (`name` ASC);"));
	}

	private void addUniquePersonGroup() {
		statements.add(new DirectStatement("delete n1 from persongroup n1, persongroup n2 WHERE n1._id >n2._id AND n1.person_id = n2.person_id AND n1.group_id = n2.group_id;"));
		add("ALTER TABLE `persongroup` \n"
				+ "ADD UNIQUE INDEX `unique_person_group` (`person_id` ASC, `group_id` ASC);");
	}

	private void add(String sql) {
		statements.add(new DirectStatement(sql));
	}

	private void addAuthColumns(TableDefinition... defs) {

		for (TableDefinition t : defs) {
			statements.add(new AddColumnStatement(t, new ColumnDefinition(DataType.TEXT, "username", "DEFAULT NULL")));
			statements.add(new AddColumnStatement(t, new ColumnDefinition(DataType.TEXT, "password", "DEFAULT NULL")));
		}
	}

	private void addDeletedColumn(TableDefinition... defs) {

		for (TableDefinition t : defs) {
			statements.add(new AddColumnStatement(t, new ColumnDefinition(DataType.DATETIME, "deleted", " DEFAULT null")));
		}
	}

	private void createWith(TableDefinition... defs) {
		for (TableDefinition d : defs) {
			statements.add(new CreateTableStatement(d));
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

		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(new ColumnDefinition(DataType.INTEGER, "person1", "NOT NULL"));
		columns.add(new ColumnDefinition(DataType.INTEGER, "person2", "NOT NULL"));
		columns.add(new ColumnDefinition(DataType.TEXT, "TO_PERSON2_RELATION"));
		columns.add(new ColumnDefinition(DataType.TEXT, "TO_PERSON1_RELATION"));
		return columns;
	}

	private List<ColumnDefinition> createContactColumns() {
		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(new ColumnDefinition(DataType.TEXT, "type", "NOT NULL"));
		columns.add(new ColumnDefinition(DataType.TEXT, "value"));
		columns.add(new ColumnDefinition(DataType.INTEGER, "person_id", "NOT NULL"));
		return columns;
	}

	private List<ColumnDefinition> createPersonColumns() {

		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(new ColumnDefinition(DataType.TEXT, "prename", "NOT NULL"));
		columns.add(new ColumnDefinition(DataType.TEXT, "surname"));
		columns.add(new ColumnDefinition(DataType.DATETIME, "birth"));
		columns.add(new ColumnDefinition(DataType.TEXT, "username", "DEFAULT NULL"));
		columns.add(new ColumnDefinition(DataType.TEXT, "password", "DEFAULT NULL"));

		return columns;
	}

	private void addCreateChangeColumn(List<ColumnDefinition> columns) {

		columns.add(new ColumnDefinition(DataType.DATETIME, "changed"));
		columns.add(new ColumnDefinition(DataType.DATETIME, "created"));
	}

	public void executeOn(Database db) throws SQLException {

		for(MyStatement stm:statements) {
			String sql = stm.getSql();
			if(logger.isDebugEnabled()) {
				logger.debug(sql);
			}
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
			if(logger.isDebugEnabled()) {
				logger.debug(sql);
			}
			db.execSQL(sql);
			if(logger.isInfoEnabled()) {
				logger.info("Updated database to version " + LATEST_VERSION);
			}
		} else {
			if(logger.isInfoEnabled()) {
				logger.info("Database was up to date.");
			}
		}
	}
	
	private class DropColumnStatement extends AddColumnStatement {

		public DropColumnStatement(TableDefinition def, ColumnDefinition col) {
			super(def, col);
		}

		@Override
		public String getSql() {
			return de.kreth.dbmanager.DbManager.createSqlDropColumns(def, col);
		}
	}
	
	private class AddColumnStatement extends CreateTableStatement {

		protected ColumnDefinition col;

		public AddColumnStatement(TableDefinition def, ColumnDefinition col) {
			super(def);
			this.col = col;
		}
		
		@Override
		public String getSql() {
			return de.kreth.dbmanager.DbManager.createSqlAddColumns(def, col);
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + " " + col + " to " + def;
		}
	}
	
	private class CreateTableStatement implements MyStatement {

		protected TableDefinition def;

		public CreateTableStatement(TableDefinition def) {
			this.def = def;
		}
		
		@Override
		public String getSql() {
			return de.kreth.dbmanager.DbManager.createSqlStatement(def);
		}
		@Override
		public String toString() {
			return getClass().getSimpleName() + " " + def;
		}
		
	}
	
	private class DirectStatement implements MyStatement {

		private String sql;

		public DirectStatement(String sql) {
			super();
			this.sql = sql;
		}

		@Override
		public String getSql() {
			return sql;
		}
		
		@Override
		public String toString() {
			return sql;
		}
	}
	private interface MyStatement {
		String getSql();
	}
}
