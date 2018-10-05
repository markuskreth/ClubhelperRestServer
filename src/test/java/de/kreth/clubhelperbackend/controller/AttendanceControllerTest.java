package de.kreth.clubhelperbackend.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.kreth.clubhelperbackend.dao.AttendanceDao;
import de.kreth.clubhelperbackend.pojo.Attendance;

public class AttendanceControllerTest {

	@Mock
	public AttendanceDao dao;
	AttendanceController controller;
	private List<Attendance> resultList;
	
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new AttendanceController(dao);
		resultList = new ArrayList<>();
		when(dao.getAttendencesFor(any(Date.class))).thenReturn(resultList);
		when(dao.insert(any(Attendance.class))).thenReturn(new Attendance(-100L, null, -200L));
	}

	@Test
	public void testGetAttendencesOn() throws SQLException {
		resultList.add(new Attendance(11L, null, 7L));
		List<Attendance> result = controller.getAttendencesOn(null);
		assertEquals(resultList, result);
	}

	@Test
	public void testPostLong() {
		
		Attendance att = controller.post(3L);
		ArgumentCaptor<Attendance> arg = ArgumentCaptor.forClass(Attendance.class);
		verify(dao).insert(arg.capture());
		assertEquals(-100L, att.getId().longValue());
		assertEquals(-1, arg.getValue().getId().longValue());
		assertEquals(3l, arg.getValue().getPersonId());
		assertNotNull(arg.getValue().getOnDate());
	}

}
