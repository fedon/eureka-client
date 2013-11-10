package org.fedon.discoverable.resource;

import org.fedon.discoverable.Hello;

/**
 * @author Dmytro Fedonin
 *
 */
public class HelloResource implements Hello {
    @Override
    public String getIt() {
        return "Eureka!!!";
    }
}
