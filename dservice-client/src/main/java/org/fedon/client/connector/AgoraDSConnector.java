package org.fedon.client.connector;

import java.util.concurrent.Future;

import javax.ws.rs.core.Configuration;

import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.spi.AsyncConnectorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Perhaps it should be in core project. But for refactoring/demo purpose I leave it here close to API.
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

        return _apply(request);
    }

    @Override
    public Future<?> apply(final ClientRequest request, final AsyncConnectorCallback callback) {
        request.setUri(eurekaUri(request.getUri()));
        logger.debug("Invoke async Histrix command -- " + request.getUri());

        return _apply(request, callback);
    }
}
