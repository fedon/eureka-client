package org.fedon.client.jersey;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.eureka.resources.ApplicationResource;

/**
 * Eureka runtime registration utility. Designed to register each resource as a separate instance of application/service. This class supposed to be
 * instantiated as configured singleton in real application.
 * 
 * @author Dmytro Fedonin
 * 
 */
public class EurekaClientJersey {
    private final static Log log = LogFactory.getLog(EurekaClientJersey.class);
    static String eurekaBase = "http://localhost:8080/eureka/v2";

    /**
     * Performs remote call to register new instance.
     * 
     * @param appName
     *            common name for joined resources.
     * @param explicitBaseUrl
     *            per resource base url. This url will be stored as home URL for the registered instance.
     * @param vipAddress
     *            unique ID of any resource. This value to be used to retrieve available instances.
     * @param isReplica
     */
    public static void registerInstance(String appName, String explicitBaseUrl, String vipAddress, boolean isReplica) {
        ClientConfig cc = new ClientConfig();
        Client resource = ClientBuilder.newClient(cc);
        ApplicationResource eResource = WebResourceFactory.newResource(ApplicationResource.class, resource.target(eurekaBase));
        InstanceInfo info = InstanceInfo.Builder.newBuilder().setAppName(appName).setHomePageUrl(null, explicitBaseUrl).setVIPAddress(vipAddress)
                .build();
        eResource.addInstance(info, Boolean.toString(isReplica));
        log.info("Registration done.");
    }

    void setInstance() {
        // String appNameFieldName = "appName";
        // String vipAddressFieldName = "vipAddress";
        // String homePageUrlFieldName = "homePageUrl";

        // InstanceInfo info = ApplicationInfoManager.getInstance().getInfo();
        // Field fAppName = InstanceInfo.class.getDeclaredField(appNameFieldName);
        // fAppName.setAccessible(true);
        // fAppName.set(info, appName);
        // Field fVipAddress = InstanceInfo.class.getDeclaredField(vipAddressFieldName);
        // fVipAddress.setAccessible(true);
        // fVipAddress.set(info, vipAddress);
        // Field fHomePageUrl = InstanceInfo.class.getDeclaredField(homePageUrlFieldName);
        // fHomePageUrl.setAccessible(true);
        // fHomePageUrl.set(info, homePageUrlFieldName);
    }
}
