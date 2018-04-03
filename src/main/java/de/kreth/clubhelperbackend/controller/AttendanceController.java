package de.kreth.clubhelperbackend.controller;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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

	@RequestMapping(value = "/on", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public List<Attendance> getAttendencesOn(@RequestBody(required=false) Date date)
			throws SQLException {

		if(date == null) {
			date = new Date();
		}
		AttendanceDao tmpDao = (AttendanceDao) dao;
		
		return tmpDao.getAttendencesFor(date);
	}

	@RequestMapping(value = "/for/{id}", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Attendance post(@PathVariable("id") Long id) {
		Attendance att = new Attendance(-1L, new Date(), id);
		return post(att);
	}
}
