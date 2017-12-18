package de.kreth.clubhelperbackend.controller;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.kreth.clubhelperbackend.controller.abstr.AbstractController;
import de.kreth.clubhelperbackend.dao.AttendanceDao;
import de.kreth.clubhelperbackend.dao.abstr.Dao;
import de.kreth.clubhelperbackend.pojo.Attendance;

@Controller
@RequestMapping("/attendance")
public class AttendanceController extends AbstractController<Attendance> {

	@Autowired
	public AttendanceController(Dao<Attendance> attendanceDao) {
		super(attendanceDao, Attendance.class);
	}

	public List<Attendance> getAttendencesOn(Date date) throws SQLException {
		AttendanceDao tmpDao = (AttendanceDao) dao;
		return tmpDao.getAttendencesFor(date);
	}

}
