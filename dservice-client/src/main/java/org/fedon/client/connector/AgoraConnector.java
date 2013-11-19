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

/**
 * Perhaps it should be in core project. But for refactoring/demo purpose I leave it here close to client.
 * 
 * @author Dmytro Fedonin
 * 
 */
public class AgoraConnector extends HttpUrlConnector {
    private Logger log = LoggerFactory.getLogger(getClass());
    public static final ThreadLocal<Boolean> doAsyncProtection = new ThreadLocal<>(); // for demonstration only
    public static final ThreadLocal<AsyncConnectorCallback> callBackHolder = new ThreadLocal<>(); // for demonstration only
    private static final String PROTO = "http://";
    public static final String dynamicURIPartTemplate = "eureka";
    public static final String prefixProp = "resource.prefix";
    public static final String vipAddressProp = "eureka.instance.vip";
    public static final String appNameProp = "eureka.app.name";

    // @Inject
    // Provider<RoutingContext> routingContextProvider;
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
        InstanceInfo info = null;// DiscoveryManager.getInstance().getDiscoveryClient().getNextServerFromEureka(vipAddress, false);
        String replacement = str.replaceFirst(dynamicURIPartTemplate, buildHostPort(info));
        return UriBuilder.fromUri(replacement).build();
    }

    String buildHostPort(InstanceInfo info) {
        if (info == null) {
            // return "replacement";
            return "http://localhost:8080";
        }
        String result = PROTO + info.getHostName() + ":" + info.getPort();
        if (prefix != null) {
            result += "/" + prefix;
        }
        log.debug(" -- " + result);
        return result;
    }
}
