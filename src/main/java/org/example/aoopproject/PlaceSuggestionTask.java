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
 * Enhanced Google Place Autocomplete fetcher.
 * - Prioritizes Dhaka (all places, bus stops, metro stations)
 * - Falls back to rest of Bangladesh
 * - Handles multi-stop text (e.g., "Mirpur-Gul" → query only "Gul")
 */
public class PlaceSuggestionTask extends Task<List<String>> {

    private static final long DEBOUNCE_DELAY_MS = 300;
    private static final String COUNTRY_CODE = "bd";
    private static final String DHAKA_LAT = "23.8103";
    private static final String DHAKA_LON = "90.4125";
    private static final int DHAKA_RADIUS = 30000; // 30 km radius

    private final String input;
    private final String apiKey;

    public PlaceSuggestionTask(String input, String apiKey) {
        // Extract last segment if multi-stop text like "Mirpur-Gulshan"
        if (input != null && input.contains("-")) {
            String[] parts = input.split("-");
            this.input = parts[parts.length - 1].trim();
        } else {
            this.input = input != null ? input.trim() : "";
        }
        this.apiKey = apiKey;
    }

    @Override
    protected List<String> call() throws Exception {
        Thread.sleep(DEBOUNCE_DELAY_MS);

        if (isCancelled() || input == null || input.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> results;

        try {
            // 🔹 Try Dhaka-focused suggestions first (includes bus/metro)
            results = fetchSuggestions(input, true);

            // 🔹 Fallback: search entire Bangladesh
            if (results.isEmpty()) {
                results = fetchSuggestions(input, false);
            }

        } catch (Exception e) {
            System.err.println("⚠️ Error fetching suggestions: " + e.getMessage());
            results = new ArrayList<>();
        }

        return results;
    }

    private List<String> fetchSuggestions(String query, boolean dhakaPriority) throws Exception {
        List<String> suggestions = new ArrayList<>();

        String encodedInput = URLEncoder.encode(query, "UTF-8");
        StringBuilder apiUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json?");
        apiUrl.append("input=").append(encodedInput)
                .append("&components=country:").append(COUNTRY_CODE)
                .append("&types=establishment|transit_station|point_of_interest|geocode")
                .append("&key=").append(apiKey);

        // Add Dhaka bias if required
        if (dhakaPriority) {
            apiUrl.append("&location=").append(DHAKA_LAT).append(",").append(DHAKA_LON)
                    .append("&radius=").append(DHAKA_RADIUS);
        }

        JSONObject json = readJsonFromUrl(apiUrl.toString());
        JSONArray predictions = json.optJSONArray("predictions");
        if (predictions == null) return suggestions;

        for (int i = 0; i < predictions.length(); i++) {
            JSONObject prediction = predictions.getJSONObject(i);
            String description = prediction.optString("description", "");
            JSONArray types = prediction.optJSONArray("types");

            // 🔹 Filter preference for bus, metro, train, or popular place types
            if (dhakaPriority) {
                if (types != null) {
                    for (int t = 0; t < types.length(); t++) {
                        String type = types.getString(t);
                        if (isTransitOrPlace(type)) {
                            suggestions.add(description);
                            break;
                        }
                    }
                } else {
                    suggestions.add(description);
                }
            } else {
                suggestions.add(description);
            }
        }

        return suggestions;
    }

    private boolean isTransitOrPlace(String type) {
        return type.contains("bus_station") ||
                type.contains("transit_station") ||
                type.contains("train_station") ||
                type.contains("subway_station") ||
                type.contains("point_of_interest") ||
                type.contains("establishment");
    }

    private JSONObject readJsonFromUrl(String urlString) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);

        StringBuilder jsonBuilder = new StringBuilder();
        try (Scanner sc = new Scanner(new InputStreamReader(conn.getInputStream()))) {
            while (sc.hasNext()) jsonBuilder.append(sc.nextLine());
        }

        return new JSONObject(jsonBuilder.toString());
    }
}
