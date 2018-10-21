package de.kreth.clubhelperbackend.config;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kreth.clubhelperbackend.pojo.Adress;
import de.kreth.clubhelperbackend.pojo.Attendance;
import de.kreth.clubhelperbackend.pojo.Contact;
import de.kreth.clubhelperbackend.pojo.DeletedEntries;
import de.kreth.clubhelperbackend.pojo.Group;
import de.kreth.clubhelperbackend.pojo.Person;
import de.kreth.clubhelperbackend.pojo.PersonGroup;
import de.kreth.clubhelperbackend.pojo.Relative;
import de.kreth.clubhelperbackend.pojo.Startpass;
import de.kreth.clubhelperbackend.pojo.Startrecht;
import de.kreth.dbmanager.ColumnDefinition;
import de.kreth.dbmanager.DataType;
import de.kreth.dbmanager.Database;
import de.kreth.dbmanager.DatabaseType;
import de.kreth.dbmanager.TableDefinition;
import de.kreth.dbmanager.UniqueConstraint;

public class DatabaseConfiguration {

	private static final String PERSON_ID_FK_NAME = "person_id";

	private static final int LATEST_VERSION = 10;

	private final Logger logger;
	private final DatabaseType dbType;

	private List<TableDefinition> allTables;

	@Pojo(pojoClass=Person.class)
	private TableDefinition person;
	@Pojo(pojoClass=Contact.class)
	private TableDefinition contact;
	@Pojo(pojoClass=Relative.class)
	private TableDefinition relative;
	@Pojo(pojoClass=Adress.class)
	private TableDefinition adress;
	@Pojo(pojoClass=Attendance.class)
	private TableDefinition attendance;
	private TableDefinition version;
	@Pojo(pojoClass=DeletedEntries.class)
	private TableDefinition deletedEntries;
	@Pojo(pojoClass=Group.class)
	private TableDefinition group;
	@Pojo(pojoClass=PersonGroup.class)
	private TableDefinition persongroup;
	@Pojo(pojoClass=Startpass.class)
	private TableDefinition startpass;
	@Pojo(pojoClass=Startrecht.class)
	private TableDefinition startrecht;

	private final List<MyStatement> statements;

	private int fromVersion;

	public DatabaseConfiguration(int fromVersion) {
		this(fromVersion, DatabaseType.MYSQL);
	}

	public DatabaseConfiguration(int fromVersion, DatabaseType dbType) {
		this.fromVersion = fromVersion;
		this.dbType = dbType;
		statements = new ArrayList<>();

		logger = LoggerFactory.getLogger(getClass());

		switch (fromVersion) {
			case 0 :
				createAll();
				createWith(person, contact, relative, adress, attendance,
						version, deletedEntries, group, persongroup, startpass,
						startrecht);
				createAttendenceUniqueConstraint();
				createForeignkeysForMainTables();
				addForeignKey(startrecht, "startpass_id", startpass, "id");
				addForeignKey(startpass, "person_id", person, "id");
				break;
			case 1 :
				createAll();
				createWith(deletedEntries, group, persongroup);
				addDeletedColumn(person, contact, relative, adress, attendance,
						version, startpass);
				addAuthColumns(person);
				statements.add(new DirectStatement(
						"INSERT INTO `groupDef`(`name`,`changed`,`created`)VALUES('ADMIN',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)"));
				addUniquePersonGroup();
				addDeleteColumnStm(person, new ColumnDefinition(DataType.TEXT,
						"type", "NOT NULL"));
				createAttendenceUniqueConstraint();
				break;
			case 2 :
				createAll();
				createWith(startpass);
				addDeletedColumn(person, contact, relative, adress, attendance,
						version, deletedEntries, group, persongroup);
				addAuthColumns(person);
				statements.add(new DirectStatement(
						"INSERT INTO `groupDef`(`name`,`changed`,`created`)VALUES('ADMIN',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)"));
				addUniqueGroupName();
				addUniquePersonGroup();
				addDeleteColumnStm(person, new ColumnDefinition(DataType.TEXT,
						"type", "NOT NULL"));
				createAttendenceUniqueConstraint();
				break;
			case 3 :
				createAll();
				createWith(startpass);
				addAuthColumns(person);
				statements.add(new DirectStatement(
						"INSERT INTO `groupDef`(`name`,`changed`,`created`)VALUES('ADMIN',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)"));
				addUniqueGroupName();
				addUniquePersonGroup();
				addDeleteColumnStm(person, new ColumnDefinition(DataType.TEXT,
						"type", "NOT NULL"));
				createAttendenceUniqueConstraint();
				break;
			case 4 :
				createWith(startpass);
				addUniqueGroupName();
				addUniquePersonGroup();
				addDeleteColumnStm(person, new ColumnDefinition(DataType.TEXT,
						"type", "NOT NULL"));
				createAttendenceUniqueConstraint();
				break;
			case 5 :
				createAll();
				createWith(startpass);
				addDeleteColumnStm(person, new ColumnDefinition(DataType.TEXT,
						"type", "NOT NULL"));
				createAttendenceUniqueConstraint();
				break;
			case 6 :
				createAll();
				createWith(startpass);
				createAttendenceUniqueConstraint();
				changeIdNamesInAllTables();
				break;
			case 7 :
				createAll();
				createWith(startpass);
				changeIdNamesInAllTables();
				break;
			case 8 :
				createAll();
				createWith(startpass, startrecht);
				addForeignKey(startrecht, "startpass_id", startpass, "id");
				addForeignKey(startpass, "person_id", person, "id");

				createForeignkeysForMainTables();
				break;
			case 9 :
				createAll();
				createForeignkeysForMainTables();
				break;
			default:
				break;
		}

		if (fromVersion != LATEST_VERSION && logger.isInfoEnabled()) {
			logger.info("Prepared Datebase update from Version " + fromVersion
					+ " to Version " + LATEST_VERSION);
		}
	}

	private void createForeignkeysForMainTables() {
		addForeignKey(contact, "person_id", person, "id");

		addForeignKey(relative, "person1", person, "id");
		addForeignKey(relative, "person2", person, "id");

		addForeignKey(persongroup, "person_id", person, "id");
		addForeignKey(persongroup, "group_id", group, "id");

		addForeignKey(attendance, "person_id", person, "id");

		addForeignKey(adress, "person_id", person, "id");
	}

	private void addForeignKey(TableDefinition foreignTable,
			String foreignColumn, TableDefinition targetTable,
			String targetColumn) {

		throwExecptionIfColumnNotContained(foreignTable, foreignColumn);
		throwExecptionIfColumnNotContained(targetTable, targetColumn);

		StringBuilder sql = new StringBuilder("ALTER TABLE ")
				.append(foreignTable.getTableName())
				.append(" ADD FOREIGN KEY (").append(foreignColumn)
				.append(") REFERENCES ").append(targetTable.getTableName())
				.append("(").append(targetColumn).append(")");
		add(sql.toString());

	}

	private void throwExecptionIfColumnNotContained(
			TableDefinition foreignTable, String foreignColumn) {

		for (Iterator<ColumnDefinition> iterator = foreignTable.getColumns()
				.iterator(); iterator.hasNext();) {
			ColumnDefinition def = iterator.next();
			if (def.getColumnName().equals(foreignColumn)) {
				return;
			}
		}

		throw new IllegalArgumentException(foreignTable
				+ " does not contain column with name " + foreignColumn);

	}

	public List<TableDefinition> getAllTables() {
		return Collections.unmodifiableList(allTables);
	}

	public TableDefinition getPerson() {
		return person;
	}

	public TableDefinition getContact() {
		return contact;
	}

	public TableDefinition getRelative() {
		return relative;
	}

	public TableDefinition getAdress() {
		return adress;
	}

	public TableDefinition getAttendance() {
		return attendance;
	}

	public TableDefinition getVersion() {
		return version;
	}

	public TableDefinition getDeletedEntries() {
		return deletedEntries;
	}

	public TableDefinition getGroup() {
		return group;
	}

	public TableDefinition getPersongroup() {
		return persongroup;
	}

	public TableDefinition getStartpass() {
		return startpass;
	}

	public TableDefinition getStartrecht() {
		return startrecht;
	}

	private void changeIdNamesInAllTables() {
		String format = "ALTER TABLE %s CHANGE _id id INTEGER NOT NULL AUTO_INCREMENT";
		for (TableDefinition def : allTables) {
			String sql = String.format(format, def.getTableName());
			statements.add(new DirectStatement(sql));
		}
	}

	private void createAttendenceUniqueConstraint() {
		ColumnDefinition[] columns = new ColumnDefinition[2];
		attendance.getColumns().forEach(col -> {
			if (PERSON_ID_FK_NAME.equals(col.getColumnName())) {
				columns[0] = col;
			} else if ("on_date".equals(col.getColumnName())) {
				columns[1] = col;
			}
		});
		statements.add(
				new AddConstraint(attendance, new UniqueConstraint(columns)));
	}

	private void addDeleteColumnStm(TableDefinition table,
			ColumnDefinition columnDefinition) {
		statements.add(new DropColumnStatement(table, columnDefinition));
	}

	private void addDeleteColumn(List<ColumnDefinition> columns) {
		columns.add(new ColumnDefinition(DataType.DATETIME, "deleted",
				" DEFAULT null"));
	}

	private void addUniqueGroupName() {
		statements.add(new DirectStatement(
				"delete n1 from groupDef n1, groupDef n2 WHERE n1._id >n2._id AND n1.name = n2.name"));
		statements.add(new DirectStatement("ALTER TABLE `groupDef` \n"
				+ "ADD UNIQUE INDEX `groupname_UNIQUE` (`name` ASC);"));
	}

	private void addUniquePersonGroup() {
		statements.add(new DirectStatement(
				"delete n1 from persongroup n1, persongroup n2 WHERE n1._id >n2._id AND n1.person_id = n2.person_id AND n1.group_id = n2.group_id;"));
		add("ALTER TABLE `persongroup` \n"
				+ "ADD UNIQUE INDEX `unique_person_group` (`person_id` ASC, `group_id` ASC);");
	}

	private void add(String sql) {
		statements.add(new DirectStatement(sql));
	}

	private void addAuthColumns(TableDefinition... defs) {

		for (TableDefinition t : defs) {
			statements.add(new AddColumnStatement(t, new ColumnDefinition(
					DataType.TEXT, "username", "DEFAULT NULL")));
			statements.add(new AddColumnStatement(t, new ColumnDefinition(
					DataType.TEXT, "password", "DEFAULT NULL")));
		}
	}

	private void addDeletedColumn(TableDefinition... defs) {

		for (TableDefinition t : defs) {
			statements.add(new AddColumnStatement(t, new ColumnDefinition(
					DataType.DATETIME, "deleted", " DEFAULT null")));
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
		person = new TableDefinition("person", dbType, columns);

		columns = createContactColumns();
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		contact = new TableDefinition("contact", dbType, columns);

		columns = createRelativeColumns();
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		relative = new TableDefinition("relative", dbType, columns);

		columns = createAdressColumns();
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		adress = new TableDefinition("adress", dbType, columns);

		columns = createAttendanceColumns();
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);

		attendance = new TableDefinition("ATTENDANCE".toLowerCase(), dbType,
				columns);

		ColumnDefinition colVersion = new ColumnDefinition(DataType.INTEGER,
				"version", "NOT NULL");
		columns = new ArrayList<ColumnDefinition>();
		columns.add(colVersion);
		version = new TableDefinition("version", dbType, columns);

		columns = createDeletedEntriesColumns();
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		deletedEntries = new TableDefinition("deleted_entries", dbType,
				columns);

		columns = createGroupColumns();
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		group = new TableDefinition("groupDef", dbType, columns);

		columns = createPersonGroupColumns();
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		persongroup = new TableDefinition("persongroup", dbType, columns);

		columns = createStartpassColumns();
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		startpass = new TableDefinition("startpaesse", dbType, columns);

		columns = createStartrechtColumns();
		addCreateChangeColumn(columns);
		addDeleteColumn(columns);
		startrecht = new TableDefinition("startpass_startrechte", dbType,
				columns);

		allTables = Arrays.asList(person, contact, relative, adress, attendance,
				version, deletedEntries, group, persongroup, startpass,
				startrecht);
	}

	private List<ColumnDefinition> createStartrechtColumns() {
		ColumnDefinition colStartpassId = new ColumnDefinition(DataType.INTEGER,
				"startpass_id", "NOT NULL");

		ColumnDefinition colStartRechtVerein = new ColumnDefinition(
				DataType.VARCHAR100, "verein_name", "NOT NULL");

		ColumnDefinition colStartRechtFachgebiet = new ColumnDefinition(
				DataType.VARCHAR25, "fachgebiet", "NOT NULL");

		ColumnDefinition colGueltigVon = new ColumnDefinition(DataType.DATETIME,
				"startrecht_beginn", "NOT NULL");

		ColumnDefinition colGueltigBis = new ColumnDefinition(DataType.DATETIME,
				"startrecht_ende", "NOT NULL");

		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(colStartpassId);
		columns.add(colStartRechtVerein);
		columns.add(colStartRechtFachgebiet);
		columns.add(colGueltigVon);
		columns.add(colGueltigBis);

		return columns;
	}

	private List<ColumnDefinition> createStartpassColumns() {

		ColumnDefinition colPersonId = new ColumnDefinition(DataType.INTEGER,
				PERSON_ID_FK_NAME, "NOT NULL");

		ColumnDefinition colStartpassNr = new ColumnDefinition(
				DataType.VARCHAR25, "startpass_nr", "NOT NULL UNIQUE");

		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(colPersonId);
		columns.add(colStartpassNr);

		return columns;
	}

	private List<ColumnDefinition> createGroupColumns() {
		ColumnDefinition colTableName = new ColumnDefinition(
				DataType.VARCHAR255, "name", "NOT NULL UNIQUE");
		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(colTableName);
		return columns;
	}

	private List<ColumnDefinition> createPersonGroupColumns() {
		ColumnDefinition colPersonId = new ColumnDefinition(DataType.INTEGER,
				PERSON_ID_FK_NAME, "NOT NULL");
		ColumnDefinition colGroupId = new ColumnDefinition(DataType.INTEGER,
				"group_id", "NOT NULL");

		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(colPersonId);
		columns.add(colGroupId);
		return columns;
	}

	private List<ColumnDefinition> createDeletedEntriesColumns() {
		ColumnDefinition colTableName = new ColumnDefinition(DataType.VARCHAR25,
				"tablename", "NOT NULL");
		ColumnDefinition colEntryId = new ColumnDefinition(DataType.INTEGER,
				"entryId", "NOT NULL");

		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(colTableName);
		columns.add(colEntryId);
		return columns;
	}

	private List<ColumnDefinition> createAttendanceColumns() {
		ColumnDefinition colOnDate = new ColumnDefinition(DataType.DATETIME,
				"on_date");
		ColumnDefinition colPersonId = new ColumnDefinition(DataType.INTEGER,
				PERSON_ID_FK_NAME, "NOT NULL");

		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(colOnDate);
		columns.add(colPersonId);
		return columns;
	}

	private List<ColumnDefinition> createAdressColumns() {
		ColumnDefinition colAdress1 = new ColumnDefinition(DataType.TEXT,
				"adress1");
		ColumnDefinition colAdress2 = new ColumnDefinition(DataType.TEXT,
				"adress2");
		ColumnDefinition colPlz = new ColumnDefinition(DataType.TEXT, "plz");
		ColumnDefinition colCity = new ColumnDefinition(DataType.TEXT, "city");
		ColumnDefinition colPersonId = new ColumnDefinition(DataType.INTEGER,
				PERSON_ID_FK_NAME, "NOT NULL");

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
		columns.add(
				new ColumnDefinition(DataType.INTEGER, "person1", "NOT NULL"));
		columns.add(
				new ColumnDefinition(DataType.INTEGER, "person2", "NOT NULL"));
		columns.add(new ColumnDefinition(DataType.TEXT, "TO_PERSON2_RELATION"));
		columns.add(new ColumnDefinition(DataType.TEXT, "TO_PERSON1_RELATION"));
		return columns;
	}

	private List<ColumnDefinition> createContactColumns() {
		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(new ColumnDefinition(DataType.TEXT, "type", "NOT NULL"));
		columns.add(new ColumnDefinition(DataType.TEXT, "value"));
		columns.add(new ColumnDefinition(DataType.INTEGER, PERSON_ID_FK_NAME,
				"NOT NULL"));
		return columns;
	}

	private List<ColumnDefinition> createPersonColumns() {

		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		columns.add(new ColumnDefinition(DataType.TEXT, "prename", "NOT NULL"));
		columns.add(new ColumnDefinition(DataType.TEXT, "surname"));
		columns.add(new ColumnDefinition(DataType.DATETIME, "birth"));
		columns.add(new ColumnDefinition(DataType.TEXT, "username",
				"DEFAULT NULL"));
		columns.add(new ColumnDefinition(DataType.TEXT, "password",
				"DEFAULT NULL"));

		return columns;
	}

	private void addCreateChangeColumn(List<ColumnDefinition> columns) {

		columns.add(new ColumnDefinition(DataType.DATETIME, "changed"));
		columns.add(new ColumnDefinition(DataType.DATETIME, "created"));
	}

	public void executeOn(Database db) throws SQLException {

		for (MyStatement stm : statements) {
			String sql = stm.getSql();

			if (logger.isDebugEnabled()) {
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
			sql = "INSERT INTO version(version) VALUES (" + LATEST_VERSION
					+ ")";
		} else {
			sql = "UPDATE version SET version=" + LATEST_VERSION;
		}

		if (fromVersion != LATEST_VERSION) {
			if (logger.isDebugEnabled()) {
				logger.debug(sql);
			}
			db.execSQL(sql);
			if (logger.isInfoEnabled()) {
				logger.info("Updated database to version " + LATEST_VERSION);
			}
		} else {
			if (logger.isInfoEnabled()) {
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

	private class AddConstraint extends CreateTableStatement {

		private UniqueConstraint constraint;

		public AddConstraint(TableDefinition def, UniqueConstraint constraint) {
			super(def);
			this.constraint = constraint;
		}

		@Override
		public String getSql() {
			return de.kreth.dbmanager.DbManager.createUniqueConstraint(def,
					constraint);
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

	private static class DirectStatement implements MyStatement {

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
