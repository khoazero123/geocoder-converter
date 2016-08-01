package com.graphhopper.converter.resources;

/**
 * @author Robin Boldt
 */
abstract class AbstractConverterResource {

    int fixLimit(int limit) {
        if (limit > 10) {
            return 10;
        }
        return limit;
    }

    void checkInvalidParameter(boolean reverse, String query, String point) {
        if (reverse) {
            String[] cords = point.split(",");
            if (cords.length != 2) {
                throw new IllegalArgumentException("You have to pass the point in the format \"lat,lon\"");
            }
            double lat = Double.parseDouble(cords[0]);
            double lon = Double.parseDouble(cords[1]);
        } else {
            if (query.isEmpty()) {
                throw new IllegalArgumentException("q cannot be empty");
            }
        }
    }

}
