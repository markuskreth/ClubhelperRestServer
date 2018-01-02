package de.kreth.clubhelperbackend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.PersonCompetition;

public class PersonCompetitionDao extends AbstractDao<PersonCompetition> {

	public PersonCompetitionDao() {
		super(createConfig());
	}

	private static DaoConfig<PersonCompetition> createConfig() {
		String[] columnNames = {"person_id", "competition_id"
				, "participation", "routine", "comment"};
		Mapper mapper = new Mapper();
		String[] orderBy = {"competition_id", "person_id"};
		return new DaoConfig<>("personcompetition", columnNames, mapper, orderBy);
	}
	
	private static class Mapper implements AbstractDao.RowMapper<PersonCompetition> {

		@Override
		public PersonCompetition mapRow(ResultSet rs, int rowNr) throws SQLException {
			
			return new PersonCompetition(rs.getLong("id"), 
					rs.getLong("person_id"), rs.getString("competition_id")
					, rs.getString("participation"), rs.getString("routine")
					, rs.getString("comment"), rs.getTimestamp("changed"),
					rs.getTimestamp("created"));
		}

		@Override
		public Collection<Object> mapObject(PersonCompetition obj) {
			List<Object> values = new ArrayList<Object>();
			values.add(obj.getPersonId());
			values.add(obj.getCalenderId());
			values.add(obj.getParticipation());
			values.add(obj.getRoutine());
			values.add(obj.getComment());
			return values;
		}
		
	}
}