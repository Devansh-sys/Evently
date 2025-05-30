package eventlyv1.EventSpecificUtilities;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class EventActions {
    public static void addEvent(EventDetails eventDetails, Calendar service) throws IOException {
        Event event = new Event()
                .setSummary(eventDetails.getSummary())
                .setLocation(eventDetails.getLocation())
                .setDescription(eventDetails.getDescription());


        // Process start time
        String startDateStr = eventDetails.getStartDateTime();
        String startTime = toRfc3339(startDateStr);
        DateTime startDateTime = new DateTime(startTime);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Kolkata");
        event.setStart(start);

// Process end time
        String endDateStr = eventDetails.getEndDateTime();
        String endTime = toRfc3339(endDateStr);
        DateTime endDateTime = new DateTime(endTime);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Asia/Kolkata");
        event.setEnd(end);

        String calendarId = "primary"; // or "primary"
        event = service.events().insert(calendarId, event).execute();

        System.out.printf("Event created: %s\n", event.getHtmlLink());


    }


    private static String toRfc3339(String input) {
        try {
            // Try parsing with timezone
            OffsetDateTime odt = OffsetDateTime.parse(input, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return odt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (Exception e) {
            // Fallback: parse as LocalDateTime (no timezone), assume UTC
            LocalDateTime ldt = LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            return ldt.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
    }

    public static List<String> displayNext10Events(Calendar service) throws IOException {
            DateTime now = new DateTime(System.currentTimeMillis());
            Events events = service.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();

            List<Event> items = events.getItems();
        List<String> next10Events = getStrings(items);
        return next10Events;
        }

    private static List<String> getStrings(List<Event> items) {
        List<String> next10Events = new ArrayList<>();
        if (items.isEmpty()) {
            next10Events.add("*No upcoming events found.*");
        } else {
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                String timeStr;
                if (start == null) {
                    start = event.getStart().getDate();
                    timeStr = start.toString().substring(0, 10); // YYYY-MM-DD format for all-day events
                } else {
                    // Convert to more readable format
                    LocalDateTime localDateTime = LocalDateTime.parse(start.toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                    timeStr = localDateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a"));
                }
                next10Events.add(String.format("👀 %s\n📅 %s", event.getSummary(), timeStr));
            }
        }
        return next10Events;
    }

    public static String displayLastEvent(Calendar service) throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
                .setMaxResults(1)
                .setTimeMax(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            return "No past events found.";
        } else {
            Event lastEvent = items.get(0);
            DateTime start = lastEvent.getStart().getDateTime();
            if (start == null) {
                start = lastEvent.getStart().getDate();
            }
            String startTime = LocalDateTime.parse(start.toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a"));

            String location = lastEvent.getLocation() != null ? lastEvent.getLocation() : "📍 Location not specified";
            String description = lastEvent.getDescription() != null ? lastEvent.getDescription() : "📝 No description available";

            return String.format(" 🔙 *Last Event*\n\n" +
                            "**📌 Title:** %s\n" +
                            "**📅 Start Time:** %s\n" +
                            "**📍 Location:** %s\n" +
                            "**📝 Description:** %s",
                    lastEvent.getSummary(), startTime, location, description);
        }
    }

    public static String displayNextEvent(Calendar service) throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
                .setMaxResults(1)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            return "No upcoming events found.";
        } else {
            Event nextEvent = items.get(0);
            DateTime start = nextEvent.getStart().getDateTime();
            if (start == null) {
                start = nextEvent.getStart().getDate();
            }
            String startTime = LocalDateTime.parse(start.toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a"));

            String location = nextEvent.getLocation() != null ? nextEvent.getLocation() : "📍 Location not specified";
            String description = nextEvent.getDescription() != null ? nextEvent.getDescription() : "📝 No description available";

            return String.format("  🎉 *Next Event* \n\n" +
                            "**📌 Title:** %s\n" +
                            "**📅 Start Time:** %s\n" +
                            "**📍 Location:** %s\n" +
                            "**📝 Description:** %s",
                    nextEvent.getSummary(), startTime, location, description);
        }
    }
}
