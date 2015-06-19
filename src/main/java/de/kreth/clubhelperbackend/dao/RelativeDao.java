package de.kreth.clubhelperbackend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import de.kreth.clubhelperbackend.pojo.Relative;

public class RelativeDao extends AbstractDao<Relative> implements Dao<Relative> {

	private static final String relativeFields[] 		= {"person1", "person2", "TO_PERSON1_RELATION", "TO_PERSON2_RELATION", "changed", "created"};
	private static final String relativeValues[] 		= {"person1", "person2", "TO_PERSON1_RELATION", "TO_PERSON2_RELATION", "changed"};
	private static final String SQL_INSERT = "insert into relative (" + String.join(", ", relativeFields) + ") values (?,?,?,?,?,?)";
	private static final String SQL_UPDATE = "update relative set " + String.join("=?, ", relativeValues) + "=? WHERE _id=?";
	private static final String SQL_DELETE = "delete from relative where _id=?";
	private static final String SQL_QUERY_BY_ID = "select " + relativeFields + " from relative where id=?";
	private static final String SQL_QUERY_ALL = "select * from relative";

	public RelativeDao() {
		super(Relative.class, SQL_QUERY_BY_ID, SQL_INSERT, SQL_UPDATE, SQL_DELETE, SQL_QUERY_ALL);
	}

	@Override
	protected Object[] getInsertValues(Relative obj) {
		Object[] values = new Object[6];
		values[0] = obj.getPerson1();
		values[1] = obj.getPerson2();
		values[2] = obj.getToPerson1Relation();
		values[3] = obj.getToPerson2Relation();
		values[4] = obj.getChanged();
		values[5] = obj.getCreated();
		return values;
	}

	@Override
	protected Object[] getUpdateValues(Relative obj) {
		Object[] values = new Object[6];
		values[0] = obj.getPerson1();
		values[1] = obj.getPerson2();
		values[2] = obj.getToPerson1Relation();
		values[3] = obj.getToPerson2Relation();
		values[4] = obj.getChanged();
		values[5] = obj.getId();
		return values;
	}

	@Override
	protected RowMapper<Relative> getRowMapper() {
		// TODO Auto-generated method stub
		return rowMapper;
	}

	private final RowMapper<Relative> rowMapper = new RowMapper<Relative>() {
//		private static final String relativeFields[] 		= {"person1", "person2", "TO_PERSON1_RELATION", "TO_PERSON2_RELATION", "changed", "created"};
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
	};
}
