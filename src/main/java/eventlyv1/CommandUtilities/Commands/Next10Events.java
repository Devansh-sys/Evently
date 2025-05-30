package eventlyv1.CommandUtilities.Commands;

import com.google.api.services.calendar.Calendar;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import eventlyv1.CommandUtilities.Command;
import eventlyv1.CommandUtilities.CommandContext;
import eventlyv1.EventSpecificUtilities.EventActions;

import java.io.IOException;
import java.util.List;

public class Next10Events implements Command {
    public void executeCommand(CommandContext context, Calendar service) throws IOException {
        Long chatId = context.getChatId();
        Long userId = context.getUserId();
        List<String> events = EventActions.displayNext10Events(service);
        String next10EventsString = String.format("*Upcoming Events*\n\n%s", String.join("\n\n", events));
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(next10EventsString)
                .parseMode("Markdown")
                .build();

        try {
            context.getBot().execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
