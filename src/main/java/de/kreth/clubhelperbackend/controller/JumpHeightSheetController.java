package de.kreth.clubhelperbackend.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.mobile.device.Device;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.kreth.clubhelperbackend.spreadsheet.JumpHeightSheet;
import de.kreth.clubhelperbackend.spreadsheet.SheetService;

@Controller
@RequestMapping("/jumpheights")
//@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'STAFF')")
public class JumpHeightSheetController {

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String getAsView(long id, boolean ajax, Device device, Model m) {
		// TODO Auto-generated method stub
		return null;
	}

	@RequestMapping(value = { "/", "" }, method = RequestMethod.GET)
	public String getAllAsView(boolean ajax, Device device, Model m) {
		// TODO Auto-generated method stub
		return null;
	}

	@RequestMapping(value = "/tasks/{title}/{taskName}", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public List<String> put(@PathVariable("title") String title, @PathVariable("taskName") String taskName) throws IOException {
		return SheetService.get(title).addTask(taskName);
	}

	@RequestMapping(value = "/{title}", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, List<?>> post(@PathVariable("title") String title) throws IOException {
		JumpHeightSheet sheet = SheetService.create(title);
		Map<String, List<?>> result = createTaskValues(sheet);
		return result;
	}

	@RequestMapping(value = "/tasks/{title}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<String> getTasks(@PathVariable("title") String title) throws IOException {
		return SheetService.get(title).getTasks();
	}

	@RequestMapping(value = "/{title}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map<String, List<?>> getByTitle(@PathVariable("title") String title) throws IOException {
		JumpHeightSheet sheet = SheetService.get(title);
		Map<String, List<?>> result = createTaskValues(sheet);
		return result;
	}

	private Map<String, List<?>> createTaskValues(JumpHeightSheet sheet) {
		List<Date> dates = sheet.getDates();
		List<String> tasks = sheet.getTasks();
		Map<String, List<?>> result = new HashMap<>();
		result.put("dates", dates);
		result.put("tasks", tasks);
		return result;
	}

	@RequestMapping(value = { "/", "" }, method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<String> getTitles() throws IOException {
		List<String> result = new ArrayList<>();
		List<JumpHeightSheet> sheets = SheetService.getSheets();
		sheets.sort(new Comparator<JumpHeightSheet>() {

			@Override
			public int compare(JumpHeightSheet o1, JumpHeightSheet o2) {
				return o1.getTitle().compareTo(o2.getTitle());
			}
		});
		for(JumpHeightSheet s: sheets) {
			result.add(s.getTitle());
		}
		return result;
	}

}
