package de.kreth.clubhelperbackend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import de.kreth.clubhelperbackend.pojo.Attendance;

public class AttendanceDao extends AbstractDao<Attendance> implements Dao<Attendance> {

	private static final String attendanceFields[] 		= {"on_date", "person_id", "changed", "created"};
	private static final String attendanceValues[] 		= {"on_date", "person_id", "changed"};
	private static final String SQL_INSERT = "insert into attendance (" + String.join(", ", attendanceFields) + ") values (?,?,?,?)";
	private static final String SQL_UPDATE = "update attendance set " + String.join("=?, ", attendanceValues) + "=? WHERE _id=?";
	private static final String SQL_DELETE = "delete from attendance where _id=?";
	private static final String SQL_QUERY_BY_ID = "select " + attendanceFields + " from attendance where id=?";
	private static final String SQL_QUERY_ALL = "select * from attendance";

	public AttendanceDao() {
		super(Attendance.class, SQL_QUERY_BY_ID, SQL_INSERT, SQL_UPDATE, SQL_DELETE, SQL_QUERY_ALL);
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
	protected Object[] getUpdateValues(long id, Attendance obj) {
		Object[] values = new Object[4];
		values[0] = obj.getOnDate();
		values[1] = obj.getPersonId();
		values[2] = obj.getChanged();
		values[3] = id;
		return values;
	}
	
	@Override
	protected RowMapper<Attendance> getRowMapper() {
		return rowMapper;
	}

	private RowMapper<Attendance> rowMapper = new RowMapper<Attendance>() {
//		{"on_date", "person_id", "changed", "created"};
		@Override
		public Attendance mapRow(ResultSet rs, int rowNr) throws SQLException {
			Attendance a = new Attendance(rs.getLong("_id"), rs.getDate("on_date"), rs.getLong("person_id"), rs.getDate("changed"), rs.getDate("created"));
			return a;
		}
	};

}
