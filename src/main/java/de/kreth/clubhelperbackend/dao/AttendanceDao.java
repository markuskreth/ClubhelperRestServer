package de.kreth.clubhelperbackend.dao;

import java.util.List;

import de.kreth.clubhelperbackend.pojo.Attendance;

public class AttendanceDao extends AbstractDao<Attendance> implements Dao<Attendance> {

public AttendanceDao() {
		super(Attendance.class);
	}

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
		return super.getById(SQL_QUERY_BY_ID, id);
	}

	@Override
	public List<Attendance> getAll() {
		return super.getAll(SQL_QUERY_ALL);
	}

	@Override
	public Attendance insert(Attendance obj) {
		return super.insert(obj, SQL_INSERT);
	}

	@Override
	public boolean update(Attendance obj) {
		return super.update(obj, SQL_UPDATE);
	}

	@Override
	public boolean delete(Attendance obj) {
		return super.delete(obj, SQL_DELETE);
	}

	@Override
	protected Object[] getInsertValues(Attendance obj) {
		Object[] values = new Object[4];
		values[0] = obj.getOnDate();
		values[1] = obj.getPersonId();
		values[2] = obj.getChanged();
		values[3] = obj.getCreated();
		return values;
	}

	@Override
	protected Object[] getUpdateValues(Attendance obj) {
		Object[] values = new Object[3];
		values[0] = obj.getOnDate();
		values[1] = obj.getPersonId();
		values[2] = obj.getChanged();
		return values;
	}

}
