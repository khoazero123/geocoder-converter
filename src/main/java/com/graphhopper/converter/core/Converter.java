package com.graphhopper.converter.core;

import com.graphhopper.converter.api.GHResponse;
import com.graphhopper.converter.api.NominatimResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Robin Boldt
 */
public class Converter
{

    public static GHResponse convertFromNominatim( NominatimResponse response){
        return new GHResponse(response.getOsmId(), response.getLat(), response.getLon(), response.getDisplayName(), response.getAddress().getCountry(), response.getAddress().getCity());
    }

    public static List<GHResponse> convertFromNominatimList(List<NominatimResponse> nominatimResponses){

        List<GHResponse> ghResponses = new ArrayList<GHResponse>(nominatimResponses.size());

        for (NominatimResponse nominatimResponse: nominatimResponses)
        {
            ghResponses.add(convertFromNominatim(nominatimResponse));
        }

        return ghResponses;

    }

}
