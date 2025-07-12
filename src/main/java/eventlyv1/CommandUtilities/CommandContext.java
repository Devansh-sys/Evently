package eventlyv1.CommandUtilities;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandContext {
    private final Update update;

    public Update getUpdate() {
        return update;
    }

    public TelegramLongPollingBot getBot() {
        return bot;
    }

    private final TelegramLongPollingBot bot;
    private final String messageText;
    private final String callbackData;

    public CommandContext(Update update, TelegramLongPollingBot bot) {
        this.update = update;
        this.bot = bot;

        if (update.hasMessage() && update.getMessage().hasText()) {
            this.messageText = update.getMessage().getText();
            this.callbackData = null;
        } else if (update.hasCallbackQuery()) {
            this.callbackData = update.getCallbackQuery().getData();
            this.messageText = null;
        } else {
            this.messageText = null;
            this.callbackData = null;
        }
    }

    public String getMessageText() {
        return messageText;
    }

    public String getCallbackData() {
        return callbackData;
    }

    public Long getChatId() {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        return null;
    }

    public Long getUserId() {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        }
        return null;
    }
}

