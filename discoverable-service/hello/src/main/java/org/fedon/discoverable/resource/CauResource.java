package org.fedon.discoverable.resource;

import javax.ws.rs.Path;

import org.fedon.discoverable.Cau;

/**
 * @author Dmytro Fedonin
 *
 */
@Path("/cau")
public class CauResource implements Cau {
    @Override
    public String getForeval() {
        return "CU!";
    }
}
