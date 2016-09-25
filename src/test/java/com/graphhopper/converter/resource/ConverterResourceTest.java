package com.graphhopper.converter.resource;

import com.graphhopper.converter.ConverterApplication;
import com.graphhopper.converter.ConverterConfiguration;
import com.graphhopper.converter.api.GHEntry;
import com.graphhopper.converter.api.GHResponse;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Robin Boldt
 */
public class ConverterResourceTest {
    @ClassRule
    public static final DropwizardAppRule<ConverterConfiguration> RULE =
            new DropwizardAppRule<>(ConverterApplication.class, ResourceHelpers.resourceFilePath("converter.yml"));

    @Test
    public void testHandleForward() {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("test forward client");

        client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
        client.property(ClientProperties.READ_TIMEOUT, 100000);

        Response response = client.target(
                String.format("http://localhost:%d/nominatim?q=berlin", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getLocale().equals("en"));
    }

    @Test
    public void testHandleReverse() {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("test reverse client");

        client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
        client.property(ClientProperties.READ_TIMEOUT, 100000);

        Response response = client.target(
                String.format("http://localhost:%d/nominatim?point=52.5487429714954,-1.81602098644987&reverse=true", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    public void testCorrectLocale() {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("testCorrectLocale");

        client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
        client.property(ClientProperties.READ_TIMEOUT, 100000);

        Response response = client.target(
                String.format("http://localhost:%d/nominatim?q=berlin&locale=de", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getLocale().equals("de"));
    }

    @Test
    public void testIncorrectLocale() {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("testIncorrectLocale");

        client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
        client.property(ClientProperties.READ_TIMEOUT, 100000);

        Response response = client.target(
                String.format("http://localhost:%d/nominatim?q=berlin&locale=IAmNotValid", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getLocale().equals("en"));
    }
}
