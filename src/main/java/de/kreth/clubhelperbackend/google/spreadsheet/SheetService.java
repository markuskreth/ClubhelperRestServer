package de.kreth.clubhelperbackend.google.spreadsheet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.ValueRange;

public enum SheetService {

	INSTANCE;

	private static List<Sheet> sheets;
	Logger log = LoggerFactory.getLogger(getClass());
	private GoogleSpreadsheetsAdapter service;

	private SheetService() {
		createService();
	}

	private void createService() {
		if (service != null) {
			return;
		}
		if (log.isInfoEnabled()) {
			log.info(GoogleSpreadsheetsAdapter.class.getName()
					+ " not initiated, creating...");
		}
		try {
			service = new GoogleSpreadsheetsAdapter();
			sheets = new ArrayList<>();
		} catch (IOException | GeneralSecurityException e) {
			log.error("unable to init " + getClass().getName()
					+ ", Service won't work.", e);
		}
	}

	public static JumpHeightSheet get(ServletRequest request, String title)
			throws IOException, InterruptedException {
		if (INSTANCE.log.isDebugEnabled()) {
			INSTANCE.log.debug(
					"Getting " + Sheet.class.getName() + " for " + title);
		}
		Sheet result = getForName(request, title);
		try {
			return new JumpHeightSheet(result);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public static List<JumpHeightSheet> getSheets(ServletRequest request)
			throws IOException, InterruptedException {
		List<JumpHeightSheet> result = new ArrayList<>();
		for (Sheet s : getAllSheets(request)) {
			try {
				result.add(new JumpHeightSheet(s));
			} catch (SheetDataException e) {
				INSTANCE.log.error("unable to add sheet: " + s, e);
			}
		}
		return result;
	}

	private static Sheet getForName(ServletRequest request, String title)
			throws IOException, InterruptedException {
		List<Sheet> all = getAllSheets(request);

		for (Sheet s : all) {
			if (INSTANCE.log.isTraceEnabled()) {
				INSTANCE.log
						.trace("found Sheet: " + s.getProperties().getTitle());
			}
			if (s.getProperties().getTitle().equals(title)) {
				if (INSTANCE.log.isTraceEnabled()) {
					INSTANCE.log.trace("returning Sheet: " + s);
				}
				return s;
			}
		}
		throw new IOException("Sheet with title \"" + title + "\" not found.");
	}

	private static List<Sheet> getAllSheets(ServletRequest request)
			throws IOException, InterruptedException {
		INSTANCE.createService();
		if (sheets != null && sheets.isEmpty() == false) {
			return sheets;
		}
		sheets = INSTANCE.service.getSheets(request.getServerName());
		return sheets;
	}

	public static JumpHeightSheet create(ServletRequest request, String title)
			throws IOException {
		INSTANCE.createService();
		try {
			Sheet dublicateTo = INSTANCE.service
					.dublicateTo(request.getServerName(), "Vorlage", title);
			sheets.add(dublicateTo);
			return new JumpHeightSheet(dublicateTo);
		} catch (Exception ex) {
			throw new IOException(ex);
		}
	}

	public static void delete(JumpHeightSheet test) throws IOException {
		INSTANCE.createService();
		sheets.remove(test.sheet);
		INSTANCE.service.delete(test.sheet);
	}

	public static ExtendedValue set(String sheetTitle, int column, int row,
			double value) throws IOException {
		INSTANCE.createService();
		ValueRange content = new ValueRange();
		content = content.setValues(Arrays.asList(Arrays.asList(value)));
		INSTANCE.service.setValue(sheetTitle, column, row, content);
		ExtendedValue res = new ExtendedValue();
		res.setNumberValue(value);
		return res;
	}

	public static ExtendedValue set(String sheetTitle, int column, int row,
			String value) throws IOException {
		INSTANCE.createService();
		ValueRange content = new ValueRange();
		content = content.setValues(Arrays.asList(Arrays.asList(value)));
		INSTANCE.service.setValue(sheetTitle, column, row, content);
		ExtendedValue res = new ExtendedValue();
		res.setStringValue(value);
		return res;
	}

	public static JumpHeightSheet changeTitle(ServletRequest request,
			Sheet sheet, String name) throws IOException, InterruptedException {
		INSTANCE.createService();
		INSTANCE.service.setSheetTitle(sheet, name);
		sheets = null;
		return get(request, name);
	}

	public static ValueRange getRange(String sheetTitle, String range)
			throws IOException {
		INSTANCE.createService();
		return INSTANCE.service.getValues(sheetTitle, range);
	}

}
