package de.kreth.clubhelperbackend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.Startpass;
import de.kreth.clubhelperbackend.pojo.Startrecht;
import de.kreth.dbmanager.TableDefinition;

@Repository
public class StartpassDao extends AbstractDao<Startpass> {

	private static DaoConfig<Startpass> config;
	private static RowMapper<Startrecht> startrechtMapper = new RowMapper<>(Startrecht.class);
	private static PassMapper passMapper;
	
	static {
		TableDefinition tdef = dbConfig.getStartpass();
		String[] columns = tdef.getColumns().stream()
				.map(col -> col.getColumnName()).collect(Collectors.toList()).toArray(new String[0]);
		passMapper = new PassMapper();
		config = new DaoConfig<>(tdef.getTableName(), columns, passMapper, null);
	}

	public StartpassDao() {
		super(config);
	}
	
	public List<Startpass> getForPersonId(long id) {
		if(passMapper.selectStartrecht == null) {
			startrechtMapper.setLog(log);
			TableDefinition startrecht = dbConfig.getStartrecht();
			
			try (Connection connection = getConnection()) {
				
				String sql = new StringBuilder("SELECT * FROM ").append(startrecht.getTableName())
						.append(" WHERE startpass_id=?").toString();
				PreparedStatement selectStartrecht = connection
						.prepareStatement(sql);
				passMapper.selectStartrecht = selectStartrecht;
			} catch (CannotGetJdbcConnectionException | SQLException e) {
				throw new DataSourceLookupFailureException("Error creating Prepared Statement for " + startrecht.getTableName(), e);
			}
		}
		return getByWhere(new StringBuilder("person_id=").append(id).toString());
	}
	
	static class PassMapper extends RowMapper<Startpass> {

		private PreparedStatement selectStartrecht;

		public PassMapper() {
			super(Startpass.class);
		}
		
		@Override
		protected Startpass appendDefault(Startpass obj, ResultSet rs) throws SQLException {
			obj = super.appendDefault(obj, rs);
			selectStartrecht.setLong(1, obj.getId());
			ResultSet rechtRs = selectStartrecht.executeQuery();
			int index = 1;

			List<Startrecht> startrechte =new ArrayList<>();
			while (rechtRs.next()) {
				startrechte.add(startrechtMapper.mapRow(rechtRs, index++));
			}
			
			obj.setStartrechte(startrechte);
			return obj;
		}

	}
	
}
