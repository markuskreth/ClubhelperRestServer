package de.kreth.clubhelperbackend.dao;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.Adress;

@Repository
public class AdressDao extends AbstractDao<Adress> {

	public static final String ADRESS1 = "adress1";
	public static final String ADRESS2 = "adress2";
	public static final String PLZ = "plz";
	public static final String CITY = "city";
	public static final String PERSON_ID = "person_id";

	static final String columnNames[] = {ADRESS1, ADRESS2, PLZ,
			CITY, PERSON_ID};

	private static DaoConfig<Adress> daoConfig = new DaoConfig<Adress>("adress",
			columnNames, new RowMapper<Adress>(AdressWrapper.class), null);

	public AdressDao() {
		super(daoConfig);
	}

	public static class AdressWrapper extends Adress {

		private static final long serialVersionUID = -1443368978470854581L;

		@Override
		public String toString() {
			StringBuilder bld = new StringBuilder();
			bld.append(getAdress1()).append(", ").append(getAdress2())
					.append(", ").append(getPlz()).append(" ")
					.append(getCity());
			return bld.toString();
		}
	}
}
