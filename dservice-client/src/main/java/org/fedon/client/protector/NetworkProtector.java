package org.fedon.client.protector;

import java.util.concurrent.Future;

import org.fedon.client.connector.AgoraConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.SettableFuture;

/**
 * Demo implementation.<br>
 * Signaling class to control AgoraConnector Hystrix behavior.
 * 
 * @author Dmytro Fedonin
 * 
 */
public class NetworkProtector {
    private Logger log = LoggerFactory.getLogger(getClass());
    @SuppressWarnings("rawtypes")
    public static final ThreadLocal<Future> futureHoler = new ThreadLocal<>(); // for demonstration only

    public NetworkProtector sync() {
        // trivial case
        AgoraConnector.doAsyncProtection.set(false);
        return this;
    }

    public <T> NetworkProtector async(Class<T> clazz) {
        final SettableFuture<T> setFuture = SettableFuture.create();
        futureHoler.set(setFuture);
        AgoraConnector.doAsyncProtection.set(true);
        AgoraAsyncCallback<T> callback = new AgoraAsyncCallback<T>(setFuture, clazz);
        AgoraConnector.callBackHolder.set(callback);
        return this;
    }

    public <T> Future<T> protectAsync(T var) {
        // TODO possibly get return type in runtime / get it from aspect or body reader?
        log.debug("___ intermediate result is: {}", var);
        @SuppressWarnings("unchecked")
        Future<T> result = futureHoler.get();
        // memory cleaning, remove current thread ref
        futureHoler.remove();
        AgoraConnector.callBackHolder.remove();
        AgoraConnector.doAsyncProtection.remove();
        return result;
    }

    public <T> T protectSync(T var) {
        // trivial case
        AgoraConnector.doAsyncProtection.remove();
        return var;
    }
}
