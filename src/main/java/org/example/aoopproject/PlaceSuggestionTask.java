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
 * Thread task to fetch Google Place Autocomplete suggestions.
 * Prioritizes Dhaka first, then falls back to the rest of Bangladesh.
 * Handles multi-stop text (e.g. "Mirpur-Gul" → query only "Gul").
 */
public class PlaceSuggestionTask extends Task<List<String>> {

    private static final long DEBOUNCE_DELAY_MS = 300;
    private final String input;
    private final String apiKey;

    public PlaceSuggestionTask(String input, String apiKey) {
        // 🧩 Extract only the last segment after '-'
        if (input != null && input.contains("-")) {
            String[] parts = input.split("-");
            this.input = parts[parts.length - 1].trim();  // only use last segment
        } else {
            this.input = input != null ? input.trim() : "";
        }
        this.apiKey = apiKey;
    }

    @Override
    protected List<String> call() throws Exception {
        Thread.sleep(DEBOUNCE_DELAY_MS);

        List<String> results = new ArrayList<>();
        if (input == null || input.isEmpty()) return results;

        try {
            // Try places in Dhaka first
            results = fetchSuggestions(input, "bd", "Dhaka");
            // If Dhaka returns empty, fallback to entire Bangladesh
            if (results.isEmpty()) {
                results = fetchSuggestions(input, "bd", null);
            }

        } catch (Exception e) {
            System.err.println("⚠️ Error fetching suggestions: " + e.getMessage());
        }

        return results;
    }

    private List<String> fetchSuggestions(String query, String countryCode, String cityFilter) throws Exception {
        List<String> suggestions = new ArrayList<>();

        String encodedInput = URLEncoder.encode(query, "UTF-8");
        String apiUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
                + encodedInput
                + "&components=country:" + countryCode
                + "&types=geocode"
                + "&key=" + apiKey;

        JSONObject json = readJsonFromUrl(apiUrl);
        JSONArray predictions = json.optJSONArray("predictions");
        if (predictions == null) return suggestions;

        for (int i = 0; i < predictions.length(); i++) {
            JSONObject prediction = predictions.getJSONObject(i);
            String description = prediction.getString("description");

            if (cityFilter == null || description.toLowerCase().contains(cityFilter.toLowerCase())) {
                suggestions.add(description);
            }
        }

        return suggestions;
    }

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
