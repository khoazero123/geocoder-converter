package com.graphhopper.converter.core;

import com.graphhopper.converter.api.NominatimResponse;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author Robin Boldt
 */
public class NominatimRequest
{

    private final Client client;
    private List<NominatimResponse> nominatimResponses;

    public NominatimRequest( Client client )
    {
        this.client = client;
        this.execute();
    }

    private void execute()
    {
        /*
        NominatimResponse response = client.
            target("http://nominatim.openstreetmap.org/search/").
            path("Unter%20den%20Linden?city=berlin&format=json&addressdetails=1").
                request().
                get(NominatimResponse.class);

        */

        Response response = client.
                target("http://nominatim.openstreetmap.org/search/").
                path("Unter%20den%20Linden?city=berlin&format=json&addressdetails=1").
                request().
                accept("application/json").
                get();

        System.out.println("Answer received");

        List<NominatimResponse> entitiesFromResponse = response.readEntity(new GenericType<List<NominatimResponse>>(){});
        nominatimResponses = entitiesFromResponse;
    }

    public List<NominatimResponse> getNominatimResponses()
    {
        return nominatimResponses;
    }
}
