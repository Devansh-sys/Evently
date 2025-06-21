package eventlyv1;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.calendar.Calendar;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import eventlyv1.CommandUtilities.Command;
import eventlyv1.CommandUtilities.CommandContainer;
import eventlyv1.CommandUtilities.CommandContext;
import eventlyv1.EventSpecificUtilities.EventActions;
import eventlyv1.EventSpecificUtilities.EventDetails;
import eventlyv1.GoogleAPIUtility.ConnectToCalendar;
import eventlyv1.GoogleAPIUtility.GeminiEventExtractor;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;

import static org.glassfish.jersey.server.ServerProperties.APPLICATION_NAME;

import static eventlyv1.GoogleAPIUtility.ConnectToCalendar.JSON_FACTORY;
import static eventlyv1.GoogleAPIUtility.ConnectToCalendar.getCredentials;

public class Bot extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return "synctocalendar_bot";
    }

    @Override
    public String getBotToken() {
        return io.github.cdimascio.dotenv.Dotenv.load().get("TELEGRAM_BOT_TOKEN");
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            CommandContext context = new CommandContext(update, this);
            Calendar service = null;
            final NetHttpTransport HTTP_TRANSPORT;
            try {
                HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, context.getUserId().toString()))
                        .setApplicationName(APPLICATION_NAME)
                        .build();
            } catch (IOException e ) {

                String authUrl = ConnectToCalendar.getAuthorizationUrl(context.getUserId().toString());
                SendMessage authMessage = SendMessage.builder()
                        .chatId(context.getChatId().toString())
                        .text("🔗 Calendar Connection Required\n\n" +
                                "Your calendar is not connected yet. Please connect it via this link:\n\n" +
                                "Don't be shy to connect – it's safe and easy. Click below:\n" + authUrl)
//                        .parseMode("Markdown")
                        .build();
                System.out.println("Generated URL: " + authUrl);

                try {
                    execute(authMessage);
                } catch (TelegramApiException ex) {
                    ex.printStackTrace();
                }
                return;
            } catch (SQLException e) {
                System.out.println("Error while getting token from database: " + e.getMessage());
                throw new RuntimeException(e);
            } catch (GeneralSecurityException e) {

                System.out.println("Error while getting credentials: " + e.getMessage());
                throw new RuntimeException(e);
            }

            if(context.getMessageText().startsWith("/")){
                CommandContainer commandContainer = new CommandContainer();
                String commandIdentifier = context.getMessageText().split(" ")[0].toLowerCase();
                Command command = commandContainer.getCommand(commandIdentifier);
                try {
                    command.executeCommand(context, service);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else{

                EventDetails eventDetails = GeminiEventExtractor.extractEvent(context.getMessageText());
                if(eventDetails.getSummary().isEmpty()){
                    SendMessage errorMessage = SendMessage.builder()
                            .chatId(context.getChatId().toString())
                            .text("❌ *Sorry!*\n\nI couldn't extract any event details from your message. Please try again with a clearer description.")
                            .parseMode("Markdown")
                            .build();
                    try {
                        execute(errorMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                EventActions eventActions = new EventActions();
                try {
                    eventActions.addEvent(eventDetails,service);
                    SendMessage confirmation = SendMessage.builder()
                            .chatId(context.getChatId().toString())
                            .text("✅ *Success!*\n\n" +
                                    "*Event Details:*\n" +
                                    "*Summary:* " + eventDetails.getSummary() + "\n" +
                                    "*Description:* " + eventDetails.getDescription() + "\n" +
                                    "*Start DateTime:* " + eventDetails.getStartDateTime() + "\n" +
                                    "*End DateTime:* " + eventDetails.getEndDateTime() + "\n" +
                                    "*Location:* " + eventDetails.getLocation() + "\n\n" +
                                    "Event has been added to your calendar successfully! 📅")
                            .parseMode("Markdown")
                            .build();
                    execute(confirmation);
                    execute(confirmation);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }


        }
    }
}
