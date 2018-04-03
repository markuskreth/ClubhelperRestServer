package de.kreth.clubhelperbackend.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import de.kreth.clubhelperbackend.dao.AbstractDaoTest;
import de.kreth.clubhelperbackend.dao.AttendanceDao;
import de.kreth.clubhelperbackend.dao.ObjectArrayMatcher;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.dao.abstr.DaoPackageMemberAccessor;
import de.kreth.clubhelperbackend.pojo.Attendance;

public class AttendenceTests extends AbstractDaoTest<Attendance> {

	private static final int NOW_SECOND = 15;
	private static final int NOW_MINUTE = 10;
	private static final int NOW_HOUR = 17;
	private static final int NOW_DAY = 18;
	private static final int NOW_YYYY = 2017;
	private AttendanceController controller;

	@Before
	public void initController() {
		controller = new AttendanceController(dao);
	}

	@Test
	public void createAttendence() {
		when(jdbcTemplate.update(Matchers.anyString(), Matchers.argThat(new ObjectArrayMatcher(null)))).thenReturn(1);
		Date now = now();
		Attendance toCreate = new Attendance(-1L, now, 1L);
		Attendance created = controller.post(toCreate);
		assertNotNull(created);
		String sql = DaoPackageMemberAccessor.getSQL_INSERTWithoutId(dao);
		verify(jdbcTemplate).update(Matchers.eq(sql), Matchers.argThat(new ObjectArrayMatcher(null)));
		assertNotNull(created.getChanged());
		assertNotNull(created.getCreated());
		assertEquals(nowWithoutTime(), created.getOnDate());
		assertEquals(1L, created.getPersonId());
		assertEquals(objectId, created.getId());

	}

	private Date now() {
		return new GregorianCalendar(NOW_YYYY, Calendar.DECEMBER, NOW_DAY, NOW_HOUR, NOW_MINUTE, NOW_SECOND).getTime();
	}

	private Date nowWithoutTime() {
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(now());
		gregorianCalendar.set(Calendar.MILLISECOND, 0);
		gregorianCalendar.set(Calendar.SECOND, 0);
		gregorianCalendar.set(Calendar.MINUTE, 0);
		gregorianCalendar.set(Calendar.HOUR_OF_DAY, 0);
		return gregorianCalendar.getTime();
	}

	@Override
	protected AbstractDao<Attendance> configureDao() {
		dao = new AttendanceDao();
		return dao;
	}

}
