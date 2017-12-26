package de.kreth.clubhelperbackend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.dao.abstr.Dao;
import de.kreth.clubhelperbackend.pojo.Attendance;

@Repository
public class AttendanceDao extends AbstractDao<Attendance> implements Dao<Attendance> {

	private static final String columnNames[] = { "on_date", "person_id" };

	private static DaoConfig<Attendance> daoConfig = new DaoConfig<Attendance>("attendance", columnNames,
			new RowMapper(), null);

	private PreparedStatement prepStm;

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
			
			Date time = normalizeDateToDay(obj.getOnDate());
			obj.setOnDate(time);
			values.add(time);
			values.add(obj.getPersonId());
			return values;
		}

	};

	public List<Attendance> getAttendencesFor(Date day) throws SQLException {
		JdbcTemplate jdbcTemplate = getJdbcTemplate();
		if(prepStm == null) {
			prepStm = jdbcTemplate.getDataSource().getConnection().prepareStatement(SQL_QUERY_ALL + " AND on_date=?");
		}
		prepStm.setDate(1, new java.sql.Date(day.getTime()));
		PreparedStatementCreator psc = new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return prepStm;
			}
		};
		return jdbcTemplate.query(psc, daoConfig.getMapper());
	}
	
}
