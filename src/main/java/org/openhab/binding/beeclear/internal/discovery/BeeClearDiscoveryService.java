/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.beeclear.internal.discovery;

import static org.openhab.binding.beeclear.BeeClearBindingConstants.THING_TYPE_METER;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.beeclear.internal.BeeClearRegistry;
import org.openhab.binding.beeclear.internal.RestClient;
import org.openhab.binding.beeclear.internal.data.SoftwareVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The BeeClearDiscoveryService is responsible for auto detecting a BeeClear
 * device in the local network.
 *
 * @author Wim Vissers - Initial contribution
 */
public class BeeClearDiscoveryService extends AbstractDiscoveryService {

    private Logger logger = LoggerFactory.getLogger(BeeClearDiscoveryService.class);
    private RestClient restClient;
    private ScheduledFuture<?> discoveryJob;
    private int holdOff;

    private static final String BEECLEAR_HOSTNAME = "beeclear";
    private static final int BEECLEAR_PORT = 80;
    private static final int INTERVAL = 30;

    private static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS;

    public static Set<ThingTypeUID> getSupportedTypes() {
        if (SUPPORTED_THING_TYPES_UIDS == null) {
            SUPPORTED_THING_TYPES_UIDS = new HashSet<>();
            SUPPORTED_THING_TYPES_UIDS.add(THING_TYPE_METER);
        }
        return SUPPORTED_THING_TYPES_UIDS;
    }

    public BeeClearDiscoveryService() {
        super(getSupportedTypes(), 5, true);
        holdOff = 1;
    }

    public void activate() {
        logger.debug("Starting BeeClear discovery...");
        // removeOlderResults(System.currentTimeMillis(), getSupportedTypes());
        startScan();
        startBackgroundDiscovery();
    }

    @Override
    public void deactivate() {
        logger.debug("Stopping BeeClear discovery...");
        stopBackgroundDiscovery();
        stopScan();
    }

    private void discover() {
        if (holdOff == 0) {
            if (!BeeClearRegistry.getInstance().isRegistered(BEECLEAR_HOSTNAME, BEECLEAR_PORT)) {
                try {
                    SoftwareVersion softwareVersion = restClient.getSoftwareVersion();
                    if (softwareVersion.getInfo() != null && !softwareVersion.getInfo().isEmpty()) {
                        DiscoveryResult discoveryResult = DiscoveryResultBuilder
                                .create(new ThingUID("beeclear:meter:unit1")).withProperties(getConfigProperties())
                                .withLabel("BeeClear Device").build();
                        thingDiscovered(discoveryResult);
                        BeeClearRegistry.getInstance().registerByName(BEECLEAR_HOSTNAME, BEECLEAR_PORT);
                    }
                } catch (IOException e) {
                    logger.debug("Could not connect to BeeClear device.", e);
                }
            }
        }
        if (holdOff > 0) {
            holdOff--;
        }
    }

    private Map<String, Object> getConfigProperties() {
        Map<String, Object> result = new HashMap<>();
        result.put("host", BEECLEAR_HOSTNAME);
        result.put("port", new BigDecimal(BEECLEAR_PORT));
        return result;
    }

    @Override
    protected void startBackgroundDiscovery() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    discover();
                } catch (Exception e) {
                    logger.debug("Exception occurred during execution: {}", e.getMessage(), e);
                }
            }
        };
        logger.info("Start BeeClear device background discovery");
        if (discoveryJob == null || discoveryJob.isCancelled()) {
            restClient = new RestClient(BEECLEAR_HOSTNAME, BEECLEAR_PORT);
            discoveryJob = scheduler.scheduleAtFixedRate(runnable, 0, INTERVAL, TimeUnit.SECONDS);
        }
    }

    @Override
    protected void stopBackgroundDiscovery() {
        logger.info("Stop Beeclear background discovery");
        if (discoveryJob != null && !discoveryJob.isCancelled()) {
            discoveryJob.cancel(true);
            restClient = null;
            discoveryJob = null;
        }
    }

    @Override
    protected void startScan() {
        logger.debug("Starting device search...");
    }

    @Override
    protected synchronized void stopScan() {
        removeOlderResults(getTimestampOfLastScan());
        super.stopScan();
        if (!isBackgroundDiscoveryEnabled()) {
        }
    }

}
