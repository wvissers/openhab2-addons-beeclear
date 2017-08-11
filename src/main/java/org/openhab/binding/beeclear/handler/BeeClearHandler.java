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
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.json.simple.JSONObject;
import org.openhab.binding.beeclear.internal.DataCollectorFacade;
import org.openhab.binding.beeclear.internal.data.ActiveValues;
import org.openhab.binding.beeclear.internal.data.ActiveValuesImplRev1;
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

    // The Facade to the BeeClear restfull webAPI.
    private DataCollectorFacade _data;

    // The last retrieved actual data
    private ActiveValues _activeValues;

    // Helper fields and constants
    private boolean _online;

    // Scheduler to retrieve data from time to time.
    ScheduledFuture<?> _refreshJob;

    public BeeClearHandler(Thing thing) {
        super(thing);
        _online = false;
        _activeValues = new ActiveValuesImplRev1(new JSONObject());
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            switch (channelUID.getId()) {
                case CHANNEL_POWER:
                    updateState(channelUID, new DecimalType(_activeValues.getUsedPower()));
                    break;
                case CHANNEL_USED_HIGH:
                    updateState(channelUID, new DecimalType(_activeValues.getUsedElectricityHigh()));
                    break;
                case CHANNEL_USED_LOW:
                    updateState(channelUID, new DecimalType(_activeValues.getUsedElectricityLow()));
                    break;
                case CHANNEL_USED_GAS:
                    updateState(channelUID, new DecimalType(_activeValues.getUsedGas()));
                    break;
                case CHANNEL_TARIFF:
                    updateState(channelUID, new StringType("" + _activeValues.getTariffStatus()));
                    break;
                case CHANNEL_FIRMWARE:
                    updateState(channelUID, new StringType(_data.getSoftwareVersion().getFirmware()));
                    break;
                case CHANNEL_HARDWARE:
                    updateState(channelUID, new StringType(_data.getSoftwareVersion().getHardware()));
                    break;
                case CHANNEL_SERIAL_ELEC:
                    updateState(channelUID, new StringType(_data.getSoftwareVersion().getSerialElec()));
                    break;
                case CHANNEL_SERIAL_GAS:
                    updateState(channelUID, new StringType(_data.getSoftwareVersion().getSerialGas()));
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
        String host = (String) config.get("host");
        BigDecimal port = ((BigDecimal) config.get("port"));

        // Create a Facade to the API
        _data = new DataCollectorFacade(host, port.intValue());

        if (_data.getSoftwareVersion().getInfo().equals("notAuthenticated")) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Not authenticated. Please use the BeeClear software, login and enter http://youraddress/bc_securitybc_security?set=off and try again.");
            _online = false;
        } else if (!_data.isVersionSupported()) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Firmware version " + _data.getSoftwareVersion().getFirmware() + " not supported.");
            _online = false;
        } else {
            updateStatus(ThingStatus.ONLINE);
            _online = true;
        }
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
                    if (_online) {
                        if (_data.isVersionDataExpired()) {
                            handleCommand(new ChannelUID(getThing().getUID(), CHANNEL_FIRMWARE), RefreshType.REFRESH);
                            handleCommand(new ChannelUID(getThing().getUID(), CHANNEL_HARDWARE), RefreshType.REFRESH);
                            handleCommand(new ChannelUID(getThing().getUID(), CHANNEL_SERIAL_ELEC),
                                    RefreshType.REFRESH);
                            handleCommand(new ChannelUID(getThing().getUID(), CHANNEL_SERIAL_GAS), RefreshType.REFRESH);
                        }

                        _activeValues = _data.getActiveValues();
                        handleCommand(new ChannelUID(getThing().getUID(), CHANNEL_POWER), RefreshType.REFRESH);
                        handleCommand(new ChannelUID(getThing().getUID(), CHANNEL_USED_HIGH), RefreshType.REFRESH);
                        handleCommand(new ChannelUID(getThing().getUID(), CHANNEL_USED_LOW), RefreshType.REFRESH);
                        handleCommand(new ChannelUID(getThing().getUID(), CHANNEL_USED_GAS), RefreshType.REFRESH);
                        handleCommand(new ChannelUID(getThing().getUID(), CHANNEL_TARIFF), RefreshType.REFRESH);
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
