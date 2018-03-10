package de.kreth.clubhelperbackend.google.spreadsheet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletRequest;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets.BatchUpdate;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets.Values.Update;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.DeleteSheetRequest;
import com.google.api.services.sheets.v4.model.DuplicateSheetRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.UpdateSheetPropertiesRequest;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import de.kreth.clubhelperbackend.google.GoogleBaseAdapter;

class GoogleSpreadsheetsAdapter extends GoogleBaseAdapter {

    static final String SPREADSHEET_ID = "1clDEc9NakRJTM-onxrjsuyB2Vby8P1j6NINdWelOrwg";
    
    private static final AtomicInteger instanceCount = new AtomicInteger(0);
    private static final Lock lock = new ReentrantLock();
    
    private Sheets service;

    public GoogleSpreadsheetsAdapter() throws IOException, GeneralSecurityException {
    	super();
    	int number = instanceCount.incrementAndGet();
    	if(log.isInfoEnabled()) {
    		log.info("Instanciated " + getClass().getName() + " #" + number);
    	}
	}
    
    @Override
    protected void checkRefreshToken(ServletRequest request) throws IOException {
    	try {
			lock.tryLock(10, TimeUnit.MINUTES);
	    	super.checkRefreshToken(request);
	    	if(service == null) {
	            service =  new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
	                    .setApplicationName(APPLICATION_NAME)
	                    .build();
	    	}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
	private BatchUpdateSpreadsheetResponse sendRequest(Request request, Boolean includeSpreadsheetInResponse) throws IOException {
		BatchUpdateSpreadsheetRequest content = new BatchUpdateSpreadsheetRequest();
		List<Request> requests = new ArrayList<>();
		requests.add(request);
		content.setRequests(requests );
		content.setIncludeSpreadsheetInResponse(includeSpreadsheetInResponse);
		BatchUpdate batch = service.spreadsheets().batchUpdate(SPREADSHEET_ID, content );
		BatchUpdateSpreadsheetResponse response = batch.execute();
		return response;
	}
    
    public List<Sheet> getSheets(ServletRequest request) throws IOException, InterruptedException {
    	if(log.isTraceEnabled()) {
    		log.trace("Loading Sheets");
    	}
    	if(lock.tryLock(10, TimeUnit.SECONDS) == false) {
    		log.error("Error fetching Sheets! Using empty result.");
    		return Collections.emptyList();
    	}
        Spreadsheet sheet = loadSheet(request);
        return sheet.getSheets();
    }
    
	private Spreadsheet loadSheet(ServletRequest request) throws IOException {
		checkRefreshToken(request);
		
		Spreadsheets spreadsheets;
		Spreadsheet sheet;
		try {
			spreadsheets = service.spreadsheets();
			sheet = spreadsheets.get(SPREADSHEET_ID).setIncludeGridData(false).execute();
			
		} catch (IOException e) {
			if(log.isDebugEnabled()) {
				log.debug("Error fetching SpreadSheed, trying token refresh", e);
			}
			credential.refreshToken();
			if(log.isInfoEnabled()) {
				log.info("Successfully refreshed Google Security Token.");
			}
			spreadsheets = service.spreadsheets();
			sheet = spreadsheets.get(SPREADSHEET_ID).setIncludeGridData(false).execute();
		}
		return sheet;
	}
    
	public Sheet dublicateTo(ServletRequest request, String originalTitle, String title) throws IOException, InterruptedException {
        Spreadsheet sheet = loadSheet(request);
        List<Sheet> sheets=sheet.getSheets();
        Integer sourceSheetId = null;
        if(log.isTraceEnabled()) {
        	log.trace("Changing Title from " + originalTitle + " to " + title);
        }
        for(Sheet s : sheets) {
        	if(s.getProperties().getTitle().equals(originalTitle)) {
        		sourceSheetId = s.getProperties().getSheetId();
        		break;
        	}
        }

        if(sourceSheetId == null) {
        	throw new IllegalStateException("Source Sheet with name \"" + originalTitle + "\" not found!");
        }
        
		DuplicateSheetRequest ds = new DuplicateSheetRequest();
		ds.setSourceSheetId(sourceSheetId);
		ds.setNewSheetName(title);
		
		Request googleRequest = new Request();
		googleRequest.setDuplicateSheet(ds);
		sendRequest(googleRequest, false);
		sheets = getSheets(request);
		
        for(Sheet s : sheets) {
        	if(s.getProperties().getTitle().equals(title)) {
        		return s;
        	}
        }

		return null;
	}

	public void delete(Sheet sheet) throws IOException {

		if(sheet == null || sheet.getProperties() == null) {
			return;
		}
		DeleteSheetRequest ds = new DeleteSheetRequest();
		ds.setSheetId(sheet.getProperties().getSheetId());
		
		Request request = new Request();
		request.setDeleteSheet(ds);
		sendRequest(request, false);
	}

	public ValueRange setValue(String sheetTitle, int column, int row, ValueRange content) throws IOException {
		StringBuilder range = new StringBuilder();
		range.append(sheetTitle).append("!");
		range.append(intToColumn(column)).append(row);
		return setValue(range.toString(), content);
	}
	
	static String intToColumn(int column) {
	    StringBuilder name = new StringBuilder();
	    while (column > 0) {
	    	column--;
	        name.insert(0, (char)('A' + column%26));
	        column /= 26;
	    }
	    return name.toString();
	}
	
	public ValueRange setValue(String range, ValueRange content) throws IOException {
		if(log.isDebugEnabled()) {
			log.debug("Setting value of " + range + " to " + content);
		}
		Update updateExecutor = service.spreadsheets().values().update(SPREADSHEET_ID, range, content);
		UpdateValuesResponse response = updateExecutor.setValueInputOption("RAW").execute();
		return response.getUpdatedData();
	}
	
	public ValueRange getValues(String sheetTitle, String range) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(sheetTitle);
		stringBuilder.append("!");
		stringBuilder.append(range);
		ValueRange result = service.spreadsheets().values().get(SPREADSHEET_ID, stringBuilder.toString()).execute();
		return result;
	}
	
	public void setSheetTitle(Sheet sheet, String name) throws IOException {		
		SheetProperties properties  = new SheetProperties();
		properties.setTitle(name);
		properties.setSheetId(sheet.getProperties().getSheetId());
		
		UpdateSheetPropertiesRequest ur = new UpdateSheetPropertiesRequest();
		ur.setProperties(properties);
		ur.setFields("title");
		Request request = new Request();
		request.setUpdateSheetProperties(ur);
		sendRequest(request, false);
		
	}
}