package eventlyv1.CommandUtilities.Commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import eventlyv1.CommandUtilities.Command;
import eventlyv1.CommandUtilities.CommandContext;
import eventlyv1.EventSpecificUtilities.EventActions;
import com.google.api.services.calendar.Calendar;

import java.io.IOException;


public class NextEventCommand implements Command {
    //write the logic for the next event command here
    public void executeCommand(CommandContext context, Calendar service) throws IOException {
        Long chatId = context.getChatId();
        Long userId = context.getUserId();
        String nextEventString = EventActions.displayNextEvent(service);
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(nextEventString)
                .parseMode("Markdown")
                .build();

        try {
            context.getBot().execute(message); // Make sure your context provides a reference to the bot
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}
