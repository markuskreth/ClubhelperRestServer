package de.kreth.clubhelperbackend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
public class AttendanceDao extends AbstractDao<Attendance>
		implements
			Dao<Attendance> {

	static final String[] columnNames = {"on_date", "person_id"};

	private static DaoConfig<Attendance> daoConfig = new DaoConfig<Attendance>(
			"attendance", columnNames, new RowMapper(), null);

	public AttendanceDao() {
		super(daoConfig);
	}

	public static class RowMapper extends AbstractDao.ClubhelperRowMapper<Attendance> {

		public RowMapper() {
			super(Attendance.class);
		}

		@Override
		public Collection<Object> mapObject(Attendance obj, String[] columnNames) {

			Date time = normalizeDateToDay(obj.getOnDate());
			obj.setOnDate(time);
			return super.mapObject(obj, columnNames);
		}

	};

	public List<Attendance> getAttendencesFor(Date day) throws SQLException {
		JdbcTemplate jdbcTemplate = getJdbcTemplate();
		PreparedStatement prepStm = jdbcTemplate.getDataSource().getConnection()
				.prepareStatement(SQL_QUERY_ALL + " AND on_date=?");
		prepStm.setDate(1, new java.sql.Date(day.getTime()));
		PreparedStatementCreator psc = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				return prepStm;
			}
		};
		return jdbcTemplate.query(psc, daoConfig.getMapper());
	}

}
