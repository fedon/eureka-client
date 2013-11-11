package org.fedon.discoverable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fedon.discoverable.resource.CauResource;
import org.fedon.discoverable.resource.HelloResource;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryManager;

/**
 * Main class.
 *
 */
public class Main {
    private final static Log log = LogFactory.getLog(Main.class);
    // Base URI the Grizzly HTTP server will listen on
    public static final String baseUri = "http://localhost:";
    public static final String appName = Hello.class.getPackage().getName(); // in the real app this approach will require to limit one
                                                                                           // service to one package in the API project

    private static final DynamicPropertyFactory configInstance = com.netflix.config.DynamicPropertyFactory.getInstance();

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        log.info("app name: " + appName);
        // create a resource config that scans for JAX-RS resources and providers
        // in org.fedon.discoverable package
        final ResourceConfig rc = new ResourceConfig().register(HelloResource.class).register(CauResource.class);

        // support for JSON on the service provided by jersey-media-moxy package

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer( // eureka configuration property names should go to global Constants
                URI.create(baseUri + configInstance.getStringProperty("eureka.port", "18080").get()),
                rc);
        try {
            registerWithEureka(appName, appName);
        } catch (SecurityException e) {
            log.warn("app registration failed", e);
        } catch (IllegalArgumentException e) {
            log.warn("app registration failed", e);
        } catch (NoSuchFieldException e) {
            log.warn("app registration failed", e);
        } catch (IllegalAccessException e) {
            log.warn("app registration failed", e);
        } catch (Exception e) {
            log.warn("app registration failed", e);
        }
        // HttpHandler handler = server.getServerConfiguration().getHttpHandlers().keySet().iterator().next(); // may be used to reconstruct base URI
        // Set<Resource> resources = rc.getResources();
        // for (Resource resource : resources) {
        // String path = resource.getPath();
        // String perResourceUri = baseUri + path;
        // log.info("base uri: " + perResourceUri);
        // String vipAddress = appName + path;
        // log.info("vip address: " + vipAddress);
        // EurekaClientJersey.registerInstance(appName, perResourceUri, vipAddress, false);
        // }

        return server;
    }

    /**
     * Main method.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        log.info(String.format("Jersey app started with WADL available at " + "%sapplication.wadl\nHit enter to stop it...", baseUri
                + configInstance.getStringProperty("eureka.port", "18080").get()));
        System.in.read();
        server.stop();
    }

    static void registerWithEureka(String appName, String vipAddress) throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        // Register with Eureka
        EurekaInstanceConfig instanceConfig = new MyDataCenterInstanceConfig();
        // instanceConfig.
        DefaultEurekaClientConfig euConf = new DefaultEurekaClientConfig();
        DiscoveryManager.getInstance().initComponent(instanceConfig, euConf);

        // TODO setup runtime values
        String appNameFieldName = "appName";
        String vipAddressFieldName = "vipAddress";
        InstanceInfo info = ApplicationInfoManager.getInstance().getInfo();
        Field fAppName = InstanceInfo.class.getDeclaredField(appNameFieldName);
        fAppName.setAccessible(true);
        fAppName.set(info, appName);
        Field fVipAddress = InstanceInfo.class.getDeclaredField(vipAddressFieldName);
        fVipAddress.setAccessible(true);
        fVipAddress.set(info, vipAddress);

        ApplicationInfoManager.getInstance().setInstanceStatus(InstanceStatus.UP);
        // InstanceInfo nextServerInfo = null;
        // while (nextServerInfo == null) {
        // try {
        // nextServerInfo = DiscoveryManager.getInstance().getDiscoveryClient().getNextServerFromEureka(vipAddress, false);
        // } catch (Throwable e) {
        // log.info("Waiting for service to register with eureka..");
        // }
        // }
    }
}
