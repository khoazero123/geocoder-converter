package com.graphhopper.converter.resources;

import javax.ws.rs.BadRequestException;

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
            if (point == null || point.isEmpty()) {
                throw new BadRequestException("When setting reverse=true you have to pass the point parameter");
            }
            String[] cords = point.split(",");
            if (cords.length != 2) {
                throw new BadRequestException("You have to pass the point in the format \"lat,lon\"");
            }
            double lat = Double.parseDouble(cords[0]);
            double lon = Double.parseDouble(cords[1]);
        } else {
            if (query == null || query.isEmpty()) {
                throw new BadRequestException("q cannot be empty");
            }
        }
    }

}
