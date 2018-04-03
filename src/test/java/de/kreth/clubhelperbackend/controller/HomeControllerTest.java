package de.kreth.clubhelperbackend.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.context.ApplicationContext;

import de.kreth.testutils.sql.TestResultset;

public class HomeControllerTest {

	private ApplicationContext context;
	private HomeController controller;

	@Before
	public void init() {
		context = mock(ApplicationContext.class);
		controller = new HomeController();
		controller.setApplicationContext(context);
	}
	
	@Test
	public void test() throws SQLException {
		TestResultset rs = new TestResultset();
		Map<String, Object> values = new HashMap<>();
		values.put("prename", "Prename1");
		values.put("surname", "Surname1");
		values.put("on_date", new java.sql.Date(new GregorianCalendar(2018, Calendar.JANUARY, 1).getTime().getTime()));
		rs.add(values);
		values = new HashMap<>();
		values.put("prename", "Prename2");
		values.put("surname", "Surname2");
		values.put("on_date", new java.sql.Date(new GregorianCalendar(2018, Calendar.JANUARY, 2).getTime().getTime()));
		rs.add(values);
		
		DataSource ds = mock(DataSource.class);
		when(context.getBean(DataSource.class)).thenReturn(ds);
		Connection conn = mock(Connection.class);
		when(ds.getConnection()).thenReturn(conn);
		
		Statement stm = mock(Statement.class);
		when(conn.createStatement()).thenReturn(stm);
		when(stm.executeQuery(Matchers.anyString())).thenReturn(rs);
		String csv = controller.getAttendenceGeneral();
		assertFalse(csv.trim().isEmpty());
		assertEquals(";01.01.2018;02.01.2018\n" + 
				"Surname1, Prename1;X;\n" + 
				"Surname2, Prename2;;X", csv);
	}

	@Test
	public void testRequestUri() throws Exception {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getRequestURL()).thenReturn(new StringBuffer("testhost.de"));
		HttpServletResponse response = mock(HttpServletResponse.class);
		final StringWriter out = new StringWriter();
		ServletOutputStream stream = new ServletOutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				out.write(b);
			}
		};
		when(response.getOutputStream()).thenReturn(stream);
		controller.getHtmlUri(req, response);
		String output = out.toString();
		assertFalse(output.isEmpty());
		
	}
}
