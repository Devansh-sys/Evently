package eventlyv1.GoogleAPIUtility;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.cdimascio.dotenv.Dotenv;
import eventlyv1.EventSpecificUtilities.EventDetails;

public class GeminiEventExtractor {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String GEMINI_API_URL = dotenv.get("GEMINI_API_URL");
    public static EventDetails extractEvent(String message) {
        try {

            ZonedDateTime now = ZonedDateTime.now();
            String currentDate = now.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));
            String currentTime = now.format(DateTimeFormatter.ofPattern("HH:mm z"));

            String prompt = "Extract event details ( description, summary, startDateTime, endDateTime, location) from this message. "
                    + "If details are not explicitly mentioned, infer them from context. "
                    + "If the message refers to a relative date such as tomorrow, this weekend, or next day, convert it to the specific calendar date and time "
                    + "based on today's date, which is " + currentDate + ", and the current local time, which is " + currentTime + ". "
                    +"startDateTime should be before endDateTime. "+"Don,t keep timeRange empty, if the time is not mentioned, use the current date's 12 am as startDateTime and next day as endDateTime. "
                    + "If a field cannot be inferred, leave it blank except times, which should default as above. Return as JSON." + message;

            String jsonRequest = "{ \"contents\": [{ \"parts\": [{ \"text\": \"" + prompt + "\" }] }] }";

            URL url = new URL(GEMINI_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            StringBuilder response;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            // Parse and return the relevant part of the Gemini response
            return parseGeminiResponse(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new EventDetails();
        }
    }

    private static EventDetails parseGeminiResponse(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // 1. Parse the top-level response
            JsonNode root = mapper.readTree(response);

            // 2. Navigate to the text field
            String text = root
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text").asText();


            String jsonBlock = extractJsonFromMarkdown(text);


            return mapper.readValue(jsonBlock, EventDetails.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new EventDetails();
        }
    }

    private static String extractJsonFromMarkdown(String text) {

        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }
}
