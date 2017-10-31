package de.kreth.clubhelperbackend.controller;

import java.io.IOException;
import java.util.ArrayList;
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

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public JumpHeightSheet put(long id, JumpHeightSheet toUpdate) {
		// TODO Auto-generated method stub
		return null;
	}

	@RequestMapping(value = "/", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public JumpHeightSheet post(JumpHeightSheet toCreate) {
		// TODO Auto-generated method stub
		return null;
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
		for(JumpHeightSheet s: SheetService.getSheets()) {
			result.add(s.getTitle());
		}
		return result;
	}

}
