package com.graphhopper.converter.resources;

import com.codahale.metrics.annotation.Timed;
import com.graphhopper.converter.api.NominatimEntry;
import com.graphhopper.converter.api.Status;
import com.graphhopper.converter.core.Converter;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Robin Boldt
 */
@Path("/nominatim")
@Produces(MediaType.APPLICATION_JSON)
public class ConverterResourceNominatim {

    private final String nominatimUrl;
    private final String nominatimReverseUrl;
    private final String nominatimEmail;
    private final Client jerseyClient;

    public ConverterResourceNominatim(String nominatimUrl, String nominatimReverseUrl, String nominatimEmail, Client jerseyClient) {
        this.nominatimUrl = nominatimUrl;
        this.nominatimReverseUrl = nominatimReverseUrl;
        this.nominatimEmail = nominatimEmail;
        this.jerseyClient = jerseyClient;
    }

    @GET
    @Timed
    public Response handle(@QueryParam("q") @DefaultValue("") String query,
                           @QueryParam("limit") @DefaultValue("5") int limit,
                           @QueryParam("locale") @DefaultValue("") String locale,
                           @QueryParam("viewbox") @DefaultValue("") String viewbox,
                           @QueryParam("viewboxlbrt") @DefaultValue("") String viewboxlbrt,
                           @QueryParam("bounded") @DefaultValue("") String bounded,
                           @QueryParam("reverse") @DefaultValue("false") boolean reverse,
                           @QueryParam("point") @DefaultValue("false") String point
    ) {
        if (limit > 10) {
            limit = 10;
        }

        WebTarget target;
        if (reverse) {
            target = buildReverseTarget(point);
        } else {
            target = buildForwardTarget(query);
        }

        target = target.
                queryParam("limit", limit).
                queryParam("format", "json").
                queryParam("email", nominatimEmail).
                queryParam("addressdetails", "1");

        if (!locale.isEmpty()) {
            target = target.queryParam("locale", locale);
        }
        if (!viewbox.isEmpty()) {
            target = target.queryParam("viewbox", viewbox);
        }
        if (!viewboxlbrt.isEmpty()) {
            target = target.queryParam("viewboxlbrt", viewboxlbrt);
        }
        if (!bounded.isEmpty()) {
            target = target.queryParam("bounded", bounded);
        }

        Response response = target.request().accept("application/json").
                get();

        Status status = new Status(response.getStatus(), response.getStatusInfo().getReasonPhrase());
        List<NominatimEntry> entitiesFromResponse;
        if (reverse) {
            entitiesFromResponse = new ArrayList<>(1);
            entitiesFromResponse.add(0, response.readEntity(NominatimEntry.class));
        } else {
            entitiesFromResponse = response.readEntity(new GenericType<List<NominatimEntry>>() {
            });
        }
        return Converter.convertFromNominatimList(entitiesFromResponse, status);
    }

    private WebTarget buildForwardTarget(String query) {
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("The q parameter cannot be empty");
        }

        return jerseyClient.
                target(nominatimUrl).
                queryParam("q", query);
    }

    private WebTarget buildReverseTarget(String point) {
        if (point == null || point.isEmpty()) {
            throw new IllegalArgumentException("When setting reverse=true you have to pass the point parameter");
        }
        String[] cords = point.split(",");
        if (cords.length != 2) {
            throw new IllegalArgumentException("You have to pass the point in the format \"lat,lon\"");
        }

        String lat = cords[0];
        String lon = cords[1];
        return jerseyClient.
                target(nominatimReverseUrl).
                queryParam("lat", lat).
                queryParam("lon", lon);
    }
}
