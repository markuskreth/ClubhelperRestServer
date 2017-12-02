package de.kreth.clubhelperbackend.spreadsheet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets.BatchUpdate;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets.Values.Update;
import com.google.api.services.sheets.v4.SheetsScopes;
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

class GoogleSpreadsheetsAdapter {

    static final String SPREADSHEET_ID = "1clDEc9NakRJTM-onxrjsuyB2Vby8P1j6NINdWelOrwg";
    
    /** Application name. */
    static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";

    /** Directory to store user credentials for this application. */
    static final File DATA_STORE_DIR = new File(
        System.getProperty("catalina.base"), ".credentials/sheets.googleapis.com-java-quickstart");

    /** Global instance of the JSON factory. */
    static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/sheets.googleapis.com-java-quickstart
     */
    static final List<String> SCOPES =
        Arrays.asList(SheetsScopes.SPREADSHEETS_READONLY, SheetsScopes.SPREADSHEETS);

    static final Logger log = LoggerFactory.getLogger(GoogleSpreadsheetsAdapter.class);
    private static final AtomicInteger instanceCount = new AtomicInteger(0);
    
    /** Global instance of the {@link FileDataStoreFactory}. */
    private final FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the HTTP transport. */
    final HttpTransport HTTP_TRANSPORT;

    private final Sheets service;

    public GoogleSpreadsheetsAdapter(@Nonnull URI uri) throws IOException, GeneralSecurityException {

        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
    	service = getSheetsService(uri);
    	if(instanceCount.incrementAndGet() >1) {
    		log.error("Initialized " + getClass().getName() + " #" + instanceCount.get() + ", may slow down system.");
    	}
    	if(log.isDebugEnabled()) {
    		log.debug("Initialized " + getClass().getName() + " #"+ instanceCount.get());
    	}
	}
    
	/**
     * Creates an authorized Credential object.
     * @param uri 
     * @return an authorized Credential object.
     * @throws IOException
     */
    private Credential authorize(URI uri) throws IOException {
        // Load client secrets.
        InputStream in =
            GoogleSpreadsheetsAdapter.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                		HTTP_TRANSPORT, 
                		JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("online")
                .build();
        LocalServerReceiver.Builder builder = new LocalServerReceiver.Builder();
        if(uri != null) {
        	builder.setHost(uri.getHost());
        }
		LocalServerReceiver localServerReceiver = builder.build();
		Credential credential = new AuthorizationCodeInstalledApp(
            flow, localServerReceiver).authorize("user");
        if(log.isDebugEnabled()) {
        	log.debug("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        }
        return credential;
    }

    /**
     * Build and return an authorized Sheets API client service.
     * @param uri 
     * @return an authorized Sheets API client service
     * @throws IOException
     */
    private Sheets getSheetsService(URI uri) throws IOException {
        Credential credential = authorize(uri);
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
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
    
    public List<Sheet> getSheets() throws IOException {
    	if(log.isTraceEnabled()) {
    		log.trace("Loading Sheets");
    	}
        Spreadsheet sheet = loadSheet();
        return sheet.getSheets();
    }
    
	private Spreadsheet loadSheet() throws IOException {
		Spreadsheets spreadsheets = service.spreadsheets();
        Spreadsheet sheet = spreadsheets.get(SPREADSHEET_ID).setIncludeGridData(false).execute();
		return sheet;
	}
    
	public Sheet dublicateTo(String originalTitle, String title) throws IOException {
        Spreadsheet sheet = loadSheet();
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
		
		Request request = new Request();
		request.setDuplicateSheet(ds);
		sendRequest(request, false);
		sheets = getSheets();
		
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