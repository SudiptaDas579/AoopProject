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
 * Fetches Google Place Autocomplete suggestions.
 * ✅ Returns *all* place types (not just bus/metro)
 * ✅ Focused on Bangladesh, prioritizes Dhaka
 * ✅ Includes simple logging for debugging
 */
public class PlaceSuggestionTask extends Task<List<String>> {

    private static final String COUNTRY_CODE = "bd";
    private static final String DHAKA_LAT = "23.8103";
    private static final String DHAKA_LON = "90.4125";
    private static final int DHAKA_RADIUS = 30000; // 30 km

    private final String input;
    private final String apiKey;

    public PlaceSuggestionTask(String input, String apiKey) {
        this.input = input == null ? "" : input.trim();
        this.apiKey = apiKey;
    }

    @Override
    protected List<String> call() throws Exception {
        if (input.isEmpty()) return new ArrayList<>();

        // build API URL
        String encoded = URLEncoder.encode(input, "UTF-8");
        String apiUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json?" +
                "input=" + encoded +
                "&components=country:" + COUNTRY_CODE +
                "&location=" + DHAKA_LAT + "," + DHAKA_LON +
                "&radius=" + DHAKA_RADIUS +
                "&key=" + apiKey;

        System.out.println("🌐 Fetching from: " + apiUrl);

        List<String> suggestions = new ArrayList<>();
        try {
            JSONObject json = readJsonFromUrl(apiUrl);

            if (json.has("status"))
                System.out.println("📡 API Status: " + json.getString("status"));

            JSONArray preds = json.optJSONArray("predictions");
            if (preds == null || preds.isEmpty()) {
                System.out.println("⚠️ No predictions found");
                return suggestions;
            }

            for (int i = 0; i < preds.length(); i++) {
                JSONObject obj = preds.getJSONObject(i);
                String description = obj.optString("description", "");
                if (!description.isEmpty()) suggestions.add(description);
            }

            System.out.println("✅ Found " + suggestions.size() + " suggestions");
        } catch (Exception e) {
            System.err.println("❌ Error fetching suggestions: " + e.getMessage());
        }

        return suggestions;
    }

    private JSONObject readJsonFromUrl(String urlString) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(4000);
        conn.setReadTimeout(4000);

        StringBuilder jsonBuilder = new StringBuilder();
        try (Scanner sc = new Scanner(new InputStreamReader(conn.getInputStream()))) {
            while (sc.hasNext()) jsonBuilder.append(sc.nextLine());
        }

        return new JSONObject(jsonBuilder.toString());
    }
}
