package org.fedon.client.protector;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.fedon.client.connector.AgoraConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dmytro Fedonin
 *
 */
@Provider
@Consumes(AgoraConnector.resolverType)
public class AsyncTypeProvider<T> implements MessageBodyReader<T> {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (AgoraConnector.resolverType.equals(mediaType.toString()))
            return true;
        return false;
    }

    @Override
    public T readFrom(Class<T> clazz, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream) throws IOException, WebApplicationException {
        @SuppressWarnings("unchecked")
        AgoraAsyncCallback<T> callback = AgoraConnector.callBackHolder.get();
        if (callback == null) {
            // FIXME throw illegal state ex?
            log.warn("Callback is null. Skeep processing.");
            return null;
        }
        if (callback.getType() != null) {
            // FIXME throw illegal state ex?
            log.warn("Callback has type already. Skeep processing.");
            return null;
        }
        byte[] expected = AgoraConnector.resolverMagic.getBytes();
        byte[] bytesEntity = new byte[expected.length];
        int r = entityStream.read(bytesEntity);
        if (r != expected.length) {
            log.warn("Failed to read magic bytes from response. Available lengs is {} expected is {}", r, expected.length);
        }
        log.debug("Type assigned to callback is {}", clazz.getName());
        callback.setType(clazz);

        return null;
    }
}
