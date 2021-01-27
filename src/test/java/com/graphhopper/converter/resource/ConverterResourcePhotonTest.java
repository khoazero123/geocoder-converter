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

/**
 * @author Robin Boldt
 */
public class ConverterResourcePhotonTest {
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
                String.format("http://localhost:%d/photon?q=berlin", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getLocale().equals("en"));

        // This might change in OSM and we might need to update this test then
        List<Double> extent = entry.getHits().get(1).getExtent().getExtent();
        assertEquals(extent.get(0), 13.1, .1);
        assertEquals(extent.get(1), 52.3, .1);
        assertEquals(extent.get(2), 13.7, .1);
        assertEquals(extent.get(3), 52.6, .1);
    }

    @Test
    public void testLocationBiasScale() {
        // First test a low bias
        Response response = client.target(
                String.format("http://localhost:%d/photon?q=beer&point=48.774675,9.172136&location_bias_scale=0.1", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertEquals("Beer Quarry Caves", entry.getHits().get(0).getName());

        // Now test a high bias
        response = client.target(
                String.format("http://localhost:%d/photon?q=beer&point=48.774675,9.172136&location_bias_scale=10", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        entry = response.readEntity(GHResponse.class);
        assertEquals("Georg-Beer-Straße", entry.getHits().get(0).getName());
    }

    @Test
    public void testBBox() {
        Response response = client.target(
                String.format("http://localhost:%d/photon?q=berlin&bbox=9.5,51.5,11.5,53.5&locale=de", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertEquals("Niedersachsen", entry.getHits().get(0).getState());
    }

    @Test
    public void testHandleReverse() {
        Response response = client.target(
                String.format("http://localhost:%d/photon?point=48.774675,9.172136&reverse=true", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);

        GHResponse entry = response.readEntity(GHResponse.class);
        assertEquals("Rotebühlplatz", entry.getHits().get(0).getName());
        assertEquals("Baden-Württemberg", entry.getHits().get(0).getState());
    }

    @Test
    public void osmTags() {
        Response response = client.target(
                String.format("http://localhost:%d/photon?q=berlin&osm_tag=place:city", RULE.getLocalPort()))
                .request()
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertEquals("Berlin", entry.getHits().get(0).getName());
        assertEquals("city", entry.getHits().get(0).getOsmValue());

        response = client.target(
                String.format("http://localhost:%d/photon?q=berlin&osm_tag=!place:city", RULE.getLocalPort()))
                .request()
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        entry = response.readEntity(GHResponse.class);
        assertEquals("Berlin", entry.getHits().get(0).getName());
        assertEquals("state", entry.getHits().get(0).getOsmValue());
    }

    @Test
    public void testCorrectLocale() {
        Response response = client.target(
                String.format("http://localhost:%d/photon?q=berlin&locale=de", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getLocale().equals("de"));
    }

}
