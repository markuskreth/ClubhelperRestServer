package de.kreth.clubhelperbackend.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.jdbc.core.RowMapper;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.Startpass;

public class StartpassDaoTest extends AbstractDaoTest<Startpass> {

	PreparedStatement selectStartpaesse;
	
	@Before
	public void setupStatement() throws SQLException {
		selectStartpaesse = mock(PreparedStatement.class);
		when(connection.prepareStatement(startsWith("SELECT * FROM startpass_startrechte"))).thenReturn(selectStartpaesse);
	}
	
	@Test
	public void testGetForPersonId() {
		List<Startpass> list = new ArrayList<>();
		Startpass pass = new Startpass();
		pass.setId(1L);
		pass.setPersonId(1L);

		list.add(pass);

		when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<Startpass>>any())).thenReturn(list);
		StartpassDao startpassDao = (StartpassDao) dao;
		List<Startpass> startpaese = startpassDao.getForPersonId(1L);
		assertNotNull(startpaese);
		assertEquals(1, startpaese.size());
	}

	@Override
	protected AbstractDao<Startpass> configureDao() {
		return new StartpassDao();
	}

}
