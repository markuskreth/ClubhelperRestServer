package de.kreth.clubhelperbackend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.pojo.Relative;

@Repository
public class RelativeDao extends AbstractDao<Relative> implements Dao<Relative> {

	private static final String columnNames[] 		= {"person1", "person2", "TO_PERSON1_RELATION", "TO_PERSON2_RELATION"};

	private static DaoConfig<Relative> daoConfig = new DaoConfig<Relative>("relative", columnNames, new RelativeRowMapper());
	
	public RelativeDao() {
		super(daoConfig);
	}

	private static class RelativeRowMapper implements RowMapper<Relative> {
		
		@Override
		public Relative mapRow(ResultSet rs, int rowNo) throws SQLException {
			Relative r = new Relative(
					rs.getLong("_id"), 
					rs.getLong("person1"), 
					rs.getLong("person2"), 
					rs.getString("TO_PERSON1_RELATION"), 
					rs.getString("TO_PERSON2_RELATION"), 
					rs.getDate("changed"), 
					rs.getDate("created"));
			return r;
		}

		@Override
		public Collection<Object> mapObject(Relative obj) {
			List<Object> values = new ArrayList<Object>();
			values.add(obj.getPerson1());
			values.add(obj.getPerson2());
			values.add(obj.getToPerson1Relation());
			values.add(obj.getToPerson2Relation());
			return values;
		}
	};
}
