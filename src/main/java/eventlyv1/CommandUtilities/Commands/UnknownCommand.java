package eventlyv1.CommandUtilities.Commands;

import com.google.api.services.calendar.Calendar;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import eventlyv1.CommandUtilities.Command;
import eventlyv1.CommandUtilities.CommandContext;

public class UnknownCommand implements Command {
    @Override
    public void executeCommand(CommandContext context, Calendar service) throws TelegramApiException {
        String unknownMessage = "Sorry, I didn't recognize that command. Type /help for the list of available commands.";
        SendMessage message = new SendMessage(context.getChatId().toString(), unknownMessage);
         context.getBot().execute(message); // Ensure your context provides a reference to the bot
        // send the message using your bot's send logic
    }
}
