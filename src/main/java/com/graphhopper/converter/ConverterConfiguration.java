package com.graphhopper.converter;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author Robin Boldt
 */
public class ConverterConfiguration extends Configuration {

    @NotEmpty
    private String nominatimUrl = "http://nominatim.openstreetmap.org/search/";

    @Valid
    @NotNull
    private final JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();
    private String ipBlackList = "";
    private String ipWhiteList = "";

    @JsonProperty
    public String getNominatimUrl() {
        return nominatimUrl;
    }

    @JsonProperty
    public void setNominatimUrl(String url) {
        this.nominatimUrl = url;
    }

    @JsonProperty("jerseyClient")
    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return jerseyClient;
    }

    @JsonProperty(value = "ipBlackList")
    public String getIPBlackList() {
        return ipBlackList;
    }

    @JsonProperty(value = "ipBlackList")
    public void setIPBlackList(String ipBlackList) {
        this.ipBlackList = ipBlackList;
    }

    @JsonProperty(value = "ipWhiteList")
    public String getIPWhiteList() {
        return ipWhiteList;
    }

    @JsonProperty(value = "ipWhiteList")
    public void setIPWhiteList(String ipWhiteList) {
        this.ipWhiteList = ipWhiteList;
    }
}
