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

    // This is not the omsType
    private String type;

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

    public String getStreetName() {
        if (this.address.road != null) {
            return this.address.road;
        }
        if (this.address.pedestrian != null) {
            return this.address.pedestrian;
        }
        if (this.address.path != null) {
            return this.address.path;
        }
        if (this.address.footway != null) {
            return this.address.footway;
        }
        return this.address.construction;
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
        @JsonProperty("house_number")
        public String houseNumber;
        @JsonProperty
        public String postcode;

        // Possible Street names TODO: Not sure what tags can be returned here
        @JsonProperty
        public String road;
        @JsonProperty
        public String pedestrian;
        @JsonProperty
        public String path;
        @JsonProperty
        public String footway;
        @JsonProperty
        public String construction;

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
