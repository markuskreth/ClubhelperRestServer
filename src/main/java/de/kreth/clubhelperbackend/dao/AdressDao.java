package de.kreth.clubhelperbackend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import de.kreth.clubhelperbackend.pojo.Adress;

public class AdressDao extends AbstractDao<Adress> {

	private static final String adressFields[] 		= {"adress1", "adress2", "plz", "city", "person_id", "changed", "created"};
	private static final String adressValues[] 		= {"adress1", "adress2", "plz", "city", "person_id", "changed"};
	private static final String SQL_INSERT = "insert into adress (" + String.join(", ", adressFields) + ") values (?,?,?,?,?,?,?)";
	private static final String SQL_UPDATE = "update adress set " + String.join("=?, ", adressValues) + "=? WHERE _id=?";
	private static final String SQL_DELETE = "delete from adress where _id=?";
	private static final String SQL_QUERY_BY_ID = "select " + adressFields + " from adress where id=?";
	private static final String SQL_QUERY_ALL = "select * from adress";
	
	public AdressDao() {
		super(Adress.class, SQL_QUERY_BY_ID, SQL_INSERT, SQL_UPDATE, SQL_DELETE, SQL_QUERY_ALL);
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
	protected Object[] getUpdateValues(long id, Adress obj) {
		Object[] values = new Object[7];
		values[0] = obj.getAdress1();
		values[1] = obj.getAdress2();
		values[2] = obj.getPlz();
		values[3] = obj.getCity();
		values[4] = obj.getPersonId();
		values[5] = obj.getChanged();
		values[6] = id;
		return values;
	}
	
	@Override
	protected RowMapper<Adress> getRowMapper() {
		return rowMapper;
	}

	private final RowMapper<Adress> rowMapper = new RowMapper<Adress>() {
 
		@Override
		public Adress mapRow(ResultSet rs, int rowNr) throws SQLException {
			Adress a = new Adress(rs.getLong("_id"), rs.getString("adress1"), rs.getString("adress2"), rs.getString("plz"), rs.getString("city"), rs.getLong("person_id"), rs.getDate("changed"), rs.getDate("created"));
			return a;
		}
	};

}
