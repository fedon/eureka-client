package org.fedon.client.jersey;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.ext.MessageBodyReader;

import org.fedon.client.connector.AgoraConnector;
import org.fedon.client.connector.AgoraDSConnector;
import org.fedon.client.protector.AsyncTypeProvider;
import org.fedon.client.protector.NetworkProtector;
import org.fedon.discoverable.Hello;
import org.fedon.matrix.model.Matrix;
import org.fedon.matrix.rest.MatrixIf;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

/**
 * Demo client to be use with Matrix Jersey server.
 * 
 * @author Dmytro Fedonin
 * 
 */
public class DclientJerseyProxy {
    static private Logger log = LoggerFactory.getLogger(DclientJerseyProxy.class);

    // static String eurekaBase = "http://localhost:8080/eureka/v2";
    static Hello hello;
    static MatrixIf matrix;

    // @Inject
    NetworkProtector protector;

    /**
     * @param args
     *            if the first argument is not empty it is appended to base dependency uri. For instance /service/name.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Logger debug = LoggerFactory.getLogger(AsyncTypeProvider.class);
        if (debug instanceof ch.qos.logback.classic.Logger) {
            ch.qos.logback.classic.Logger mod = (ch.qos.logback.classic.Logger) debug;
            mod.setLevel(Level.DEBUG);
        } else {
            log.info("logback binding failed");
        }

        ResourceConfig configuration = new ResourceConfig();
        configuration.property(AgoraConnector.appNameProp, Hello.class.getPackage().getName());
        configuration.property(AgoraConnector.vipAddressProp, Hello.class.getPackage().getName());
        // prefix is to be set only for alternative implementation from external config
        String prefix = null; // for now default implementation is used
        configuration.property(AgoraConnector.prefixProp, prefix);
        // configure Jersey client
        ClientConfig cc = new ClientConfig().register(JacksonFeature.class).register(AsyncTypeProvider.class, MessageBodyReader.class)
                .connector(new AgoraDSConnector(configuration));
        Client resource = ClientBuilder.newClient(cc);
        // create client proxy
        matrix = WebResourceFactory.newResource(MatrixIf.class,
                resource.target(AgoraConnector.dynamicURIPartTemplate + (args.length > 0 ? args[0] : "")),
                prefix != null, // ignoreResourcePath should be false for alternative invocation
                new MultivaluedHashMap<String, Object>(), 
                Collections.<Cookie>emptyList(),  
                new Form());

        // not protected use
        log.info("Got a message: " + matrix.supportedOps());
        new DclientJerseyProxy().tryIt();
    }

    void tryIt() throws InterruptedException, ExecutionException {
        protector = new NetworkProtector();
        // Async call protected example with explicit return type. String is default return type in Jersey so it will work without type resolving.
        Future<String> futureStr = protector.async(String.class).protectAsync(matrix.supportedOps());
        log.info("Got a future: " + futureStr);
        log.info(" future string is: " + futureStr.get());
        // Async call protected example with auto-detected Matrix type. AsyncTypeProvider will set callback type.
        Future<Matrix> futureMatrix = protector.async().protectAsync(matrix.single());
        log.info("Got a future: " + futureMatrix);
        do {
            if (futureMatrix.isCancelled()) {
                log.info("canceled ------------------");
                return;
            }
            log.info("waiting for matrix... " + futureMatrix.isDone());
            Thread.sleep(2000);
        } while (!futureMatrix.isDone());
        Matrix result = futureMatrix.get();
        log.info("async MATRIX is HERE ==> " + result);

        // Sync call protected example Matrix
        result = protector.sync().protectSync(matrix.trans(result));
        log.info("sync trans MATRIX is HERE ==> " + result);
    }
}
