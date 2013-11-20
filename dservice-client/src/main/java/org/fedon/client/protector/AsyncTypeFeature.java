package org.fedon.client.protector;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.MessageBodyReader;

/**
 * AsyncType provider registration feature.
 * 
 * @author Dmytro Fedonin
 * 
 */
public class AsyncTypeFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        context.register(AsyncTypeProvider.class, MessageBodyReader.class);
        return true;
    }
}
