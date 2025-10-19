package org.example.aoopproject;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.util.*;
import java.util.stream.Collectors;

public class BusSearchService {

    private final HashSet<CompanyList> companies;
    private final StopLocationCache cache;
    private final GooglePlacesService placesService;
    private final Pane infoPane;
    private final double radiusMeters = 200.0; // YES_RADIUS_MATCH 200

    public BusSearchService(HashSet<CompanyList> companies, StopLocationCache cache, GooglePlacesService placesService, Pane infoPane){
        this.companies = companies;
        this.cache = cache;
        this.placesService = placesService;
        this.infoPane = infoPane;
    }

    // Resolve a place query to nearest internal stop
    public String resolveNearestStop(String query){
        if(query == null || query.isBlank()) return null;
        final String normalized = query.trim(); // final for lambda

        // Check for exact string match in any company
        Optional<String> exact = companies.stream()
                .flatMap(c -> c.getBusStopages().values().stream())
                .filter(s -> s != null && s.equalsIgnoreCase(normalized))
                .findFirst();
        if (exact.isPresent()) return exact.get();

        // Fetch lat/lng for user input using GooglePlacesService
        final LatLng placeLatLng;
        try {
            placeLatLng = placesService.getLatLngForPlace(query);
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }

        if(placeLatLng == null) return null;

        // Ensure cache contains all internal stops coordinates
        List<String> allStops = companies.stream()
                .flatMap(c -> c.getBusStopages().values().stream())
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        for(String stop : allStops){
            if(cache.get(stop) == null){
                try{
                    LatLng latLng = placesService.getLatLngForPlace(stop);
                    if(latLng != null) cache.put(stop, latLng);
                } catch(Exception ignored){}
            }
        }

        // Find nearest stop within radius
        final LatLng fixedLatLng = placeLatLng; // final for lambda
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

    // Display search result and company info in the infoPane
    public void searchRoute(String originStop, String destStop){
        if(originStop == null || destStop == null){
            Platform.runLater(() -> {
                infoPane.getChildren().clear();
                infoPane.getChildren().add(new Label("No matching stops found."));
            });
            return;
        }

        List<CompanyList> routeCompanies = companies.stream()
                .filter(c -> c.getBusStopages().containsValue(originStop) && c.getBusStopages().containsValue(destStop))
                .collect(Collectors.toList());

        Platform.runLater(() -> {
            infoPane.getChildren().clear();
            if(routeCompanies.isEmpty()){
                infoPane.getChildren().add(new Label("No single-company route found. Multi-company search not implemented yet."));
            } else {
                for(CompanyList company : routeCompanies){
                    infoPane.getChildren().add(new Label(company.toString()));
                }
            }
        });
    }
}