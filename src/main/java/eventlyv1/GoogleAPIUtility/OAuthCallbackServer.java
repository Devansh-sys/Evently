package eventlyv1.GoogleAPIUtility;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.cdimascio.dotenv.Dotenv;
import eventlyv1.DBUtility.GoogleToken;
import eventlyv1.DBUtility.GoogleTokenDAO;

public class OAuthCallbackServer {
    private static final Dotenv dotenv = Dotenv.load();
    private static final int PORT = Integer.parseInt(dotenv.get("PORT", "8888"));
    private static final String CALLBACK_PATH = dotenv.get("CALLBACK_PATH", "/Callback");
    private static final String CREDENTIALS_FILE_PATH = dotenv.get("CREDENTIALS_FILE_PATH", "/credentials.json");

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/calendar");

    public static void start() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext(CALLBACK_PATH, new CallbackHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("OAuth callback server started on http://localhost:" + PORT + CALLBACK_PATH);
    }


    static class CallbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URI requestURI = exchange.getRequestURI();
            String query = requestURI.getQuery();
            String code = null;
            String state = null;
            if (query != null) {
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length == 2) {
                        if (pair[0].equals("code")) code = pair[1];
                        if (pair[0].equals("state")) state = pair[1];
                    }
                }
            }
            String response;
            if (code != null && state != null) {
                try {
                    InputStream in = OAuthCallbackServer.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
                    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
                    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                            GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                            .setAccessType("offline")
                            .build();


                    var tokenResponse = flow.newTokenRequest(code)
                            .setRedirectUri("http://localhost:8888/Callback")
                            .execute();


                    String clientId = clientSecrets.getDetails().getClientId();
                    String clientSecret = clientSecrets.getDetails().getClientSecret();

                    Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                            .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                            .setJsonFactory(JSON_FACTORY)
                            .setTokenServerEncodedUrl("https://oauth2.googleapis.com/token")
                            .setClientAuthentication(new ClientParametersAuthentication(clientId, clientSecret))
                            .build()
                            .setAccessToken(tokenResponse.getAccessToken())
                            .setRefreshToken(tokenResponse.getRefreshToken())
                            .setExpirationTimeMilliseconds(
                                    tokenResponse.getExpiresInSeconds() != null
                                            ? System.currentTimeMillis() + tokenResponse.getExpiresInSeconds() * 1000
                                            : null
                            );

                    // Save to Supabase
                    GoogleToken token = new GoogleToken(
                            state,
                            credential.getAccessToken(),
                            credential.getRefreshToken(),
                            "Bearer",
                            credential.getExpirationTimeMilliseconds()
                    );
                    try {
                        GoogleTokenDAO.saveOrUpdateToken(token);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    response = "<html><body><h2>Authorization successful!</h2>You can now return to Telegram and use the bot.</body></html>";
                } catch (Exception e) {
                    e.printStackTrace();
                    response = "<html><body><h2>Authorization failed.</h2></body></html>";
                }
            } else {
                response = "<html><body><h2>Missing code or state.</h2></body></html>";
            }
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}

