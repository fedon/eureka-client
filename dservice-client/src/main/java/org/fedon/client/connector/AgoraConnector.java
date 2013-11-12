package org.fedon.client.connector;

import java.net.URI;
import java.util.concurrent.Future;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.HttpUrlConnector;
import org.glassfish.jersey.client.spi.AsyncConnectorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryManager;

/**
 * Perhaps it should be in core project. But for refactoring/demo purpose I leave it here close to API.
 * 
 * @author Dmytro Fedonin
 * 
 */
public class AgoraConnector extends HttpUrlConnector {
    private static final String PROTO = "http://";
    private Logger log = LoggerFactory.getLogger(getClass());
    public static final String dynamicURIPartTemplate = "eureka";
    public static final String prefixProp = "resource.prefix";
    public static final String vipAddressProp = "eureka.instance.vip";
    public static final String appNameProp = "eureka.app.name";

    // TODO find proper solution for alternative resource implementation
    String vipAddress;
    String prefix;
    protected String appName;

    public AgoraConnector(Configuration configuration) {
        super(configuration);
        prefix = (String) configuration.getProperty(prefixProp);
        vipAddress = (String) configuration.getProperty(vipAddressProp);
        appName = (String) configuration.getProperty(appNameProp);
    }

    /**
     * @param connectionFactory
     */
    public AgoraConnector(Configuration configuration, ConnectionFactory connectionFactory) {
        super(configuration, connectionFactory);
    }

    protected ClientResponse _apply(ClientRequest request) {
        return super.apply(request);
    }

    protected Future<?> _apply(ClientRequest request, AsyncConnectorCallback callback) {
        return super.apply(request, callback);
    }

    protected URI eurekaUri(URI uri) {
        String str = uri.toString();
        // TODO remove static call
        InstanceInfo info = DiscoveryManager.getInstance().getDiscoveryClient().getNextServerFromEureka(vipAddress, false);
        String replacement = str.replaceFirst(dynamicURIPartTemplate, buildHostPort(info.getHostName(), info.getPort()));
        return UriBuilder.fromUri(replacement).build();
    }

    String buildHostPort(String host, int port) {
        String result = PROTO + host + ":" + port;
        if (prefix != null) {
            result += "/" + prefix;
        }
        log.debug(" -- " + result);
        return result;
    }
}
