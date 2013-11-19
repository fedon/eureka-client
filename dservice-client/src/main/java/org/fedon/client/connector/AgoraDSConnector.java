package org.fedon.client.connector;

import java.util.concurrent.Future;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.spi.AsyncConnectorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Perhaps it should be in core project. But for refactoring/demo purpose I leave it here close to API.<br>
 * Eureka aware connector.<br>
 * Hystrix aware connector with dynamic command creation.
 * 
 * @author Dmytro Fedonin
 * 
 */
public class AgoraDSConnector extends AgoraConnector {
    Logger logger = LoggerFactory.getLogger(getClass());

    public AgoraDSConnector(Configuration configuration) {
        super(configuration);
    }

    /**
     * @param connectionFactory
     */
    public AgoraDSConnector(Configuration configuration, ConnectionFactory connectionFactory) {
        super(configuration, connectionFactory);
    }

    @Override
    public ClientResponse apply(final ClientRequest request) {
        request.setUri(eurekaUri(request.getUri()));
        logger.debug("Invoke Histrix command -- " + request.getUri().getPath());
        logger.debug("Application name: " + appName);
        if (doAsyncProtection.get() == null) { // no protection
            logger.info("++++ no protection");
            return super.apply(request);
        }
        logger.info("++++ network protection ++++");
        final AsyncConnectorCallback callback = callBackHolder.get();
        // TODO hystrix command constructed here
        if (doAsyncProtection.get()) { // async ==> cmd.queue();
            logger.info("++++ ASYNC ++++");
            new Thread() {

                @Override
                public void run() {
                    callback.response(_apply(request));

                }
            }.start(); // demo only
            // next line could be used for runtime writer based return type discovery
            // request.abortWith(Response.status(Response.Status.OK).entity("type magic").build());
            return new ClientResponse(Response.Status.NO_CONTENT, request);
        }
        logger.info(".... sync ....");
        return _apply(request); // sync ==> cmd.execute();
    }

    @Override
    public Future<?> apply(final ClientRequest request, final AsyncConnectorCallback callback) {
        request.setUri(eurekaUri(request.getUri()));
        logger.debug("Invoke async Histrix command -- " + request.getUri());
        logger.debug("Application name: " + appName);

        return _apply(request, callback);
    }
}
