package eventlyv1.CommandUtilities;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandContext {
    private final Update update;
    private final String messageText;
    private final Long chatId;
    private final Long userId;
    private final TelegramLongPollingBot bot;

    public CommandContext(Update update, TelegramLongPollingBot bot) {
        this.update = update;
        this.messageText = update.getMessage().getText();
        this.chatId = update.getMessage().getChatId();
        this.userId = update.getMessage().getFrom().getId();
        this.bot = bot;
    }

    public String getMessageText() { return messageText; }
    public Long getChatId() { return chatId; }
    public Long getUserId() { return userId; }
    public Update getUpdate() { return update; }

    public TelegramLongPollingBot getBot() {
        return bot;
    }
}

