package org.example.aoopproject;
import javafx.concurrent.Task;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Thread task to fetch Google Place Autocomplete suggestions (Bangladesh only).
 */
public class PlaceSuggestionTask extends Task<List<String>> {

    private static final long DEBOUNCE_DELAY_MS = 300; // optional small delay
    private final String input;
    private final String apiKey;

    public PlaceSuggestionTask(String input, String apiKey) {
        this.input = input;
        this.apiKey = apiKey;
    }

    @Override
    protected List<String> call() throws Exception {
        // Small delay to prevent sending too many requests while typing
        Thread.sleep(DEBOUNCE_DELAY_MS);

        List<String> results = new ArrayList<>();
        if (input == null || input.trim().isEmpty()) return results;

        try {
            // Build API URL for Autocomplete
            String encodedInput = URLEncoder.encode(input, "UTF-8");
            String apiUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
                    + encodedInput
                    + "&components=country:bd"
                    + "&types=geocode"
                    + "&key=" + apiKey;

            // Fetch and parse JSON
            JSONObject json = readJsonFromUrl(apiUrl);
            JSONArray predictions = json.getJSONArray("predictions");

            for (int i = 0; i < predictions.length(); i++) {
                JSONObject prediction = predictions.getJSONObject(i);
                String description = prediction.getString("description");
                results.add(description);
            }

        } catch (Exception e) {
            System.err.println("⚠️ Error fetching suggestions: " + e.getMessage());
        }

        return results;
    }

    // Helper method to fetch JSON data from a URL
    private JSONObject readJsonFromUrl(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        StringBuilder jsonBuilder = new StringBuilder();
        try (Scanner sc = new Scanner(new InputStreamReader(conn.getInputStream()))) {
            while (sc.hasNext()) jsonBuilder.append(sc.nextLine());
        }
        return new JSONObject(jsonBuilder.toString());
    }
}
