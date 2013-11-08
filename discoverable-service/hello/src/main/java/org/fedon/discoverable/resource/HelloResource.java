package org.fedon.discoverable.resource;

import javax.ws.rs.Path;

import org.fedon.discoverable.Hello;

/**
 * @author Dmytro Fedonin
 *
 */
@Path("/hello")
public class HelloResource implements Hello {
    @Override
    public String getIt() {
        return "Eureka!!!";
    }
}
