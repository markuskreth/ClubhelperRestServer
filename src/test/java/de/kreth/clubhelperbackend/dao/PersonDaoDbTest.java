package de.kreth.clubhelperbackend.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import de.kreth.clubhelperbackend.config.SqlForMysql;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.Person;
import de.kreth.clubhelperbackend.testutils.TestDataPerson;

public class PersonDaoDbTest extends AbstractMysqlDatabaseTests<Person> {

	@Test
	public void insertDelete() throws Exception {
		Person p1 = TestDataPerson.INSTANCE.person;
		Person p2 = (Person) p1.clone();

		assertEquals(p1, p2);
		p2.setId(p1.getId() + 1);
		p2.setSurname("AAAAA");
		p2.setPrename("AAAAAA");

		Person p3 = (Person) p1.clone();
		p3.setId(p1.getId() + 2);
		p3.setPrename("AAAAAAA");
		p3.setSurname("ZZZZZZZZZ");

		dao.insert(p3);
		dao.insert(p2);
		dao.insert(p1);

		assertEquals(3, dao.getAll().size());
		dao.delete(p3);
		dao.delete(p2);
		assertEquals(1, dao.getAll().size());
	}

	@Test
	public void personListIsSorted() throws Exception {

		dbCheck.checkDb();
		PersonDao dao = new PersonDao();
		dao.setDataSource(dataSource);
		dao.setPlatformTransactionManager(new DataSourceTransactionManager(dataSource));

		dao.setSqlDialect(new SqlForMysql(dataSource));
		Person p1 = TestDataPerson.INSTANCE.person;
		Person p2 = (Person) p1.clone();

		assertEquals(p1, p2);
		p2.setId(p1.getId() + 1);
		p2.setSurname("AAAAA");
		p2.setPrename("AAAAAA");

		Person p3 = (Person) p1.clone();
		p3.setId(p1.getId() + 2);
		p3.setPrename("AAAAAAA");
		p3.setSurname("ZZZZZZZZZ");

		dao.insert(p3);
		dao.insert(p2);
		dao.insert(p1);

		List<Person> all = dao.getAll();

		assertEquals(p2.getId(), all.get(0).getId());
		assertEquals(p1.getId(), all.get(1).getId());
		assertEquals(p3.getId(), all.get(2).getId());
	}

	@Override
	public AbstractDao<Person> initDao() {
		return new PersonDao();
	}
}
