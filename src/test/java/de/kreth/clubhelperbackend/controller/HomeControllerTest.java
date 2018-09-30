package de.kreth.clubhelperbackend.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import de.kreth.testutils.sql.TestResultset;

public class HomeControllerTest {

	@Mock
	private ApplicationContext context;
	@Mock
	private HttpServletResponse response;
	@Mock
	private Device device;
	@Mock
	private Model model;
	@Mock
	private Authentication auth;
	
	private HomeController controller;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		controller = new HomeController();
		controller.setApplicationContext(context);
	}

	@Test
	public void testHome() throws IOException {
		when(device.isNormal()).thenReturn(true);
		assertEquals("home", controller.home(response, device, Locale.getDefault(), model));
		when(device.isNormal()).thenReturn(false);
		assertNull(controller.home(response, device, Locale.getDefault(), model));
		verify(response).sendRedirect("person");
	}

	@Test
	public void testHomeException() throws IOException {
		doThrow(new IOException("Test IOException")).when(response).sendRedirect(anyString());
		assertEquals("home", controller.home(response, device, Locale.getDefault(), model));
	}
	
	@Test
	public void testGetAttendenceGeneral() throws SQLException {
		TestResultset rs = new TestResultset();
		Map<String, Object> values = new HashMap<>();
		values.put("prename", "Prename1");
		values.put("surname", "Surname1");
		values.put("on_date",
				new java.sql.Date(
						new GregorianCalendar(2018, Calendar.JANUARY, 1)
								.getTime().getTime()));
		rs.add(values);
		values = new HashMap<>();
		values.put("prename", "Prename2");
		values.put("surname", "Surname2");
		values.put("on_date",
				new java.sql.Date(
						new GregorianCalendar(2018, Calendar.JANUARY, 2)
								.getTime().getTime()));
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
		assertEquals(";01.01.2018;02.01.2018\n" + "Surname1, Prename1;X;\n"
				+ "Surname2, Prename2;;X", csv);
	}

	@Test
	public void testShowLogin() throws IOException {
		assertEquals("login", controller.showLogin(response, null));
		assertNull(controller.showLogin(response, auth));
	}
}
