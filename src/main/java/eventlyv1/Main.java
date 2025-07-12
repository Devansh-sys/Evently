package eventlyv1;

import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import eventlyv1.DBUtility.DBConnection;
import eventlyv1.GoogleAPIUtility.OAuthCallbackServer;

import java.sql.SQLException;


public class Main {
    public static void main(String[] args) throws Exception {

        try {
            Dotenv dotenv = Dotenv.load();
            String url = dotenv.get("DB_URL");
            String user = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASS");
            DBConnection.init(url, user, password);

            OAuthCallbackServer.start();
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            Bot bot = new Bot();
            botsApi.registerBot(bot);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }



    }
}