package de.kreth.clubhelperbackend.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 */
@Controller
@RequestMapping("/")
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(HttpServletRequest request, Locale locale, Model model) {

		logger.info("Welcome home! The client locale is {}.", locale);

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate);

		String dir = new File(".").getAbsolutePath();
		model.addAttribute("directory", dir);

		return "home";
	}

	@RequestMapping(value = "/downloadJnlp", method = RequestMethod.GET)
	public String downloadJnlp(HttpServletRequest request, HttpServletResponse response) {
		File jnlpFile = new File(System.getProperty("java.io.tmpdir"), "ClubHelperClient.jnlp");

		String requestUrl = request.getRequestURL().toString();
		logger.debug("Current Url: " + requestUrl);
		requestUrl = requestUrl.substring(0, requestUrl.indexOf("/downloadJnlp"));
		logger.debug("Current Url: " + requestUrl);

		response.setContentType("application/x-java-jnlp-file");
		response.setHeader("Content-Disposition", "attachment; filename=\"ClubHelperClient.jnlp\"");
		FileInputStream a = null;

		try {
			logger.debug("Downloadfile: " + jnlpFile.getAbsolutePath());

			String fileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<jnlp spec=\"1.0+\" codebase=\""
					+ requestUrl + "\">\n" + "    \n" + "    <information>\n"
					+ "        <title>ClubHelperClient</title>\n" + "        <vendor>Markus Kreth</vendor>\n"
					+ "    </information>\n" + "    \n" + "    <resources>\n"
					+ "        <j2se href=\"http://java.sun.com/products/autodl/j2se\" version=\"1.8+\"/>\n"
					+ "        <jar href=\"resources/ClubHelperClient.jar\" main=\"true\" />\n" + "    </resources>\n"
					+ "    \n" + "    <application-desc\n" 
					+ "         name=\"Clubhelper Web Client\"\n" 
					+ "         main-class=\"de.kreth.clubhelperclient.Main\">\n"
					+ "         <argument>"  + requestUrl + "</argument>\n"
					+ "     </application-desc>\n" + "     \n" + "     <update check=\"background\"/>\n" + "    \n"
					+ "    <security><all-permissions/></security>\n" + "</jnlp>";
			PrintWriter out = new PrintWriter(Files.newBufferedWriter(jnlpFile.toPath()));
			out.write(fileContent);
			out.flush();
			out.close();
			
			response.setHeader("Content-Length", String.valueOf(jnlpFile.length()));
			a = new FileInputStream(jnlpFile);

			while (a.available() > 0)
				response.getWriter().append((char) a.read());

		} catch (IOException e) {
			PrintStream s;
			try {
				s = new PrintStream(response.getOutputStream());
				e.printStackTrace(s);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			if (a != null) {
				try {
					a.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return "home";
	}

}
