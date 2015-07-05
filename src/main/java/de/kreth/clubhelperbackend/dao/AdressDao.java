package de.kreth.clubhelperbackend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.pojo.Adress;

@Repository
public class AdressDao extends AbstractDao<Adress> {

	private static final String columnNames[] 		= {"adress1", "adress2", "plz", "city", "person_id"};
	
	private static DaoConfig<Adress> daoConfig = new DaoConfig<Adress>("adress", columnNames, new AdressRowMapper());
	
	public AdressDao() {
		super(daoConfig);
	}

	private static class AdressRowMapper implements RowMapper<Adress> {
 
		@Override
		public Adress mapRow(ResultSet rs, int rowNr) throws SQLException {
			Adress a = new Adress(rs.getLong("_id"), rs.getString("adress1"), rs.getString("adress2"), rs.getString("plz"), rs.getString("city"), rs.getLong("person_id"), rs.getDate("changed"), rs.getDate("created"));
			return a;
		}

		@Override
		public Collection<Object> mapObject(Adress obj) {
			List<Object> values = new ArrayList<Object>();
			values.add(obj.getAdress1());
			values.add(obj.getAdress2());
			values.add(obj.getPlz());
			values.add(obj.getCity());
			values.add(obj.getPersonId());
			return values;
		}
	};

}
