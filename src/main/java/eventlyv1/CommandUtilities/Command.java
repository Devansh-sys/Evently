package eventlyv1.CommandUtilities;


import com.google.api.services.calendar.Calendar;

public interface Command {
    void executeCommand(CommandContext context, Calendar service) throws Exception;
}
