package org.example.aoopproject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GooglePlacesService {

    private static final String API_KEY = "AIzaSyCqbKdjkod9FVs371m7I4Vv3B7opV2xfWI";

    public List<String> getSuggestions(String input) {
        if (input == null || input.isBlank()) return List.of();

        // 1) Try Dhaka only (strict)
        List<String> dhakaResults = query(input, true);
        if (!dhakaResults.isEmpty()) return dhakaResults;

        // 2) Fallback: Bangladesh only (not strict)
        return query(input, false);
    }

    private List<String> query(String input, boolean strictDhaka) {
        List<String> suggestions = new ArrayList<>();
        try {
            String encoded = URLEncoder.encode(input, StandardCharsets.UTF_8);
            String urlStr =
                    "https://maps.googleapis.com/maps/api/place/autocomplete/json"
                            + "?input=" + encoded
                            + "&key=" + API_KEY
                            + "&components=country:BD";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) response.append(line);

                JSONObject json = new JSONObject(response.toString());
                JSONArray preds = json.optJSONArray("predictions");
                if (preds == null) return suggestions;

                for (int i = 0; i < preds.length(); i++) {
                    String desc = preds.getJSONObject(i).getString("description");
                    if (strictDhaka) {
                        if (desc.toLowerCase().contains("dhaka")) {
                            suggestions.add(desc);
                        }
                    } else {
                        suggestions.add(desc);
                    }
                }
            }

        } catch (Exception ignored) {}

        return suggestions;
    }

    public LatLng getLatLngForPlace(String input) {
        if (input == null || input.isBlank()) return null;
        try {
            List<String> suggestions = getSuggestions(input);
            if (suggestions.isEmpty()) return null;
            String place = suggestions.get(0); // take first match

            // Make a Google Place Details API call to get lat/lng
            String encoded = URLEncoder.encode(place, StandardCharsets.UTF_8);
            String urlStr = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json"
                    + "?input=" + encoded
                    + "&inputtype=textquery"
                    + "&fields=geometry"
                    + "&key=" + API_KEY;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) response.append(line);

                JSONObject json = new JSONObject(response.toString());
                JSONArray candidates = json.optJSONArray("candidates");
                if (candidates != null && candidates.length() > 0) {
                    JSONObject loc = candidates.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                    return new LatLng(loc.getDouble("lat"), loc.getDouble("lng"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
