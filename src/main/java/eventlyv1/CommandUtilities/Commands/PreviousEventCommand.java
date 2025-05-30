package eventlyv1.CommandUtilities.Commands;

import com.google.api.services.calendar.Calendar;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import eventlyv1.CommandUtilities.Command;
import eventlyv1.CommandUtilities.CommandContext;
import eventlyv1.EventSpecificUtilities.EventActions;

import java.io.IOException;

public class PreviousEventCommand implements Command {
    public void executeCommand(CommandContext context, Calendar service) throws IOException {
        Long chatId = context.getChatId();
        Long userId = context.getUserId();
        String nextEventString = EventActions.displayLastEvent(service);
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(nextEventString)
                .parseMode("Markdown")
                .build();

        try {
            context.getBot().execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
