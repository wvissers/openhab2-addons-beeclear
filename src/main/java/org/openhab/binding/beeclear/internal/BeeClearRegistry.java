/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.beeclear.internal;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This registry contains all discovered BeeClear devices (typically only one). It is
 * used for checking duplicates when discovering devices. Please not that auto discovery
 * will not work when more than one BeeClear devices are present in the same network.
 *
 * @author Wim Vissers - Initial contribution
 */
public class BeeClearRegistry {

    // The singleton instance
    private static final BeeClearRegistry INSTANCE = new BeeClearRegistry();

    // Devices are registered by IPv4 ip-address + ":" + port.
    private Set<String> _devices;
    private Logger _logger = LoggerFactory.getLogger(BeeClearRegistry.class);

    private BeeClearRegistry() {
        _devices = new HashSet<>();
    }

    /**
     * Get the singleton instance.
     *
     * @return
     */
    public static final BeeClearRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Register a host by name and port, and return an identifier.
     *
     * @param hostName the name or ip-address of the host.
     * @param port the port.
     * @return an identifier, ip-address:port.
     */
    public String registerByName(String hostName, int port) {
        String id = getId(hostName, port);
        if (id != null) {
            _devices.add(id);
        }
        return id;
    }

    /**
     * Check if a device is already registered.
     *
     * @param hostName
     * @param port
     * @return
     */
    public boolean isRegistered(String hostName, int port) {
        String id = getId(hostName, port);
        return id == null ? false : _devices.contains(id);
    }

    /**
     * Return the current size.
     *
     * @return
     */
    public int size() {
        return _devices.size();
    }

    /**
     * Remove the id.
     *
     * @param id
     */
    public void remove(String id) {
        if (id != null) {
            _devices.remove(id);
        }
    }

    /**
     * Construct unique id for a host.
     *
     * @param hostName
     * @param port
     * @return
     */
    private String getId(String hostName, int port) {
        try {
            InetAddress address = InetAddress.getByName(hostName);
            return address.getHostAddress() + ":" + port;
        } catch (UnknownHostException e) {
            _logger.error("Host {} not found.", hostName);
            return null;
        }
    }

}
