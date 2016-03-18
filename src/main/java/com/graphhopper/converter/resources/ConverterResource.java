package com.graphhopper.converter.resources;

import com.graphhopper.converter.api.GHResponse;
import com.graphhopper.converter.core.Converter;
import com.graphhopper.converter.core.NominatimRequest;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
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

    public ConverterResource( String nominatimUrl, Client jerseyClient ) {
        this.nominatimUrl = nominatimUrl;
        this.counter = new AtomicLong();
        this.jerseyClient = jerseyClient;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<GHResponse> test(){

        NominatimRequest request = new NominatimRequest(jerseyClient);

        return Converter.convertFromNominatimList(request.getNominatimResponses());
    }

}
