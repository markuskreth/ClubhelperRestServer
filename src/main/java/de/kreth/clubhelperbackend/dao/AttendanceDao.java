package de.kreth.clubhelperbackend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.dao.abstr.Dao;
import de.kreth.clubhelperbackend.pojo.Attendance;

@Repository
public class AttendanceDao extends AbstractDao<Attendance> implements Dao<Attendance> {

	private static final String columnNames[] = { "on_date", "person_id" };

	private static DaoConfig<Attendance> daoConfig = new DaoConfig<Attendance>("attendance", columnNames,
			new RowMapper(), null);

	public AttendanceDao() {
		super(daoConfig);
	}

	private static class RowMapper implements AbstractDao.RowMapper<Attendance> {

		@Override
		public Attendance mapRow(ResultSet rs, int rowNr) throws SQLException {
			Attendance a = new Attendance(rs.getLong("_id"), rs.getTimestamp("on_date"), rs.getLong("person_id"),
					rs.getTimestamp("changed"), rs.getTimestamp("created"));
			return a;
		}

		@Override
		public Collection<Object> mapObject(Attendance obj) {
			List<Object> values = new ArrayList<Object>();
			values.add(obj.getOnDate());
			values.add(obj.getPersonId());
			return values;
		}
	};

}
