/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.beeclear.internal;

import java.io.IOException;

import org.eclipse.smarthome.core.library.types.StringType;
import org.openhab.binding.beeclear.internal.data.ActiveValues;
import org.openhab.binding.beeclear.internal.data.ActiveValuesImplRev1;
import org.openhab.binding.beeclear.internal.data.SoftwareVersion;
import org.openhab.binding.beeclear.internal.data.SoftwareVersionImpl;
import org.openhab.binding.beeclear.internal.data.Status;
import org.openhab.binding.beeclear.internal.data.StatusImplRev1;

/**
 * The Facade that is used for a firmware version independent way
 * to communicate with the openHAB system.
 *
 * @author Wim Vissers - Initial contribution
 */
public class DataCollectorFacade {

    private final RestClient restClient;

    private SoftwareVersion softwareVersion;
    private boolean connected;

    private long versionRefreshed;
    private static final long VERSION_INIT_DELAY = 1000 * 30;
    private static final long VERSION_REFRESH_INTERVAL = 1000 * 60 * 15;

    public DataCollectorFacade(String host, int port) throws IOException {
        restClient = new RestClient(host, port);
        softwareVersion = restClient.getSoftwareVersion();
        versionRefreshed = System.currentTimeMillis() - VERSION_REFRESH_INTERVAL + VERSION_INIT_DELAY;
    }

    /**
     * Get the IP address, if at least one successful connection was made.
     *
     * @return the IP address as StringType, ready to publish.
     */
    public StringType getIpAddress() {
        return restClient.getIpAddress();
    }

    /**
     * Retrieve the software version info from the BeeClear.
     *
     * @return
     */
    public SoftwareVersion getSoftwareVersion() {
        if (softwareVersion == null || isVersionDataExpired()) {
            try {
                softwareVersion = restClient.getSoftwareVersion();
                versionRefreshed = System.currentTimeMillis();
                connected = true;
            } catch (IOException e) {
                if (softwareVersion == null) {
                    softwareVersion = new SoftwareVersionImpl(null);
                }
                connected = false;
            }
        }
        return softwareVersion;
    }

    /**
     * Determine if a refresh of the version data is needed.
     *
     * @return
     */
    public boolean isVersionDataExpired() {
        return (System.currentTimeMillis() - versionRefreshed) > VERSION_REFRESH_INTERVAL;
    }

    /**
     * Determine is the version is supported.
     *
     * @return
     */
    public boolean isVersionSupported() throws IOException {
        return restClient.isSupported(getSoftwareVersion());
    }

    /**
     * Retrieve the current active values.
     *
     * @return
     */
    public ActiveValues getActiveValues() {
        try {
            connected = true;
            return restClient.getActiveValues(softwareVersion);
        } catch (IOException e) {
            connected = false;
            return new ActiveValuesImplRev1(null);
        }
    }

    /**
     * Retrieve the current status.
     *
     * @return
     */
    public Status getStatus() {
        try {
            connected = true;
            return restClient.getStatus(softwareVersion);
        } catch (IOException e) {
            connected = false;
            return new StatusImplRev1(null);
        }
    }

    /**
     * Return true if the binding is connected to the BeeClear device.
     *
     * @return
     */
    public boolean isConnected() {
        return connected;
    }

}
