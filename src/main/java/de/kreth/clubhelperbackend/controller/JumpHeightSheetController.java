package de.kreth.clubhelperbackend.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.kreth.clubhelperbackend.spreadsheet.JumpHeightSheet;
import de.kreth.clubhelperbackend.spreadsheet.SheetDataException;
import de.kreth.clubhelperbackend.spreadsheet.SheetService;
import de.kreth.clubhelperbackend.spreadsheetdata.CellValue;

@Controller
@RequestMapping("/jumpheights")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'STAFF')")
public class JumpHeightSheetController {

	@RequestMapping(value = "/tasks/{title}/{taskName}", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public List<String> addTask(@PathVariable("title") String title, @PathVariable("taskName") String taskName) throws IOException {
		return SheetService.get(title).addTask(taskName);
	}

	@RequestMapping(value = "/{title}", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, List<?>> createCompetitor(@PathVariable("title") String title) throws IOException, InterruptedException {
		JumpHeightSheet sheet = SheetService.create(title);
		Map<String, List<?>> result = createTaskValues(sheet);
		return result;
	}

	@RequestMapping(value = "/{prename}/{surname}", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, List<?>> createCompetitor(@PathVariable("prename") String prename, @PathVariable("surname") String surname) throws IOException, InterruptedException {
		return createCompetitor(new StringBuilder(surname).append(",").append(prename).toString());
	}

	@RequestMapping(value = "/tasks/{title}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<String> getTasks(@PathVariable("title") String title) throws IOException {
		return SheetService.get(title).getTasks();
	}


	@RequestMapping(value = "/{prename}/{surname}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map<String, List<?>> getByName(@PathVariable("prename") String prename, @PathVariable("surname") String surname) throws IOException, InterruptedException {
		return getByTitle(new StringBuilder(surname).append(",").append(prename).toString());
	}
	
	@RequestMapping(value = "/{title}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map<String, List<?>> getByTitle(@PathVariable("title") String title) throws IOException, InterruptedException {
		JumpHeightSheet sheet = SheetService.get(title);
		Map<String, List<?>> result = createTaskValues(sheet);
		return result;
	}

	private Map<String, List<?>> createTaskValues(JumpHeightSheet sheet) throws IOException, InterruptedException {

		final Map<String, List<?>> result = new HashMap<>();
		ExecutorService exec = Executors.newFixedThreadPool(2);
		exec.execute(new Runnable() {
			
			@Override
			public void run() {

				try {
					List<CellValue<Date>> dates = sheet.getDates();
					result.put("dates", dates);
				} catch (IOException e) {
					throw new SheetDataException("cannot load Dates for " + sheet.getTitle(), e);
				}
			}
		});
		exec.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					List<String> tasks = sheet.getTasks();
					result.put("tasks", tasks);
				} catch (IOException e) {
					throw new SheetDataException("cannot load Tasks for " + sheet.getTitle(), e);
				}
			}
		});
		exec.shutdown();
		exec.awaitTermination(30, TimeUnit.SECONDS);
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
