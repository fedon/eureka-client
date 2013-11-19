package org.fedon.client.protector;

import java.util.Collection;
import java.util.Collections;

import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.spi.AsyncConnectorCallback;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.SettableFuture;

/**
 * @author Dmytro Fedonin
 *
 */
public class AgoraAsyncCallback<T> implements AsyncConnectorCallback {
    private Logger log = LoggerFactory.getLogger(getClass());
    private SettableFuture<T> setFuture;
    private Class<T> type;

    public AgoraAsyncCallback(SettableFuture<T> future, Class<T> clazz) {
        setFuture = future;
        type = clazz;
    }

    @Override
    public void response(ClientResponse response) {
        try {
            setFuture.set(response.readEntity(type, new PropertiesDelegate() {

                @Override
                public void setProperty(String name, Object object) {
                }

                @Override
                public void removeProperty(String name) {
                }

                @Override
                public Collection<String> getPropertyNames() {
                    return Collections.emptySet();
                }

                @Override
                public Object getProperty(String name) {
                    return null;
                }
            }));
        } catch (Exception e) {
            log.error("Client fails to process async respolse", e);
            setFuture.setException(e);
        }
    }

    @Override
    public void failure(Throwable failure) {
        log.error("Async processing fail", failure);
        setFuture.setException(failure);
    }

    void setType(Class<T> type) {
        this.type = type;
    }
}
