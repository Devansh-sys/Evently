package eventlyv1.GoogleAPIUtility;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import io.github.cdimascio.dotenv.Dotenv;
import eventlyv1.DBUtility.GoogleToken;
import eventlyv1.DBUtility.GoogleTokenDAO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ConnectToCalendar {
    /**
     * Application name.
     */
    protected static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    /**
     * Global instance of the JSON factory.
     */
    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    protected static final List<String> SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR);
    private static final Dotenv dotenv = Dotenv.load();
    protected static final String CREDENTIALS_FILE_PATH = dotenv.get("CREDENTIALS_FILE_PATH", "/credentials.json");

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    // In ConnectToCalendar.java

    public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, String userTelegramId) throws IOException, SQLException, GeneralSecurityException {
        InputStream in = ConnectToCalendar.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .build();
        String clientId = clientSecrets.getDetails().getClientId();
        String clientSecret = clientSecrets.getDetails().getClientSecret();

        // Try to load existing credentials without triggering browser
        GoogleToken token = GoogleTokenDAO.getToken(userTelegramId);
        if (token != null) {
            Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                    .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                    .setJsonFactory(JSON_FACTORY)
                    .setTokenServerEncodedUrl("https://oauth2.googleapis.com/token")
                    .setClientAuthentication(new ClientParametersAuthentication(clientId, clientSecret))
                    .build();
            // Set tokens and expiry loaded from your database
            credential.setAccessToken(token.accessToken);
            credential.setRefreshToken(token.refreshToken);
            credential.setExpirationTimeMilliseconds(token.expiryTime);
            return credential;
        } else {
            throw new IOException("No credentials found for user: " + userTelegramId);
        }
    }

    public static String getAuthorizationUrl(String userTelegramId) {
        try {
            InputStream in = ConnectToCalendar.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
            if (in == null) {
                throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
            }
            GoogleClientSecrets clientSecrets =
                    GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                    .setAccessType("offline")
                    .build();
            return flow.newAuthorizationUrl()
                    .setRedirectUri("http://localhost:8888/Callback")
                    .setState(userTelegramId)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating authorization URL.";
        }
    }
}

