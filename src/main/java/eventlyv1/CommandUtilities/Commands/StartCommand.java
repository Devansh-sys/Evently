package eventlyv1.CommandUtilities.Commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.google.api.services.calendar.Calendar;
import eventlyv1.CommandUtilities.Command;
import eventlyv1.CommandUtilities.CommandContext;


public class StartCommand implements Command {
    public void executeCommand(CommandContext context, Calendar service) {
        Long chatId = context.getChatId();
        Long userId = context.getUserId();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("👋 *Welcome to Evently!*\n\n");

        messageBuilder.append("🎯 Here’s what I can do for you:\n\n");
        messageBuilder.append("➕ *add an event*: Just forward me the event details as a message. I'll read it and add it to your calendar automatically!\n\n");
        messageBuilder.append("🗨️below are the *Commands* and their actions\n");
        messageBuilder.append("👉 1. /nextevent – View your next upcoming event\n");
        messageBuilder.append("👉 2. /lastevent – View your most recent past event\n");
        messageBuilder.append("👉 3. /next10event – See your next 10 upcoming events\n\n");
        messageBuilder.append("👉 4. /help – See all commands/n\n");
        messageBuilder.append("💔 _If your message is neither an event nor one of the above commands, I might not be of much use to you for now._");

        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(messageBuilder.toString())
                .parseMode("Markdown")
                .build();

        try {
            context.getBot().execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
