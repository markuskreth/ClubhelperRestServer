package de.kreth.clubhelperbackend.dao;

import java.util.Date;
import java.util.List;

import de.kreth.clubhelperbackend.pojo.Relative;

public class RelativeDao extends AbstractDao implements Dao<Relative> {

//	private static final String relativeAllFields[] 	= {"_id", "prename", "surname", "type", "birth", "changed", "created"};
	private static final String relativeFields[] 		= {"person1", "person2", "TO_PERSON1_RELATION", "TO_PERSON2_RELATION", "changed", "created"};
	private static final String relativeValues[] 		= {"person1", "person2", "TO_PERSON1_RELATION", "TO_PERSON2_RELATION", "changed"};
	private static final String SQL_INSERT = "insert into relative (" + String.join(", ", relativeFields) + ") values (?,?,?,?,?,?)";
	private static final String SQL_UPDATE = "update relative set " + String.join("=?, ", relativeValues) + "=? WHERE _id=?";
	private static final String SQL_DELETE = "delete from relative where _id=?";
	private static final String SQL_QUERY_BY_ID = "select " + relativeFields + " from relative where id=?";
	private static final String SQL_QUERY_ALL = "select * from relative";
	
	@Override
	public Relative getById(long id) {
		return getJdbcTemplate().queryForObject(SQL_QUERY_BY_ID, Relative.class, id);
	}

	@Override
	public List<Relative> getAll() {
		return getJdbcTemplate().queryForList(SQL_QUERY_ALL, Relative.class);
	}

	@Override
	public Relative insert(Relative obj) {
		Date now = new Date();
		obj = new Relative(null, obj.getPerson1(), obj.getPerson2(), obj.getToPerson1Relation(), obj.getToPerson2Relation(), now, now);
		int inserted = getJdbcTemplate().update(SQL_INSERT, 
				obj.getPerson1(),
				obj.getPerson2(),
				obj.getToPerson1Relation(),
				obj.getToPerson2Relation(),
				obj.getChanged(),
				obj.getCreated());

		if(inserted == 1) {
			obj.setId(sqlDialect.queryForIdentity());
		} else
			obj = null;
		return obj;
	}

	@Override
	public boolean update(Relative obj) {
		int update = getJdbcTemplate().update(SQL_UPDATE, 
				obj.getPerson1(),
				obj.getPerson2(),
				obj.getToPerson1Relation(),
				obj.getToPerson2Relation(),
				obj.getChanged());
		return update==1;
	}

	@Override
	public boolean delete(Relative obj) {
		int update = getJdbcTemplate().update(SQL_DELETE, obj.getId());
		return update == 1;
	}

}
