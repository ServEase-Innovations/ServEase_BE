package com.springboot.app.service;

import ch.hsr.geohash.GeoHash;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeoHashService {

    public String generateGeoHash(double latitude, double longitude, int precision) {
        return GeoHash.withCharacterPrecision(latitude, longitude, precision).toBase32();
    }

    public List<String> getNearbyGeoHashes(double latitude, double longitude, int precision) {
        GeoHash geoHash = GeoHash.withCharacterPrecision(latitude, longitude, precision);
        List<String> nearbyHashes = new ArrayList<>();
        for (GeoHash neighbor : geoHash.getAdjacent()) {
            nearbyHashes.add(neighbor.toBase32());
        }
        nearbyHashes.add(geoHash.toBase32());
        return nearbyHashes;
    } 
}
