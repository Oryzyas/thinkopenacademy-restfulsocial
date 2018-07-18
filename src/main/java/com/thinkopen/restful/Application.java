package com.thinkopen.restful;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@ApplicationPath("api")
public class Application extends ResourceConfig {

    public Application() {
        packages("com.thinkopen.restful.api");
    }

}
