/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nslookup.handler;

import static org.openhab.binding.nslookup.NsLookupBindingConstants.*;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link NsLookupHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Wim Vissers - Initial contribution
 */
public class NsLookupHandler extends BaseThingHandler {

    public static final int REFRESH_INTERVAL_SECONDS = 60;
    public static final int MAX_RETRIES = 8;

    private final Logger logger = LoggerFactory.getLogger(NsLookupHandler.class);

    // Helper fields and constants
    private boolean online;

    // The hostname to look for.
    private String host;

    // The IP address found.
    private StringType ip;

    // Refresh rate in seconds.
    private int refresh;

    // List with channels to update fast (every minute)
    private final List<ChannelUID> refreshFast;

    // List with channels to update slow.
    private final List<ChannelUID> refreshSlow;

    // Scheduler to retrieve data from time to time.
    ScheduledFuture<?> refreshJob;

    public NsLookupHandler(Thing thing) {
        super(thing);
        refreshFast = new ArrayList<>();
        refreshFast.add(new ChannelUID(getThing().getUID(), CHANNEL_EXISTS));
        refreshFast.add(new ChannelUID(getThing().getUID(), CHANNEL_IP));
        refreshSlow = new ArrayList<>();
        online = false;
        ip = new StringType("0.0.0.0");
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            refreshState();
            switch (channelUID.getId()) {
                case CHANNEL_EXISTS:
                    updateState(channelUID, online ? OnOffType.ON : OnOffType.OFF);
                    break;
                case CHANNEL_IP:
                    updateState(channelUID, ip); // getIpAddress());
                    break;
                default:
                    logger.warn("Unexpected channel {}", channelUID);
            }
        } else {
            logger.warn("Unexpected command type {}", command.getClass().getName());
        }
    }

    private void refreshState() {
        try {
            InetAddress addr = InetAddress.getByName(host);
            if (!ip.toFullString().equals(addr.getHostAddress())) {
                ip = new StringType(addr.getHostAddress());
                online = true;
            }
            logger.info("Host {} found with address {}.", host, addr.getHostAddress());
        } catch (UnknownHostException e) {
            ip = new StringType("0.0.0.0");
            logger.info("Host {} not found.", host);
        }
    }

    @Override
    public void initialize() {
        // Create a Facade to the API
        try {
            Configuration config = getConfig();
            host = (String) config.get("host");
            refresh = ((BigDecimal) config.get("refresh")).intValue();
            logger.info("Looking for DNS entry for {}", host);
            updateStatus(ThingStatus.ONLINE);
        } catch (Exception e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "Error creating DNS lookup.");
        }
        if (refreshJob == null) {
            startAutomaticRefresh();
        }
    }

    /**
     * Check every 60 seconds if refresh commands are needed.
     */
    protected void startAutomaticRefresh() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (online) {
                        for (ChannelUID channel : refreshFast) {
                            handleCommand(channel, RefreshType.REFRESH);
                        }
                        for (ChannelUID channel : refreshSlow) {
                            handleCommand(channel, RefreshType.REFRESH);
                        }
                    } else {
                        // Try to (re)initialize
                        initialize();
                    }
                } catch (Exception e) {
                    logger.info("Exception occurred during execution: {}", e.getMessage());
                    online = false;
                }
            }
        };
        refreshJob = scheduler.scheduleAtFixedRate(runnable, 0, refresh, TimeUnit.SECONDS);
    }

    /**
     * Stop the automatic refresh.
     */
    private void stopAutomaticRefresh() {
        if (refreshJob != null) {
            refreshJob.cancel(true);
            refreshJob = null;
        }
    }

    /**
     * Dispose off the refreshJob nicely.
     */
    @Override
    public void dispose() {
        stopAutomaticRefresh();
    }

}
