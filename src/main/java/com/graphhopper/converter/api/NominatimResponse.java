package com.graphhopper.converter.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Robin Boldt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NominatimResponse
{


    private int osmId;

    private double lat;
    private double lon;

    private String displayName;

    private Address address;

    public NominatimResponse( int osmId, double lat, double lon, String displayName, String country, String city )
    {
        this.osmId = osmId;
        this.lat = lat;
        this.lon = lon;
        this.displayName = displayName;

        this.address = new Address();
        address.setCountry(country);
        address.setCity(city);
    }

    public NominatimResponse()
    {
    }

    @JsonProperty("osm_id")
    public int getOsmId()
    {
        return osmId;
    }

    @JsonProperty("osm_id")
    public void setOsmId( int osmId )
    {
        this.osmId = osmId;
    }

    @JsonProperty
    public double getLat()
    {
        return lat;
    }

    @JsonProperty
    public void setLat( double lat )
    {
        this.lat = lat;
    }

    @JsonProperty
    public double getLon()
    {
        return lon;
    }

    @JsonProperty
    public void setLon( double lon )
    {
        this.lon = lon;
    }

    @JsonProperty("display_name")
    public String getDisplayName()
    {
        return displayName;
    }

    @JsonProperty("display_name")
    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    public Address getAddress()
    {
        return address;
    }

    public void setAddress( Address address )
    {
        this.address = address;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Address
    {

        public Address()
        {
        }

        private String country;
        private String city;
        private String town;
        private String village;

        public String getGHCity(){
            if(city != null){
                return city;
            }
            if(town != null){
                return town;
            }
            return village;
        }

        @JsonProperty
        public String getCountry()
        {
            return country;
        }

        @JsonProperty
        public void setCountry( String country )
        {
            this.country = country;
        }

        @JsonProperty
        public String getCity()
        {
            return city;
        }

        @JsonProperty
        public void setCity( String city )
        {
            this.city = city;
        }

        @JsonProperty
        public String getVillage()
        {
            return village;
        }

        @JsonProperty
        public void setVillage( String village )
        {
            this.village = village;
        }

        @JsonProperty
        public String getTown()
        {
            return town;
        }

        @JsonProperty
        public void setTown( String town )
        {
            this.town = town;
        }
    }
}
