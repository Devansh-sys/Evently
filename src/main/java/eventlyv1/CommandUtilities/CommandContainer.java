package eventlyv1.CommandUtilities;

import eventlyv1.CommandUtilities.Commands.*;

import java.util.HashMap;
import java.util.Map;

public class CommandContainer {
    private final Map<String, Command> commandMap;

    public CommandContainer() {
        commandMap = new HashMap<>();
        commandMap.put("/start", new StartCommand());
        commandMap.put("/nextevent", new NextEventCommand());
        commandMap.put("/lastevent",  new PreviousEventCommand());
        commandMap.put("/next10event",  new Next10Events());
        commandMap.put("/help", new StartCommand());

    }

    public Command getCommand(String commandIdentifier) {
        return commandMap.getOrDefault(commandIdentifier, new UnknownCommand());
    }
}
