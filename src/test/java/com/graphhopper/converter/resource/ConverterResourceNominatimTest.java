package com.graphhopper.converter.resource;

import com.graphhopper.converter.ConverterApplication;
import com.graphhopper.converter.ConverterConfiguration;
import com.graphhopper.converter.api.GHResponse;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author Robin Boldt
 */
public class ConverterResourceNominatimTest {
    @ClassRule
    public static final DropwizardAppRule<ConverterConfiguration> RULE =
            new DropwizardAppRule<>(ConverterApplication.class, ResourceHelpers.resourceFilePath("converter.yml"));
    private static Client client;

    @BeforeClass
    public static void setup() {
        client = new JerseyClientBuilder(RULE.getEnvironment()).build("client");

        client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
        client.property(ClientProperties.READ_TIMEOUT, 100000);
    }

    @Test
    public void testHandleForward() {
        Response response = client.target(
                String.format("http://localhost:%d/nominatim?q=berlin", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getLocale().equals("en"));

        // This might change in OSM and we might need to update this test then
        List<Double> extent = entry.getHits().get(0).getExtent().getExtent();
        assertEquals(13.3, extent.get(0), .1);
        assertEquals(52.3, extent.get(1), .1);
        assertEquals(13.5, extent.get(2), .1);
        assertEquals(52.7, extent.get(3), .1);
    }

    @Test
    public void testIssue38() {
        Response response = client.target(
                String.format("http://localhost:%d/nominatim?q=berlin", RULE.getLocalPort()))
                .request()
                .get();

        // Get the raw json String to check the structure of the json
        String responseString = response.readEntity(String.class);
        assertTrue(responseString.contains("\"extent\":["));
        assertFalse(responseString.contains("\"extent\":{\"extent\""));
    }

    @Test
    public void testHandleReverse() {
        Response response = client.target(
                String.format("http://localhost:%d/nominatim?point=52.5487429714954,-1.81602098644987&reverse=true", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    public void testCorrectLocale() {
        Response response = client.target(
                String.format("http://localhost:%d/nominatim?q=berlin&locale=de", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getLocale().equals("de"));
    }

    @Test
    public void testCorrectLocaleCountry() {
        Response response = client.target(
                String.format("http://localhost:%d/nominatim?q=berlin&locale=de-ch", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getLocale().equals("de-CH"));
    }

    @Test
    public void testIncorrectLocale() {
        Response response = client.target(
                String.format("http://localhost:%d/nominatim?q=berlin&locale=IAmNotValid", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getLocale().equals("en"));
    }

    @Test
    public void testIncorrectLocaleCountry() {
        Response response = client.target(
                String.format("http://localhost:%d/nominatim?q=berlin&locale=de-zz", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getLocale().equals("en"));
    }

    @Test
    public void testIncorrectFormattedPoint() {
        Response response = client.target(
                String.format("http://localhost:%d/nominatim?reverse=true&point=NaN,NaN", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    public void testIssue50() {
        Response response = client.target(
                String.format("http://localhost:%d/nominatim?point=48.4882,2.6996&reverse=true", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);

        // "Seine-et-Marne" or "Fontainebleau" seems to be valid: https://en.wikipedia.org/wiki/Fontainebleau
        // "Fontainebleau is a sub-prefecture of the Seine-et-Marne department"
        assertEquals("Seine-et-Marne", entry.getHits().get(0).getCounty());
    }
}
