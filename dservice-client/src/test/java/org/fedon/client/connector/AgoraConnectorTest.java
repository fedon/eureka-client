package org.fedon.client.connector;

import static org.mockito.Mockito.verify;

import java.net.URI;
import java.net.URISyntaxException;

import org.fedon.discoverable.Hello;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Dmytro Fedonin
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AgoraConnectorTest {
    @Mock
    Hello hello;
    @InjectMocks
    AgoraConnector connector = new AgoraConnector(new ClientConfig());

    @Ignore
    @Test
    public void testEurekaUri() throws URISyntaxException {
        // TODO implement
        connector.prefix = "pref";
        URI result = connector.eurekaUri(new URI("lala"));
    }

    @Test
    public void testHystrixProtection() {
        verify(hello).getIt();
    }
}
