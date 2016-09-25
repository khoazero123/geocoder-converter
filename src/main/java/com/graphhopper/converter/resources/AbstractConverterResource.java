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
            double lat = Double.parseDouble(cords[0]);
            double lon = Double.parseDouble(cords[1]);
        } else {
            if (query == null || query.isEmpty()) {
                throw new BadRequestException("q cannot be empty");
            }
        }
    }

    String getLocaleFromParameter(String locale){
        Locale lo = parseLocale(locale);
        if (isValid(lo)) {
            return lo.toLanguageTag();
        } else {
            return "en";
        }
    }

    // Taken from: http://stackoverflow.com/a/3684832/1548788
    Locale parseLocale(String locale) {
        String[] parts = locale.split("_");
        switch (parts.length) {
            case 3: return new Locale(parts[0], parts[1], parts[2]);
            case 2: return new Locale(parts[0], parts[1]);
            case 1: return new Locale(parts[0]);
            default: throw new IllegalArgumentException("Invalid locale: " + locale);
        }
    }

    boolean isValid(Locale locale) {
        try {
            return locale.getISO3Language() != null && locale.getISO3Country() != null;
        } catch (MissingResourceException e) {
            return false;
        }
    }
}
