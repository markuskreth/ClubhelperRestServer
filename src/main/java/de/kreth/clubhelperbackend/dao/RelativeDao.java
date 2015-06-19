package de.kreth.clubhelperbackend.dao;

import java.util.List;

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
		super(Relative.class);
	}

	@Override
	public Relative getById(long id) {
		return super.getById(SQL_QUERY_BY_ID, id);
	}

	@Override
	public List<Relative> getAll() {
		return super.getAll(SQL_QUERY_ALL);
	}

	@Override
	public Relative insert(Relative obj) {
		return super.insert(obj, SQL_INSERT);
	}

	@Override
	public boolean update(Relative obj) {
		return super.update(obj, SQL_UPDATE);
	}

	@Override
	public boolean delete(Relative obj) {
		return super.delete(obj, SQL_DELETE);
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
		Object[] values = new Object[5];
		values[0] = obj.getPerson1();
		values[1] = obj.getPerson2();
		values[2] = obj.getToPerson1Relation();
		values[3] = obj.getToPerson2Relation();
		values[4] = obj.getChanged();
		return values;
	}

}
