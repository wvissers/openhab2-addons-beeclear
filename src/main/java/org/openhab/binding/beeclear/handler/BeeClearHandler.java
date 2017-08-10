/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.beeclear.handler;

import static org.openhab.binding.beeclear.BeeClearBindingConstants.*;

import java.math.BigDecimal;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.beeclear.internal.RestClient;
import org.openhab.binding.beeclear.internal.data.ActiveValues;
import org.openhab.binding.beeclear.internal.data.SoftwareVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BeeClearHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Wim Vissers - Initial contribution
 */
public class BeeClearHandler extends BaseThingHandler {

    private final Logger _logger = LoggerFactory.getLogger(BeeClearHandler.class);

    // The RestClient instance connects to the BeeClear device using http.
    private RestClient _restClient;

    // The BeeClear unit software version.
    private SoftwareVersion _softwareVersion;

    // The last retrieved actual data
    private ActiveValues _activeValues;

    // Helper fields and constants
    private int _versionRefreshCnt;
    private static final int VERSION_REFRESH_INTERVAL = 60;

    // Scheduler to retrieve data from time to time.
    ScheduledFuture<?> _refreshJob;

    public BeeClearHandler(Thing thing) {
        super(thing);
        _versionRefreshCnt = 2;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            switch (channelUID.getId()) {
                case CHANNEL_ENGINE:
                    updateState(channelUID, new StringType(_softwareVersion.getEngine()));
                    break;
                case CHANNEL_POWER:
                    updateState(channelUID, new DecimalType(_activeValues.getUsedPower()));
                    break;
                case CHANNEL_USED_HIGH:
                    updateState(channelUID, new DecimalType(_activeValues.getUsedElectricityHigh()));
                    break;
                case CHANNEL_USED_LOW:
                    updateState(channelUID, new DecimalType(_activeValues.getUsedElectricityLow()));
                    break;
                default:
                    _logger.warn("Unexpected channel {}", channelUID);
            }
        } else {
            _logger.warn("Unexpected command type {}", command.getClass().getName());
        }
    }

    @Override
    public void initialize() {
        Configuration config = getThing().getConfiguration();
        String ip = (String) config.get("host");
        BigDecimal port = ((BigDecimal) config.get("port"));

        // Create a client for the restfull API.
        _restClient = new RestClient(ip, port.intValue());

        // Retrieve the BeeClear software version.
        _softwareVersion = _restClient.getSoftwareVersion();

        // TODO: Initialize the thing. If done set status to ONLINE to indicate proper working.
        // Long running initialization should be done asynchronously in background.
        updateStatus(ThingStatus.ONLINE);

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work
        // as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");

        startAutomaticRefresh();

    }

    /**
     * Check every 60 seconds if refresh commands are needed.
     */
    protected void startAutomaticRefresh() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    _activeValues = _restClient.getActiveValues(_softwareVersion);
                    handleCommand(new ChannelUID(getThing().getUID(), CHANNEL_POWER), RefreshType.REFRESH);
                    handleCommand(new ChannelUID(getThing().getUID(), CHANNEL_USED_HIGH), RefreshType.REFRESH);
                    handleCommand(new ChannelUID(getThing().getUID(), CHANNEL_USED_LOW), RefreshType.REFRESH);
                    if (--_versionRefreshCnt <= 0) {
                        _versionRefreshCnt = VERSION_REFRESH_INTERVAL;
                        handleCommand(new ChannelUID(getThing().getUID(), CHANNEL_ENGINE), RefreshType.REFRESH);
                    }
                } catch (Exception e) {
                    _logger.debug("Exception occurred during execution: {}", e.getMessage(), e);
                }
            }
        };

        _refreshJob = scheduler.scheduleAtFixedRate(runnable, 0, 60, TimeUnit.SECONDS);
    }

    /**
     * Stop the automatic refresh.
     */
    private void stopAutomaticRefresh() {
        if (_refreshJob != null) {
            _refreshJob.cancel(true);
            _refreshJob = null;
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
