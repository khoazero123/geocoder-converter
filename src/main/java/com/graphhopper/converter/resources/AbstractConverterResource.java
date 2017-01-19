package com.graphhopper.converter.resources;

import javax.ws.rs.BadRequestException;
import java.util.Locale;
import java.util.MissingResourceException;

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
            double lat;
            double lon;
            try {
                lat = Double.parseDouble(cords[0]);
                lon = Double.parseDouble(cords[1]);
            } catch (RuntimeException e) {
                throw new BadRequestException("The coordinates of point need to be valid numbers");
            }
            if (Double.isNaN(lat) || Double.isNaN(lon) || lat < -180 || lat > 180 || lon < -90 || lon > 90) {
                throw new BadRequestException("The coordinates of point need to be valid coordinates");
            }
        } else {
            if (query == null || query.isEmpty()) {
                throw new BadRequestException("q cannot be empty");
            }
        }
    }

    String getLocaleFromParameter(String locale){
        Locale lo = Locale.forLanguageTag(locale);
        if (isValid(lo)) {
            return lo.toLanguageTag();
        } else {
            return "en";
        }
    }

    boolean isValid(Locale locale) {
        try {
            return locale.getISO3Language() != null && locale.getISO3Country() != null && !locale.toLanguageTag().equals("und");
        } catch (MissingResourceException e) {
            return false;
        }
    }
}
