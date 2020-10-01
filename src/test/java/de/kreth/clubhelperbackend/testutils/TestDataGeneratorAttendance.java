package de.kreth.clubhelperbackend.testutils;

import java.util.Date;

import de.kreth.clubhelperbackend.pojo.Attendance;

public class TestDataGeneratorAttendance extends AbstractTestDataGenerator<Attendance> {

	public static final long attendanceDate = 1538938500000L;

	public TestDataGeneratorAttendance() {
		super(Attendance.class);
	}

	@Override
	public Attendance instance(Class<Attendance> pojoClass) {
		Attendance att = new Attendance(null, new Date(attendanceDate), testPerson().getId());
		return att;
	}

	@Override
	public void change(Attendance obj) {
		obj.setOnDate(new Date(1538784000000L));
	}

}
