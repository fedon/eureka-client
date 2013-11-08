package org.fedon.client.jersey;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fedon.discoverable.Hello;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

/**
 * @author Dmytro Fedonin
 * 
 */
public class DclientJerseyProxy {
    private final static Log log = LogFactory.getLog(DclientJerseyProxy.class);
    static String eurekaBase = "http://localhost:8080/eureka/v2";

    public static void main(String[] args) throws Exception {
        ClientConfig cc = new ClientConfig();
        Client resource = ClientBuilder.newClient(cc);
        Hello hello = WebResourceFactory.newResource(Hello.class, resource.target("place-holder"));

        log.info("Got a message: " + hello.getIt());
    }
}
