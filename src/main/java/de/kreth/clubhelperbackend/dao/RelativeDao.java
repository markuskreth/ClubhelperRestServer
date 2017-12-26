package de.kreth.clubhelperbackend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.dao.abstr.Dao;
import de.kreth.clubhelperbackend.pojo.Relative;

@Repository
public class RelativeDao extends AbstractDao<Relative>
		implements
			Dao<Relative> {

	private static final String columnNames[] = {"person1", "person2",
			"TO_PERSON1_RELATION", "TO_PERSON2_RELATION"};

	private static final DaoConfig<Relative> daoConfig = new DaoConfig<Relative>(
			"relative", columnNames, new RelativeRowMapper(), null);

	public RelativeDao() {
		super(daoConfig);
	}

	private static class RelativeRowMapper implements RowMapper<Relative> {

		@Override
		public Relative mapRow(ResultSet rs, int rowNo) throws SQLException {
			Relative r = new ToStringRelative(rs.getLong("id"),
					rs.getLong("person1"), rs.getLong("person2"),
					rs.getString("TO_PERSON1_RELATION"),
					rs.getString("TO_PERSON2_RELATION"),
					rs.getTimestamp("changed"), rs.getTimestamp("created"));
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

		private class ToStringRelative extends Relative {

			private static final long serialVersionUID = 4772529931953029461L;

			public ToStringRelative(long id, long person1Id, long person2Id,
					String toPerson1Relation, String toPerson2Relation,
					Date changedDate, Date createdDate) {
				super(id, person1Id, person2Id, toPerson1Relation,
						toPerson2Relation, changedDate, createdDate);
			}

			@Override
			public String toString() {
				return getId() + ": " + getPerson2() + " "
						+ getToPerson1Relation() + " --> " + getPerson1();
			}
		}
	};

}
