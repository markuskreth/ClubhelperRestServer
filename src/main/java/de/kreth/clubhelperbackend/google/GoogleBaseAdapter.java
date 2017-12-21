package de.kreth.clubhelperbackend.google;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

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
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.sheets.v4.SheetsScopes;

public abstract class GoogleBaseAdapter {

	/** Application name. */
	protected static final String APPLICATION_NAME = "ClubHelperBackend";
	/** Directory to store user credentials for this application. */
	protected static final File DATA_STORE_DIR = new File(
	        System.getProperty("catalina.base"), ".credentials");
	/** Global instance of the JSON factory. */
	protected static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	/** Global instance of the scopes required by this quickstart.
	 *
	 * If modifying these scopes, delete your previously saved credentials
	 * at ~/.credentials/sheets.googleapis.com-java-quickstart
	 */
	static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS, CalendarScopes.CALENDAR);

	protected static Credential credential;
	
	protected static final Logger log = LoggerFactory.getLogger(GoogleBaseAdapter.class);
	/** Global instance of the {@link FileDataStoreFactory}. */
	protected final FileDataStoreFactory DATA_STORE_FACTORY;
	/** Global instance of the HTTP transport. */
	protected final HttpTransport HTTP_TRANSPORT;

	public GoogleBaseAdapter() throws GeneralSecurityException, IOException {
		super();
        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        DATA_STORE_DIR.mkdirs();
	}

	protected void checkRefreshToken() throws IOException {

		if(credential != null && (credential.getExpiresInSeconds()!=null && credential.getExpiresInSeconds()<3600)) {
			if(log.isDebugEnabled()) {
				log.debug("Security needs refresh, trying.");
			}
			boolean result = credential.refreshToken();
			if(log.isDebugEnabled()) {
				log.debug("Token refresh " + (result?"successfull.":"failed."));
			}
		} else {
			authorize();
		}
	}
	
	/**
	 * Creates an authorized Credential object.
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	protected synchronized Credential authorize() throws IOException {
		if(credential != null && (credential.getExpiresInSeconds()!=null && credential.getExpiresInSeconds()<3600)) {
			credential.refreshToken();
			return credential;
		}
	    // Load client secrets.
	    InputStream in =
	        getClass().getResourceAsStream("/client_secret.json");
	    GoogleClientSecrets clientSecrets =
	        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
	    log.trace("client secret json resource loaded.");
	    // Build flow and trigger user authorization request.
	    GoogleAuthorizationCodeFlow flow =
	            new GoogleAuthorizationCodeFlow.Builder(
	            		HTTP_TRANSPORT, 
	            		JSON_FACTORY, clientSecrets, SCOPES)
	            .setDataStoreFactory(DATA_STORE_FACTORY)
	            .setAccessType("offline")
	            .setApprovalPrompt("force")
	            .build();
	    LocalServerReceiver.Builder builder = new LocalServerReceiver.Builder();
    	builder.setPort(59431);
		try {
	
			InetAddress localHost = InetAddress.getLocalHost();
			if(false == (localHost.isAnyLocalAddress()||localHost.isSiteLocalAddress()||localHost.isLinkLocalAddress())) {

				String hostName = localHost.getHostName();
				URI uri = new URI(new StringBuilder("http://").append(hostName).toString());
		        if(uri != null) {
		        	builder.setHost(uri.getHost());
		        }
			}
		} catch (URISyntaxException e) {
			if(log.isWarnEnabled()) {
				log.warn("Unable to determine Hostname. Using default localhost.", e);
			}
		}
	
		LocalServerReceiver localServerReceiver = builder.build();
		credential = new AuthorizationCodeInstalledApp(
	        flow, localServerReceiver).authorize("user");
	    if(log.isDebugEnabled()) {
	    	log.debug("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
	    }

		credential.setExpiresInSeconds(Long.valueOf(691200L));
		
		boolean refreshToken = credential.refreshToken();
		if(refreshToken == false && log.isWarnEnabled()) {
			log.warn("Refresh of google access token failed after initialization!");
		} else if(log.isDebugEnabled()) {
			log.debug("Initial Refresh of google access Token " + (refreshToken?"was successful.":"failed."));
		}
	    return credential;
	}

}