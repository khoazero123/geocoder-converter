package com.graphhopper.converter.resources;

import com.codahale.metrics.annotation.Timed;
import com.graphhopper.converter.api.GHResponse;
import com.graphhopper.converter.api.NominatimResponse;
import com.graphhopper.converter.core.Converter;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Robin Boldt
 */
@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
public class ConverterResource
{

    private final String nominatimUrl;
    private final AtomicLong counter;
    private final Client jerseyClient;

    public ConverterResource( String nominatimUrl, Client jerseyClient )
    {
        this.nominatimUrl = nominatimUrl;
        this.counter = new AtomicLong();
        this.jerseyClient = jerseyClient;
    }

    @GET
    @Timed
    public List<GHResponse> handle( @NotNull @QueryParam("q") String query,
                                    @QueryParam("limit") @DefaultValue("5") int limit,
                                    @QueryParam("countrycodes") @DefaultValue("") String countrycodes,
                                    @QueryParam("viewbox") @DefaultValue("") String viewbox,
                                    @QueryParam("viewboxlbrt") @DefaultValue("") String viewboxlbrt,
                                    @QueryParam("bounded") @DefaultValue("") String bounded
    )

    {
        if (limit > 10)
        {
            limit = 10;
        }

        WebTarget target = jerseyClient.
                target(nominatimUrl).
                queryParam("q", query).
                queryParam("limit", limit).
                queryParam("format", "json").
                queryParam("addressdetails", "1");

        if (!countrycodes.isEmpty())
        {
            target = target.queryParam("countrycodes", countrycodes);
        }
        if (!viewbox.isEmpty())
        {
            target = target.queryParam("viewbox", viewbox);
        }
        if (!viewboxlbrt.isEmpty())
        {
            target = target.queryParam("viewboxlbrt", viewboxlbrt);
        }
        if (!bounded.isEmpty())
        {
            target = target.queryParam("bounded", bounded);
        }


        Response response = target.
                request().
                accept("application/json").
                get();

        System.out.println("Answer received " + response);
        List<NominatimResponse> entitiesFromResponse = response.readEntity(new GenericType<List<NominatimResponse>>()
        {
        });
        return Converter.convertFromNominatimList(entitiesFromResponse);
    }
}
