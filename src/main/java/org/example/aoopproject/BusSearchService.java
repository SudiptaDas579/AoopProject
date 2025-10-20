package org.example.aoopproject;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.util.*;
import java.util.stream.Collectors;

public class BusSearchService {

    private final HashSet<CompanyList> companies;
    private final StopLocationCache cache;
    private final GooglePlacesService placesService;
    private final Pane infoPane;
    private final double radiusMeters = 1000;

    public BusSearchService(HashSet<CompanyList> companies, StopLocationCache cache,
                            GooglePlacesService placesService, Pane infoPane) {
        this.companies = companies;
        this.cache = cache;
        this.placesService = placesService;
        this.infoPane = infoPane;
    }

    // Resolve nearest stop for a query
    public String resolveNearestStop(String query) {
        if (query == null || query.isBlank()) return null;
        final String normalized = query.trim(); // final for lambda

        Optional<String> exact = companies.stream()
                .flatMap(c -> c.getBusStopages().values().stream())
                .filter(s -> s != null && s.equalsIgnoreCase(normalized))
                .findFirst();
        if (exact.isPresent()) return exact.get();

        final LatLng placeLatLng;
        try {
            placeLatLng = placesService.getLatLngForPlace(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (placeLatLng == null) return null;

        // Populate cache
        List<String> allStops = companies.stream()
                .flatMap(c -> c.getBusStopages().values().stream())
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        for (String stop : allStops) {
            if (cache.get(stop) == null) {
                try {
                    LatLng latLng = placesService.getLatLngForPlace(stop);
                    if (latLng != null) cache.put(stop, latLng);
                } catch (Exception ignored) {}
            }
        }

        final LatLng fixedLatLng = placeLatLng;
        Optional<String> nearest = cache.stops().stream()
                .filter(s -> DistanceUtil.isWithinRadius(
                        fixedLatLng.getLat(),
                        fixedLatLng.getLng(),
                        cache.get(s).getLat(),
                        cache.get(s).getLng(),
                        radiusMeters))
                .findFirst();

        return nearest.orElse(null);
    }

    // Search and display routes
    public void searchRoute(String originStop, String destStop) {
        if (originStop == null || destStop == null) {
            Platform.runLater(() -> {
                infoPane.getChildren().clear();
                infoPane.getChildren().add(new Label("No matching stops found."));
            });
            return;
        }

        List<CompanyList> routeCompanies = companies.stream()
                .filter(c -> c.getBusStopages().containsValue(originStop) &&
                        c.getBusStopages().containsValue(destStop))
                .collect(Collectors.toList());

        Platform.runLater(() -> {
            infoPane.getChildren().clear();
            if (routeCompanies.isEmpty()) {
                infoPane.getChildren().add(new Label("No single-company route found."));
            } else {
                double yOffset = 10;
                for (CompanyList company : routeCompanies) {
                    Button companyBtn = new Button(company.getCompanyName());
                    companyBtn.setLayoutX(10);
                    companyBtn.setLayoutY(yOffset);
                    companyBtn.setPrefWidth(infoPane.getPrefWidth() - 50);
                    companyBtn.setPrefHeight(30);

                    companyBtn.setOnAction(e -> {
                        // Compute fare
                        int totalFare = calculateFare(company, originStop, destStop);
                        int halfFare = totalFare / 2;

                        // Show pop-up info
                        Platform.runLater(() -> {
                            infoPane.getChildren().clear();
                            Label infoLabel = new Label("Company: " + company.getCompanyName() +
                                    "\nFull Fare: " + totalFare + " tk" +
                                    "\nHalf Fare: " + halfFare + " tk");
                            infoLabel.setLayoutX(10);
                            infoLabel.setLayoutY(10);
                            infoPane.getChildren().add(infoLabel);

                            // Optional: show bus numbers as buttons
                            double busYOffset = 70;
                            for (BusInformation bus : company.getBusInfo()) {
                                Button busBtn = new Button(bus.getBusNo());
                                busBtn.setLayoutX(10);
                                busBtn.setLayoutY(busYOffset);
                                busBtn.setPrefWidth(100);
                                busBtn.setPrefHeight(25);
                                busBtn.setOnAction(ev -> {
                                    // Show bus details popup
                                    System.out.println("Bus info: " + bus);
                                });
                                infoPane.getChildren().add(busBtn);
                                busYOffset += 35;
                            }
                        });
                    });

                    infoPane.getChildren().add(companyBtn);
                    yOffset += 50;
                }
            }
        });
    }

    // Calculate total fare using segment fares
    private int calculateFare(CompanyList company, String origin, String dest) {
        Map<Integer, String> stops = company.getBusStopages();
        Map<Integer, String> fares = company.getFareList(); // Integer -> String now

        // Find indices
        int originIndex = stops.entrySet().stream()
                .filter(e -> e.getValue().equalsIgnoreCase(origin))
                .map(Map.Entry::getKey)
                .findFirst().orElse(-1);
        int destIndex = stops.entrySet().stream()
                .filter(e -> e.getValue().equalsIgnoreCase(dest))
                .map(Map.Entry::getKey)
                .findFirst().orElse(-1);

        if (originIndex == -1 || destIndex == -1) return 0;

        // Ensure correct direction
        int start = Math.min(originIndex, destIndex);
        int end = Math.max(originIndex, destIndex);

        int sumFare = 0;
        for (int i = start; i < end; i++) {
            String fareStr = fares.getOrDefault(i, "0");
            try {
                sumFare += Integer.parseInt(fareStr);
            } catch (NumberFormatException e) {
                // If not a number, ignore or treat as 0
            }
        }
        return sumFare;
    }

}
