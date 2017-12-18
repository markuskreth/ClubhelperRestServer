package de.kreth.clubhelperbackend.dao.abstr;

import de.kreth.clubhelperbackend.pojo.Data;

public class DaoPackageMemberAccessor {

	public static <T extends Data> String getSQL_INSERTWithoutId(AbstractDao<T> dao) {
		return dao.SQL_INSERTWithoutId;
	}

	public static <T extends Data> String getTableName(AbstractDao<T> dao) {
		return dao.tableName;
	}

	
}
