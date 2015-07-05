package de.kreth.clubhelperbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.kreth.clubhelperbackend.dao.Dao;
import de.kreth.clubhelperbackend.pojo.Attendance;

@Controller
@RequestMapping("/attendance")
public class AttendanceController extends AbstractController<Attendance> {

	@Autowired
	public AttendanceController(Dao<Attendance> attendanceDao) {
		super(attendanceDao, Attendance.class);
	}

}
