package de.kreth.clubhelperbackend.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.kreth.clubhelperbackend.config.SqlForHsqlDb;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.Attendance;
import de.kreth.clubhelperbackend.pojo.Person;

public class AttendenceDaoTests extends AbstractDatabaseTests<Attendance> {

	@Before
	public void insertPersons() {
		long untilId = 4L;
		insertPersons(untilId);
	}

	public void insertPersons(long untilId) {
		PersonDao pDao = new PersonDao();
		pDao.setDataSource(dataSource);

		pDao.setPlatformTransactionManager(transMan);
		pDao.setDeletedEntriesDao(deletedEntriesDao);

		pDao.setSqlDialect(new SqlForHsqlDb(dataSource));
		for (long id = 1; id < untilId; id++) {
			Person p = new Person(id, "test" + id, "test" + id, null);
			pDao.insert(p);
		}
	}

	@Test
	public void testInsertAttendence() {
		Date withTime = getNow().getTime();
		Date withoutTime = getNowWithoutTime().getTime();
		Attendance toInsert = new Attendance(-1L, withTime, 1L);
		toInsert.setChanged(withTime);
		toInsert.setCreated(withTime);

		Attendance inserted = dao.insert(toInsert);
		assertNotNull(inserted);
		assertEquals(1L, inserted.getPersonId());
		assertEquals(withoutTime, inserted.getOnDate());
		assertTrue(inserted.getId() >= 0);
	}

	@Test
	public void testListForADay() throws SQLException {
		Calendar now = getNow();
		Attendance toInsert = new Attendance(-1L, now.getTime(), 1L);
		toInsert.setChanged(now.getTime());
		toInsert.setCreated(now.getTime());
		dao.insert(toInsert);
		now.add(Calendar.SECOND, 100);
		toInsert.setPersonId(2L);
		toInsert.setOnDate(now.getTime());
		toInsert.setId(-1L);
		dao.insert(toInsert);

		Date time = getNowWithoutTime().getTime();
		List<Attendance> list = ((AttendanceDao) dao).getAttendencesFor(time);
		assertEquals(2, list.size());
		list = ((AttendanceDao) dao).getAttendencesFor(time);
		assertEquals(2, list.size());
	}

	private Calendar getNowWithoutTime() {
		Calendar cal = getNow();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		return cal;
	}

	private Calendar getNow() {
		return new GregorianCalendar(2017, Calendar.DECEMBER, 18, 11, 11, 11);
	}

	@Override
	public AbstractDao<Attendance> initDao() {
		return new AttendanceDao();
	}

}
