package com.graphhopper.converter.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Robin Boldt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenCageDataEntry {

    private String formatted;
    private OCDAnnoations annotations;
    private OCDGeometry geometry;
    public OCDComponents components;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OCDAnnoations {

        public OCDAnnoations() {
        }

        @JsonProperty("OSM")
        public OCDAnnotationOSM osm = new OCDAnnotationOSM();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OCDAnnotationOSM {

        public OCDAnnotationOSM() {
        }

        @JsonProperty("edit_url")
        public String editUrl;
        public String url;
    }

    public static class OCDGeometry {

        public OCDGeometry() {
        }

        @JsonProperty("lat")
        public double lat;
        @JsonProperty("lng")
        public double lng;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OCDComponents extends AbstractAddress{

        public OCDComponents() {
        }

        @JsonProperty("_type")
        public String type;
        @JsonProperty("country_code")
        public String countryCode;
        @JsonProperty("county")
        public String county;
    }

    public OpenCageDataEntry() {
    }

    public OpenCageDataEntry(double lat, double lon, String displayName, OCDComponents components) {
        this.formatted = displayName;
        this.geometry = new OCDGeometry();
        this.geometry.lat = lat;
        this.geometry.lng = lon;
        this.components = components;
    }

    @JsonProperty("formatted")
    public String getFormatted() {
        return formatted;
    }

    @JsonProperty("formatted")
    public void setFormatted(String displayName) {
        this.formatted = displayName;
    }

    @JsonProperty("components")
    public OCDComponents getComponents() {
        return components;
    }

    @JsonProperty("components")
    public void setOCDComponents(OCDComponents comp) {
        this.components = comp;
    }

    @JsonProperty("geometry")
    public OCDGeometry getGeometry() {
        return geometry;
    }

    @JsonProperty("geometry")
    public void setGeometry(OCDGeometry geometry) {
        this.geometry = geometry;
    }

    @JsonProperty("annotations")
    public OCDAnnoations getAnnotations() {
        return annotations;
    }

    @JsonProperty("annotations")
    public void setAnnotations(OCDAnnoations annotations) {
        this.annotations = annotations;
    }
}
