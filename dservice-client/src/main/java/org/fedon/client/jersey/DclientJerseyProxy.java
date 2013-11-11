package org.fedon.client.jersey;

import java.util.Collections;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fedon.client.connector.AgoraConnector;
import org.fedon.client.connector.AgoraDSConnector;
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
        Configuration configuration = new ClientConfig();
        configuration.getProperties().put(AgoraConnector.appNameProp, Hello.class.getPackage().getName());
        configuration.getProperties().put(AgoraConnector.vipAddressProp, Hello.class.getPackage().getName());
        // prefix is to be set only for alternative implementation from external config
        String prefix = null; // for now default implementation is used
        configuration.getProperties().put(AgoraConnector.prefixProp, prefix);
        ClientConfig cc = new ClientConfig().connector(new AgoraDSConnector(configuration));
        Client resource = ClientBuilder.newClient(cc);
        Hello hello = WebResourceFactory.newResource(
                Hello.class, 
                resource.target(AgoraConnector.dynamicURIPartTemplate), 
                prefix != null, // ignoreResourcePath should be false for alternative invocation
                new MultivaluedHashMap<String, Object>(), 
                Collections.<Cookie>emptyList(),  
                new Form());

        log.info("Got a message: " + hello.getIt());
    }
}
