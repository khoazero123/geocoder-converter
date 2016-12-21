package com.graphhopper.converter.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Robin Boldt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NominatimEntry {

    private long osmId;
    private String osmType;

    private double lat;
    private double lon;

    private String displayName;
    private String classString;

    // This is not the omsType, but for example "restaurant" or "tertiary"
    private String type;

    public Address address;

    public NominatimEntry(long osmId, String type, double lat, double lon, String displayName, String country, String city) {
        this.osmId = osmId;
        this.osmType = type;
        this.lat = lat;
        this.lon = lon;
        this.displayName = displayName;

        this.address = new Address();
        address.country = country;
        address.city = city;
    }

    public NominatimEntry() {
        this.address = new Address();
    }

    @JsonProperty("osm_id")
    public long getOsmId() {
        return osmId;
    }

    @JsonProperty("osm_id")
    public void setOsmId(long osmId) {
        this.osmId = osmId;
    }

    @JsonProperty("osm_type")
    public String getOsmType() {
        return osmType;
    }

    public String getGHOsmType() {
        if (osmType == null) {
            return null;
        }

        // convert node into N, way into W and relation into R
        return osmType.toUpperCase().substring(0, 1);
    }

    @JsonProperty("osm_type")
    public void setOsmType(String type) {
        this.osmType = type;
    }

    @JsonProperty
    public double getLat() {
        return lat;
    }

    @JsonProperty
    public void setLat(double lat) {
        this.lat = lat;
    }

    @JsonProperty
    public double getLon() {
        return lon;
    }

    @JsonProperty
    public void setLon(double lon) {
        this.lon = lon;
    }

    @JsonProperty("display_name")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("display_name")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @JsonProperty("class")
    public String getClassString() {
        return classString;
    }

    @JsonProperty("class")
    public void setClassString(String classString) {
        this.classString = classString;
    }

    @JsonProperty
    public String getType() {
        return type;
    }

    @JsonProperty
    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Address extends AbstractAddress{

        public Address() {
        }


    }
}
