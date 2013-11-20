package org.fedon.client.filter;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dmytro Fedonin
 *
 */
public class AuthOutFilter implements ClientRequestFilter {
    Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        log.info("filtering... " + requestContext.getUri());
    }
}
