package de.kreth.clubhelperbackend.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.kreth.clubhelperbackend.config.DatabaseConfiguration;
import de.kreth.clubhelperbackend.config.SqlForHsqlDb;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.Person;
import de.kreth.dbmanager.TableDefinition;

public class MemoryHsqlDatabaseTest extends AbstractDatabaseTests<Person> {

	@Test
	public void allTablesExist() {
		SqlForHsqlDb sqlDb = new SqlForHsqlDb(dataSource);
		DatabaseConfiguration config = new DatabaseConfiguration(0);
		List<TableDefinition> tableDefs = config.getAllTables();
		for (TableDefinition def : tableDefs) {
			assertTrue("Table not found: " + def,
					sqlDb.tableExists(def.getTableName()));
		}
	}

	@Override
	public AbstractDao<Person> initDao() {
		return new PersonDao();
	}

}
