package de.kreth.clubhelperbackend.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.PersonCompetition;

public class PersonCompetitionDaoTest extends AbstractDatabaseTests<PersonCompetition> {

	@Override
	public AbstractDao<PersonCompetition> initDao() {
		return new PersonCompetitionDao();
	}

	@Test
	public void checkTableExists() {
		assertNotNull(dao);
		assertNotNull(dao.getDataSource());
		assertNotNull(dao.getJdbcTemplate());
		assertNotNull(dao.getSqlDialect());
		List<PersonCompetition> all = dao.getAll();
		assertNotNull(all);
		assertTrue(all.isEmpty());
	}
	
	@Test
	public void createAndRead() {
		PersonCompetition obj = new PersonCompetition(-1L, 1L, "googleCalenderId", "participation", "routine", "comment", null, null);
		PersonCompetition result = dao.insert(obj);
		obj.setId(result.getId());
		obj.setChanged(result.getChanged());
		obj.setCreated(result.getCreated());
		assertEquals(obj, result);
	}
}
