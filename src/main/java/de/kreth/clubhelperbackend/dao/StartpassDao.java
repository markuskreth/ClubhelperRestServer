package de.kreth.clubhelperbackend.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.Startpass;
import de.kreth.clubhelperbackend.pojo.Startrecht;
import de.kreth.dbmanager.TableDefinition;

@Repository
public class StartpassDao extends AbstractDao<Startpass> {

	private static DaoConfig<Startpass> config;
	private static ClubhelperRowMapper<Startrecht> startrechtMapper = new ClubhelperRowMapper<>(Startrecht.class);
	private static PassMapper passMapper;
	
	static {
		TableDefinition tdef = dbConfig.getStartpass();
		String[] columns = tdef.getColumns().stream()
				.map(col -> col.getColumnName()).filter(c -> Arrays.asList("id", "changed", "created", "deleted").contains(c)==false).collect(Collectors.toList()).toArray(new String[0]);
		passMapper = new PassMapper();
		config = new DaoConfig<>(tdef.getTableName(), columns, passMapper, null);
	}

	public StartpassDao() {
		super(config);
		startrechtMapper.setLog(log);
	}
	
	public List<Startpass> getForPersonId(long id) {
		return getByWhere(new StringBuilder("person_id=").append(id).toString());
	}
	
	static class PassMapper extends ClubhelperRowMapper<Startpass> {

		private String sql;

		public PassMapper() {
			super(Startpass.class);
			TableDefinition startrecht = dbConfig.getStartrecht();
			sql = new StringBuilder("SELECT * FROM ").append(startrecht.getTableName())
					.append(" WHERE startpass_id=?").toString();
		}
		
		@Override
		protected Startpass appendDefault(Startpass obj, ResultSet rs) throws SQLException {
			obj = super.appendDefault(obj, rs);

			PreparedStatement selectStartrecht;

			selectStartrecht = rs.getStatement().getConnection()
					.prepareStatement(sql);
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
