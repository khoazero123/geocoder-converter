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

    private Address address;

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

    public boolean isStreet() {
        return "highway".equals(this.classString);
    }

    /**
     * Returns the Street name if this entry is a street, return null otherwise.
     * We return null, since we do not serialize null properties.
     */
    public String getStreetName() {
        if (!isStreet()) {
            return null;
        }
        if (this.address.road != null) {
            return this.address.road;
        }
        if (this.address.pedestrian != null) {
            return this.address.pedestrian;
        }

        throw new IllegalStateException("If entry is a street, we have to return a street for: " + this.displayName);
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Address {

        public Address() {
        }

        @JsonProperty
        public String country;
        @JsonProperty
        public String city;
        @JsonProperty
        public String state;
        @JsonProperty
        public String town;
        @JsonProperty
        public String village;
        @JsonProperty
        public String hamlet;
        @JsonProperty("road")
        public String road;
        @JsonProperty("pedestrian")
        public String pedestrian;

        public String getGHCity() {
            if (city != null) {
                return city;
            }
            if (town != null) {
                return town;
            }
            if (village != null) {
                return village;
            }
            return hamlet;
        }
    }
}
