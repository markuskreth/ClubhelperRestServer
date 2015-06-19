package de.kreth.clubhelperbackend.dao;

import java.util.Date;
import java.util.List;

import de.kreth.clubhelperbackend.pojo.Adress;

public class AdressDao extends AbstractDao implements Dao<Adress>{

//	private static final String adressAllFields[] 	= {"_id", "prename", "surname", "type", "birth", "changed", "created"};
	private static final String adressFields[] 		= {"adress1", "adress2", "plz", "city", "person_id", "changed", "created"};
	private static final String adressValues[] 		= {"adress1", "adress2", "plz", "city", "person_id", "changed"};
	private static final String SQL_INSERT_ADRESS = "insert into adress (" + String.join(", ", adressFields) + ") values (?,?,?,?,?,?,?)";
	private static final String SQL_UPDATE_ADRESS = "update adress set " + String.join("=?, ", adressValues) + "=? WHERE _id=?";
	private static final String SQL_DELETE_ADRESS = "delete from adress where _id=?";
	private static final String SQL_QUERY_ADRESS_BY_ID = "select " + adressFields + " from adress where id=?";
	private static final String SQL_QUERY_ALL_ADRESS = "select * from adress";
	
	@Override
	public Adress getById(long id) {
		return getJdbcTemplate().queryForObject(SQL_QUERY_ADRESS_BY_ID, Adress.class, id);
	}

	@Override
	public List<Adress> getAll() {
		return getJdbcTemplate().queryForList(SQL_QUERY_ALL_ADRESS, Adress.class);
	}

	@Override
	public Adress insert(Adress obj) {
		Date now = new Date();
		obj = new Adress(null, obj.getAdress1(), obj.getAdress2(), obj.getPlz(), obj.getCity(), obj.getPersonId(), now , now);
		int inserted = getJdbcTemplate().update(SQL_INSERT_ADRESS, 
				obj.getAdress1(),
				obj.getAdress2(),
				obj.getPlz(),
				obj.getCity(),
				obj.getPersonId(),
				obj.getChanged(),
				obj.getCreated());
		if(inserted == 1) {
			obj.setId(sqlDialect.queryForIdentity());
		} else
			obj = null;
		return obj;
	}

	@Override
	public boolean update(Adress obj) {
		int update = getJdbcTemplate().update(SQL_UPDATE_ADRESS, 
				obj.getAdress1(),
				obj.getAdress2(),
				obj.getPlz(),
				obj.getCity(),
				obj.getPersonId(),
				obj.getChanged());

		return update==1;
	}

	@Override
	public boolean delete(Adress obj) {
		int update = getJdbcTemplate().update(SQL_DELETE_ADRESS, obj.getId());
		return update == 1;
	}

}
