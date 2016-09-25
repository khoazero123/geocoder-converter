package com.graphhopper.converter.core;

import com.graphhopper.converter.api.*;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;

/**
 * @author Robin Boldt
 */
public class Converter {

    public static GHEntry convertFromNominatim(NominatimEntry response) {
        GHEntry rsp = new GHEntry(response.getOsmId(), response.getGHOsmType(), response.getLat(), response.getLon(), response.getDisplayName(),
                response.getAddress().country, response.getAddress().getGHCity(), response.getAddress().state, response.getStreetName(), response.getAddress().houseNumber);
        return rsp;
    }

    public static Response convertFromNominatimList(List<NominatimEntry> nominatimResponses, Status status, String locale) {
        GHResponse ghResponse = new GHResponse(nominatimResponses.size());
        for (NominatimEntry nominatimResponse : nominatimResponses) {
            ghResponse.add(convertFromNominatim(nominatimResponse));
        }

        ghResponse.addCopyright("OpenStreetMap").addCopyright("GraphHopper");
        if(!locale.isEmpty()){
            ghResponse.setLocale(locale);
        }
        return createResponse(ghResponse, status);
    }

    public static GHEntry convertFromOpenCageData(OpenCageDataEntry response) {
        Long osmId = -1L;
        String type = "N";

        // extract OSM id and type from OSM annotation
        if (response.getAnnotations() != null && response.getAnnotations().osm != null && response.getAnnotations().osm.editUrl != null) {
            String url = response.getAnnotations().osm.editUrl;
            // skip nodeId;
            int index = url.indexOf("?");
            int index2 = url.indexOf("#");
            if (index > 0 && index2 > 0) {
                char typeChar = url.charAt(index + 1);
                if (typeChar == 'w') {
                    try {
                        // ?way=
                        osmId = Long.parseLong(url.substring(index + 5, index2));
                        type = "W";
                    } catch (Exception ex) {
                    }
                } else if (typeChar == 'r') {
                    try {
                        // ?relation=
                        osmId = Long.parseLong(url.substring(index + 10, index2));
                        type = "R";
                    } catch (Exception ex) {
                    }
                } else {
                    try {
                        // ?node=
                        osmId = Long.parseLong(url.substring(index + 6, index2));
                    } catch (Exception ex) {
                    }
                }
            }
        }

        GHEntry rsp = new GHEntry(osmId, type, response.getGeometry().lat, response.getGeometry().lng,
                response.getFormatted(), response.getComponents().country, response.getComponents().getGHCity(),
                response.getComponents().state, response.getStreetName(), response.getComponents().houseNumber);

        return rsp;
    }

    public static Response convertFromOpenCageData(OpenCageDataResponse ocdRsp, Status status, String locale) {
        List<OpenCageDataEntry> ocdEntries = ocdRsp.results;
        GHResponse ghResponse = new GHResponse(ocdEntries.size());
        for (OpenCageDataEntry ocdResponse : ocdEntries) {
            ghResponse.add(convertFromOpenCageData(ocdResponse));
        }

        ghResponse.addCopyright("OpenCageData").addCopyright("OpenStreetMap").addCopyright("GraphHopper");
        if(!locale.isEmpty()){
            ghResponse.setLocale(locale);
        }
        return createResponse(ghResponse, status);
    }

    public static Response createResponse(GHResponse ghResponse, Status status) {
        if (status.code == 200) {

            return Response.status(Response.Status.OK).
                    entity(ghResponse).
                    build();
        } else {

            HashMap map = new HashMap();
            map.put("message", status.message);
            return Response.status(status.code).
                    entity(map).
                    build();
        }
    }
}
