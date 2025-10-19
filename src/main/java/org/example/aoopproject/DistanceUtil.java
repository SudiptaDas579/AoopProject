package org.example.aoopproject;

public class DistanceUtil {

    // Haversine distance in meters between two lat/lng points
    public static double haversineMeters(double lat1, double lng1, double lat2, double lng2){
        final int R = 6371000; // Earth radius in meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    // Check if two points are within given radius (meters)
    public static boolean isWithinRadius(double lat1, double lng1, double lat2, double lng2, double radiusMeters){
        return haversineMeters(lat1,lng1,lat2,lng2) <= radiusMeters;
    }
}