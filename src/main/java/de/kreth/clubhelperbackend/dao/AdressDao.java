package de.kreth.clubhelperbackend.dao;

import java.util.List;

import de.kreth.clubhelperbackend.pojo.Adress;

public class AdressDao extends AbstractDao<Adress> implements Dao<Adress>{

	private static final String adressFields[] 		= {"adress1", "adress2", "plz", "city", "person_id", "changed", "created"};
	private static final String adressValues[] 		= {"adress1", "adress2", "plz", "city", "person_id", "changed"};
	private static final String SQL_INSERT = "insert into adress (" + String.join(", ", adressFields) + ") values (?,?,?,?,?,?,?)";
	private static final String SQL_UPDATE = "update adress set " + String.join("=?, ", adressValues) + "=? WHERE _id=?";
	private static final String SQL_DELETE = "delete from adress where _id=?";
	private static final String SQL_QUERY_BY_ID = "select " + adressFields + " from adress where id=?";
	private static final String SQL_QUERY_ALL = "select * from adress";
	
	public AdressDao() {
		super(Adress.class);
	}

	@Override
	public Adress getById(long id) {
		return super.getById(SQL_QUERY_BY_ID, id);
	}

	@Override
	public List<Adress> getAll() {
		return super.getAll(SQL_QUERY_ALL);
	}

	@Override
	public Adress insert(Adress obj) {
		return insert(obj, SQL_INSERT);
	}

	@Override
	public boolean update(Adress obj) {
		return super.update(obj, SQL_UPDATE);
	}

	@Override
	public boolean delete(Adress obj) {
		return super.delete(obj, SQL_DELETE);
	}

	@Override
	protected Object[] getInsertValues(Adress obj) {
		Object[] values = new Object[7];
		values[0] = obj.getAdress1();
		values[1] = obj.getAdress2();
		values[2] = obj.getPlz();
		values[3] = obj.getCity();
		values[4] = obj.getPersonId();
		values[5] = obj.getChanged();
		values[6] = obj.getCreated();
		return values;
	}

	@Override
	protected Object[] getUpdateValues(Adress obj) {
		Object[] values = new Object[6];
		values[0] = obj.getAdress1();
		values[1] = obj.getAdress2();
		values[2] = obj.getPlz();
		values[3] = obj.getCity();
		values[4] = obj.getPersonId();
		values[5] = obj.getChanged();
		return values;
	}

}
