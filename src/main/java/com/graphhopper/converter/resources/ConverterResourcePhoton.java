package com.graphhopper.converter.resources;

import com.codahale.metrics.annotation.Timed;
import com.graphhopper.converter.api.PhotonResponse;
import com.graphhopper.converter.api.Status;
import com.graphhopper.converter.core.Converter;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

/**
 * @author Robin Boldt
 */
@Path("/photon")
@Produces("application/json; charset=utf-8")
public class ConverterResourcePhoton extends AbstractConverterResource {

    private final String photonUrl;
    private final String photonReverseUrl;
    private final Client jerseyClient;
    private final Set<String> supportedLanguages;

    public ConverterResourcePhoton(String photonUrl, String photonReverseUrl, Set<String> supportedLanguages, Client jerseyClient) {
        this.photonUrl = photonUrl;
        this.photonReverseUrl = photonReverseUrl;
        this.supportedLanguages = supportedLanguages;
        this.jerseyClient = jerseyClient;
    }

    @GET
    @Timed
    public Response handle(@QueryParam("q") @DefaultValue("") String query,
                           @QueryParam("limit") @DefaultValue("5") int limit,
                           @QueryParam("locale") @DefaultValue("") String locale,
                           @QueryParam("bbox") @DefaultValue("") String bbox,
                           @QueryParam("location_bias_scale") @DefaultValue("") String locationBiasScale,
                           @QueryParam("osm_tag") List<String> osmTags,
                           @QueryParam("reverse") @DefaultValue("false") boolean reverse,
                           @QueryParam("point") @DefaultValue("") String point
    ) {
        limit = fixLimit(limit);
        checkInvalidParameter(reverse, query, point);

        WebTarget target;
        if (reverse) {
            target = buildReverseTarget(point);
        } else {
            target = buildForwardTarget(query);
        }

        target = target.
                queryParam("limit", limit);

        if (!point.isEmpty()) {
            String[] cords = point.split(",");
            String lat = cords[0];
            String lon = cords[1];
            target = target.queryParam("lat", lat).
                    queryParam("lon", lon);
        }

        if (!locale.isEmpty()) {
            if (!supportedLanguages.contains(locale))
                locale = "en";
            locale = getLocaleFromParameter(locale);
            target = target.queryParam("lang", locale);
        }
        if (!bbox.isEmpty()) {
            target = target.queryParam("bbox", bbox);
        }
        if (!locationBiasScale.isEmpty()) {
            target = target.queryParam("location_bias_scale", locationBiasScale);
        }

        for (String osmTag : osmTags) {
            target = target.queryParam("osm_tag", osmTag);
        }

        Response response = target.request().accept("application/json").get();
        Status status = failIfResponseNotSuccessful(target, response);

        try {
            PhotonResponse photonResponse = response.readEntity(PhotonResponse.class);
            return Converter.convertFromPhoton(photonResponse, status, locale);
        } catch (Exception e) {
            LOGGER.error("There was an issue with the target " + target.getUri() + " the provider returned: " + status.code + " - " + status.message);
            throw new BadRequestException("error deserializing geocoding feed");
        } finally {
            response.close();
        }
    }

    private WebTarget buildForwardTarget(String query) {
        return jerseyClient.
                target(photonUrl).
                queryParam("q", query);
    }

    private WebTarget buildReverseTarget(String point) {
        return jerseyClient.
                target(photonReverseUrl);
    }
}
