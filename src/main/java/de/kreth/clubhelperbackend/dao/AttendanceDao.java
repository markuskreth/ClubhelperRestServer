package de.kreth.clubhelperbackend.dao;

import java.util.Date;
import java.util.List;

import de.kreth.clubhelperbackend.pojo.Attendance;

public class AttendanceDao extends AbstractDao implements Dao<Attendance> {

//	private static final String attendanceAllFields[] 	= {"_id", "prename", "surname", "type", "birth", "changed", "created"};
	private static final String attendanceFields[] 		= {"on_date", "person_id", "changed", "created"};
	private static final String attendanceValues[] 		= {"on_date", "person_id", "changed"};
	private static final String SQL_INSERT = "insert into attendance (" + String.join(", ", attendanceFields) + ") values (?,?,?,?)";
	private static final String SQL_UPDATE = "update attendance set " + String.join("=?, ", attendanceValues) + "=? WHERE _id=?";
	private static final String SQL_DELETE = "delete from attendance where _id=?";
	private static final String SQL_QUERY_BY_ID = "select " + attendanceFields + " from attendance where id=?";
	private static final String SQL_QUERY_ALL = "select * from attendance";
	
	@Override
	public Attendance getById(long id) {
		return getJdbcTemplate().queryForObject(SQL_QUERY_BY_ID, Attendance.class, id);
	}

	@Override
	public List<Attendance> getAll() {
		return getJdbcTemplate().queryForList(SQL_QUERY_ALL, Attendance.class);
	}

	@Override
	public Attendance insert(Attendance obj) {
		Date now = new Date();
		obj = new Attendance(null, obj.getOnDate(), obj.getPersonId(), now, now);
		int inserted = getJdbcTemplate().update(SQL_INSERT, 
				obj.getOnDate(),
				obj.getPersonId(),
				obj.getChanged(),
				obj.getCreated());

		if(inserted == 1) {
			obj.setId(sqlDialect.queryForIdentity());
		} else
			obj = null;
		return obj;
	}

	@Override
	public boolean update(Attendance obj) {
		int update = getJdbcTemplate().update(SQL_UPDATE,  
				obj.getOnDate(),
				obj.getPersonId(),
				obj.getChanged());
		return update==1;
	}

	@Override
	public boolean delete(Attendance obj) {
		int update = getJdbcTemplate().update(SQL_DELETE, obj.getId());
		return update == 1;
	}

}
