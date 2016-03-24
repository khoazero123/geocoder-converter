package com.graphhopper.converter;

import com.graphhopper.converter.api.IPFilter;
import com.graphhopper.converter.health.NominatimHealthCheck;
import com.graphhopper.converter.resources.ConverterResource;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.EnumSet;
import javax.servlet.DispatcherType;

import javax.ws.rs.client.Client;

/**
 * @author Robin Boldt
 */
public class ConverterApplication extends Application<ConverterConfiguration>
{

    public static void main( String[] args ) throws Exception
    {
        new ConverterApplication().run(args);
    }


    @Override
    public String getName()
    {
        return "graphhopper-nominatim-converter";
    }

    @Override
    public void initialize( Bootstrap<ConverterConfiguration> bootstrap )
    {
        // nothing to do yet
    }

    @Override
    public void run( ConverterConfiguration config, Environment environment ) throws Exception
    {

        final Client client = new JerseyClientBuilder(environment).using(config.getJerseyClientConfiguration())
                .build(getName());

        final ConverterResource resource = new ConverterResource(
                config.getNominatimUrl(),
                client
        );

        final NominatimHealthCheck healthCheck =
                new NominatimHealthCheck(config.getNominatimUrl(), client);
        environment.healthChecks().register("template", healthCheck);

        environment.jersey().register(resource);
        
        environment.servlets().addFilter("ip-filter", new IPFilter(config.getIPWhiteList(), config.getIPBlackList())).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }
}
