package de.kreth.clubhelperbackend.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.kreth.clubhelperbackend.spreadsheet.JumpHeightSheet;
import de.kreth.clubhelperbackend.spreadsheet.JumpHightTask;
import de.kreth.clubhelperbackend.spreadsheet.JumpHightTask.Builder;
import de.kreth.clubhelperbackend.spreadsheet.SheetDataException;
import de.kreth.clubhelperbackend.spreadsheet.SheetService;
import de.kreth.clubhelperbackend.spreadsheetdata.CellValue;
import de.kreth.clubhelperbackend.utils.ThreadPoolErrors;

@Controller
@RequestMapping("/jumpheights")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'STAFF')")
public class JumpHeightSheetController {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
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
		return createCompetitor(concatNameToTitle(prename, surname));
	}

	@RequestMapping(value = "/{prename}/{surname}/{task}", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public boolean addValue(@PathVariable("prename") String prename, @PathVariable("surname") String surname, @PathVariable("task") String task, @RequestBody Double value) throws IOException, InterruptedException {
		String title = concatNameToTitle(prename, surname);
		JumpHeightSheet sheet = SheetService.get(title);
		CellValue<Double> result = sheet.add(task, getToday(), value);
		return result.getObject().equals(value);
	}

	private Calendar getToday() {
		Calendar today = new GregorianCalendar();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		return today;
	}

	private String concatNameToTitle(String prename, String surname) {
		return new StringBuilder(surname).append(",").append(prename).toString();
	}

	@RequestMapping(value = "/tasks/{title}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<JumpHightTask> getTasks(@PathVariable("title") String title) throws IOException, InterruptedException {
		JumpHeightSheet sheet = SheetService.get(title);
		List<JumpHightTask> tasks = buildTasks(sheet);
		return tasks;
	}

	private List<JumpHightTask> buildTasks(JumpHeightSheet sheet) throws IOException, InterruptedException {
		List<JumpHightTask> tasks = new ArrayList<>();
		List<String> tasks2 = sheet.getTasks();
		ThreadPoolErrors exec = new ThreadPoolErrors(Math.min(10, tasks2.size()+2));
		for (String name: tasks2) {
			exec.execute(new Runnable() {
				
				@Override
				public void run() {
					Builder task = new JumpHightTask.Builder().setName(name);
					List<List<String>> values2;
					try {
						values2 = sheet.getValues(name).getValues();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					if(values2.size()>1) {
						List<String> values = values2.get(1);
						try {
							MutableDouble avg = new MutableDouble(0.0);
							MutableInt valuecount = new MutableInt(0);
							Optional<Double> max = values.stream()
									.filter(str->str.length()>1)
									.map(str->{
										Double valueOf = Double.valueOf(str.replace(',', '.'));
										avg.add(valueOf.doubleValue());
										valuecount.increment();
										return valueOf;
										})
									.max((d1, d2) -> { return Double.compare(d1, d2);});
							if(max.isPresent()) {
								double average = BigDecimal.valueOf(avg.doubleValue())
										.divide(BigDecimal.valueOf(valuecount.doubleValue()), BigDecimal.ROUND_HALF_DOWN)
										.setScale(2, BigDecimal.ROUND_HALF_DOWN)
										.doubleValue();
								task.setInfo(new StringBuilder("Max=").append(max.get()).append(",Avg=").append(average).toString());
							}
						} catch (Exception e) {
							if(log.isInfoEnabled()) {
								log.info("Unable to generate statistics for Task " + name + ", ignoring. Size: " + values.stream()
								.filter(str->str.length()>1).count(), e);
							}
						}
					}
					tasks.add(task.build());
				}
			});
		}
		exec.shutdown();
		Throwable t = exec.myAwaitTermination();
		if(t != null){

			if(log.isInfoEnabled()) {
				log.info("Exception while building tasks.", t);
			}
			if (t instanceof IOException){
				throw (IOException)t;
			} else {
				throw new IOException(t);
			}
		}

		if(log.isDebugEnabled()) {
			log.debug("Finisched Tasks: " + tasks);
		}
		return tasks;
	}

	@RequestMapping(value = "/{prename}/{surname}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map<String, List<?>> getByName(@PathVariable("prename") String prename, @PathVariable("surname") String surname) throws IOException, InterruptedException {
		return getByTitle(concatNameToTitle(prename, surname));
	}

	@RequestMapping(value = "/statistics/{prename}/{surname}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map<String, List<?>> getStatisticsFor(@PathVariable("prename") String prename, @PathVariable("surname") String surname) throws IOException, InterruptedException {
		String title = concatNameToTitle(prename, surname);
		
		return getByTitle(title);
	}
	
	@RequestMapping(value = "/{title}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map<String, List<?>> getByTitle(@PathVariable("title") String title) throws IOException, InterruptedException {
		JumpHeightSheet sheet;
		try {
			sheet = SheetService.get(title);
		} catch (IOException e) {
			if(e.getMessage().equals("Sheet with title \"" + title + "\" not found.")) {
				log.warn("Sheet load failed!", e);
				sheet = SheetService.create(title);
			} else {
				throw e;
			}
		}
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
					List<JumpHightTask> tasks = buildTasks(sheet);
					result.put("tasks", tasks);
				} catch (IOException | InterruptedException e) {
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
