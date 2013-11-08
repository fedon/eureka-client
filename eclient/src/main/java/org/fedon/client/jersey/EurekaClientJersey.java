package org.fedon.client.jersey;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.client.ClientConfig;

import com.netflix.discovery.shared.EurekaJerseyClient;
import com.netflix.discovery.shared.EurekaJerseyClient.JerseyClient;

/**
 * @author Dmytro Fedonin
 * 
 */
public class EurekaClientJersey {
    private final static Log log = LogFactory.getLog(EurekaClientJersey.class);
    static String eurekaBase = "http://localhost:8080/eureka/v2";

    public static void main(String[] args) throws Exception {
        ClientConfig cc = new ClientConfig();
        Client resource = ClientBuilder.newClient(cc);
        JerseyClient euClient = EurekaJerseyClient.createJerseyClient(1, 1, 1, 1, 1);

// log.info("Got a message: " + hello.getIt());
    }
}
