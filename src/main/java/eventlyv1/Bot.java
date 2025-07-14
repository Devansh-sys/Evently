package eventlyv1;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.calendar.Calendar;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.glassfish.jersey.server.ServerProperties.APPLICATION_NAME;

import static eventlyv1.GoogleAPIUtility.ConnectToCalendar.JSON_FACTORY;
import static eventlyv1.GoogleAPIUtility.ConnectToCalendar.getCredentials;

public class Bot extends TelegramLongPollingBot {
    private static final Map<Long, EventDetails> eventDetailsMap = new ConcurrentHashMap<>();

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


        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = context.getChatId();

            if ("confirm_event".equals(callbackData)) {
                EventActions eventActions = new EventActions();
                String linkToEvent = "";
                try {
                    EventDetails eventDetails = eventDetailsMap.get(chatId);
                    linkToEvent = eventActions.addEvent(eventDetails,service);
                    SendMessage confirmationMessage = SendMessage.builder()
                            .chatId(chatId.toString())
                            .text("✅ Event confirmed and added to your calendar!" + "\n you can see it here: " + linkToEvent)
                            .build();

                    execute(confirmationMessage);


                }catch(IllegalArgumentException e){
                    e.printStackTrace();
                    SendMessage startDateIsIllegalMsg = SendMessage.builder()
                            .chatId(chatId.toString())
                            .text("Either startDate is before today or startDate is after EndDate Kindly edit the message and resend it")
                            .build();

                    try {
                        execute(startDateIsIllegalMsg);
                    } catch (TelegramApiException ex) {
                        throw new RuntimeException(ex);
                    }


                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

            } else if ("cancel_event".equals(callbackData)) {
                SendMessage cancellationMessage = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("❌ Event creation canceled.")
                        .build();
                try {
                    execute(cancellationMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (update.hasMessage() && update.getMessage().hasText()) {


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
                eventDetailsMap.put(context.getChatId(), eventDetails);

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
                SendMessage confirmationRequest = SendMessage.builder()
                        .chatId(context.getChatId().toString())
                        .text("📝 *Event Details:*\n\n" +
                                "*Summary:* " + eventDetails.getSummary() + "\n\n" +
                                "*Description:* " + eventDetails.getDescription() + "\n\n" +
                                "*Start DateTime:* " + LocalDateTime.parse(eventDetails.getStartDateTime(), DateTimeFormatter.ISO_DATE_TIME)
                                .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy h:mm a")) + "\n\n"  +
                                "*End DateTime:* " + LocalDateTime.parse(eventDetails.getEndDateTime(), DateTimeFormatter.ISO_DATE_TIME)
                                .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy h:mm a")) + "\n\n"  +
                                "*Location:* " + eventDetails.getLocation() + "\n\n\n" +
                                "Do you want to add this event to your calendar?")
                        .parseMode("Markdown")
                        .replyMarkup(InlineKeyboardMarkup.builder()
                                .keyboardRow(Arrays.asList(
                                        InlineKeyboardButton.builder()
                                                .text("✅ Confirm")
                                                .callbackData("confirm_event")
                                                .build(),
                                        InlineKeyboardButton.builder()
                                                .text("❌ Cancel")
                                                .callbackData("cancel_event")
                                                .build()
                                ))
                                .build())
                        .build();
                try {
                    execute(confirmationRequest);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }


            }


        }
    }
}
