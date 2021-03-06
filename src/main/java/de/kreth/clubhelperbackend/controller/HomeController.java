package de.kreth.clubhelperbackend.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.api.services.sheets.v4.model.Sheet;

import de.kreth.clubhelperbackend.pdf.AttendenceBean;
import de.kreth.clubhelperbackend.pdf.AttendenceBeanCollector;
import de.kreth.googleconnectors.spreadsheet.GoogleInitAdapter;

/**
 * Handles requests for the application home page.
 */
@Controller
@RequestMapping("/")
public class HomeController implements ApplicationContextAware {

	private static final Logger logger = LoggerFactory
			.getLogger(HomeController.class);
	private ApplicationContext context;

	/**
	 * Simply selects the home view to render by returning its name.
	 * 
	 * @param response
	 *                     response Object for redirection.
	 * @param device
	 *                     device Type to decide redirection target.
	 * @param locale
	 *                     locale for language
	 * @param model
	 *                     model to set response data
	 * @return Name of View
	 */
<<<<<<< HEAD
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(HttpServletResponse response, Device device,
			Locale locale, Model model) {
=======
	@GetMapping(value = "/")
	public String home(HttpServletResponse response, Device device, Locale locale, Model model) {
>>>>>>> master

		logger.info("Welcome home! The client locale is {}.", locale);
		logger.info("Client Device is running " + device.getDevicePlatform()
				+ ": " + device);
		if (device.isNormal() == false) {
			try {
				response.sendRedirect("person");
				return null;
			} catch (IOException e) {
				logger.error("Unable to redirect Mobile Device to person", e);
			}
		}
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,
				DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate);

		String dir = new File(".").getAbsolutePath();
		model.addAttribute("directory", dir);

		return "home";
	}

	/**
	 * @return
	 */
	@GetMapping(value = "/allAtt", produces = "text/comma-separated-values;charset=UTF-8")
	@ResponseBody
	public final String getAttendenceGeneral() {
		DataSource datasource = context.getBean(DataSource.class);
		StringBuilder txt = new StringBuilder();
<<<<<<< HEAD
		try {
			Statement stm = datasource.getConnection().createStatement();
			ResultSet rs = stm.executeQuery(
					"SELECT attendance.on_date, prename, surname\n"
							+ "FROM markuskreth.attendance\n"
							+ "	left join markuskreth.person on person.id = person_id\n"
							+ "order by surname, prename, on_date");
=======
		try (Connection conn = datasource.getConnection()){
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery("SELECT attendance.on_date, prename, surname\n" 
					+ "FROM markuskreth.attendance\n" 
					+ "	left join markuskreth.person on person.id = person_id\n" 
					+ "order by surname, prename, on_date");
>>>>>>> master
			AttendenceBeanCollector coll = new AttendenceBeanCollector();
			List<AttendenceBean> list = coll.getList(rs);
			String text = coll.format(list);
			System.out.println();

			System.out.println(text);
			txt.append(text);

			System.out.println();
		} catch (SQLException e) {
			logger.error("Error fetching data", e);
		}
		return txt.toString();
	}

	@RequestMapping(value = {"/test", "/tests"}, method = RequestMethod.GET)
	public final String runTests() {
		return "test";
	}

<<<<<<< HEAD
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public final String showLogin(HttpServletResponse response,
			Authentication auth) throws IOException {
=======
	@GetMapping(value = "/login")
	public final String showLogin(HttpServletResponse response, Authentication auth) throws IOException {
>>>>>>> master

		if (auth == null) {
			logger.debug("showing login view");
			return "login";
		} else {
			logger.info("redirecting to person");
			response.sendRedirect("person");
			return null;
		}
	}

<<<<<<< HEAD
	@RequestMapping(value = "/googleauth", method = RequestMethod.GET)
	public final void getHtmlUri(HttpServletRequest req,
			HttpServletResponse response) throws IOException,
			GeneralSecurityException, URISyntaxException, InterruptedException {
=======
	@GetMapping(value="/googleauth", produces = "text/plain;charset=UTF-8")
	public final void getHtmlUri(HttpServletRequest req, HttpServletResponse response) throws IOException, GeneralSecurityException, URISyntaxException, InterruptedException {
>>>>>>> master

		URI uri = new URI(req.getRequestURL().toString());
		GoogleInitAdapter adapter = new GoogleInitAdapter(uri);

		List<String> titles = new ArrayList<>();
<<<<<<< HEAD
		BufferedWriter out = new BufferedWriter(
				new OutputStreamWriter(response.getOutputStream()));
=======
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8));
>>>>>>> master
		out.write("Gefundene Sheets:");
		out.newLine();
		for (Sheet s : adapter.getSheets(req.getServerName())) {
			out.write(s.getProperties().getTitle());
			out.newLine();
			titles.add(s.getProperties().getTitle());
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Gefundene Sheets: " + titles);
		}
		out.flush();
	}

	@Override
	public final void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
	}

}
