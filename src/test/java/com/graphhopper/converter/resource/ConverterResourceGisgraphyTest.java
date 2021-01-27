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

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Robin Boldt
 */
public class ConverterResourceGisgraphyTest {
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
                String.format("http://localhost:%d/gisgraphy?q=berlin", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getHits().size() > 0);

        //now try with an Address
        response = client.target(
                String.format("http://localhost:%d/gisgraphy?q=103+avenue+des+champs+elysees,paris", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getHits().size() > 0);
    }

    @Test
    public void testHandleReverse() {
        Response response = client.target(
                String.format("http://localhost:%d/gisgraphy/?point=52.5487429714954,-1.81602098644987&reverse=true", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getHits().size() > 0);
    }

    @Test
    public void testHandleAutocomplete() {
        Response response = client.target(
                String.format("http://localhost:%d/gisgraphy?q=pari&autocomplete=true", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getHits().size() > 0);
    }

    @Test
    public void testHandleAutocompleteWithReverseShouldThrows() {
        Response response = client.target(
                String.format("http://localhost:%d/gisgraphy?q=pari&point=52.5487429714954,-1.81602098644987&autocomplete=true&reverse=true", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(400);
    }
}
