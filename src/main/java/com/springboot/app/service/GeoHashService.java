package com.springboot.app.service;

import ch.hsr.geohash.GeoHash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeoHashService {

    private static final Logger logger = LoggerFactory.getLogger(GeoHashService.class);

    public String generateGeoHash(double latitude, double longitude, int precision) {
        if (logger.isDebugEnabled()) {
            logger.debug("Generating GeoHash for latitude: {}, longitude: {}, precision: {}", latitude, longitude, precision);
        }
        return GeoHash.withCharacterPrecision(latitude, longitude, precision).toBase32();
    }

    public List<String> getNearbyGeoHashes(double latitude, double longitude, int precision) {
        if (logger.isDebugEnabled()) {
            logger.debug("Getting nearby GeoHashes for latitude: {}, longitude: {}, precision: {}", latitude, longitude, precision);
        }
        GeoHash geoHash = GeoHash.withCharacterPrecision(latitude, longitude, precision);
        List<String> nearbyHashes = new ArrayList<>();
        for (GeoHash neighbor : geoHash.getAdjacent()) {
            nearbyHashes.add(neighbor.toBase32());
        }
        nearbyHashes.add(geoHash.toBase32());
        if (logger.isDebugEnabled()) {
            logger.debug("Found {} nearby GeoHashes: {}", nearbyHashes.size(), nearbyHashes);
        }
        return nearbyHashes;
    } 
}
