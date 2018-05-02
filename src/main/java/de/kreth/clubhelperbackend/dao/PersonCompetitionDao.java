package de.kreth.clubhelperbackend.dao;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.PersonCompetition;

@Repository
public class PersonCompetitionDao extends AbstractDao<PersonCompetition> {

	public PersonCompetitionDao() {
		super(createConfig());
	}

	private static DaoConfig<PersonCompetition> createConfig() {
		String[] columnNames = {"person_id", "event_id", "calendar_id",
				"participation", "routine", "comment"};
		String[] orderBy = {"calendar_id", "person_id"};
		return new DaoConfig<>("personcompetition", columnNames,
				new RowMapper<>(PersonCompetition.class), orderBy);
	}

}
