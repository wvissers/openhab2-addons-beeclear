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
import java.util.ArrayList;
import java.util.List;
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
import org.openhab.binding.beeclear.internal.BeeClearRegistry;
import org.openhab.binding.beeclear.internal.DataCollectorFacade;
import org.openhab.binding.beeclear.internal.data.ActiveValues;
import org.openhab.binding.beeclear.internal.data.ActiveValuesImplRev1;
import org.openhab.binding.beeclear.internal.data.TariffStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BeeClearHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Wim Vissers - Initial contribution
 */
public class BeeClearHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(BeeClearHandler.class);

    // The Facade to the BeeClear restfull webAPI.
    private DataCollectorFacade data;

    // The last retrieved actual data
    private ActiveValues activeValues;

    // Helper fields and constants
    private boolean online;

    // The unique id
    private String id;

    // List with channels to update fast (every minute)
    private final List<ChannelUID> refreshFast;

    // List with channels to update slow.
    private final List<ChannelUID> refreshSlow;

    // Scheduler to retrieve data from time to time.
    ScheduledFuture<?> refreshJob;

    public BeeClearHandler(Thing thing) {
        super(thing);
        refreshFast = new ArrayList<>();
        refreshFast.add(new ChannelUID(getThing().getUID(), CHANNEL_UPTIME));
        refreshFast.add(new ChannelUID(getThing().getUID(), CHANNEL_POWER));
        refreshFast.add(new ChannelUID(getThing().getUID(), CHANNEL_POWER_HIGH));
        refreshFast.add(new ChannelUID(getThing().getUID(), CHANNEL_POWER_LOW));
        refreshFast.add(new ChannelUID(getThing().getUID(), CHANNEL_USED_HIGH));
        refreshFast.add(new ChannelUID(getThing().getUID(), CHANNEL_USED_LOW));
        refreshFast.add(new ChannelUID(getThing().getUID(), CHANNEL_USED_GAS));
        refreshSlow = new ArrayList<>();
        refreshSlow.add(new ChannelUID(getThing().getUID(), CHANNEL_TARIFF));
        refreshSlow.add(new ChannelUID(getThing().getUID(), CHANNEL_FIRMWARE));
        refreshSlow.add(new ChannelUID(getThing().getUID(), CHANNEL_HARDWARE));
        refreshSlow.add(new ChannelUID(getThing().getUID(), CHANNEL_SERIAL_ELEC));
        refreshSlow.add(new ChannelUID(getThing().getUID(), CHANNEL_SERIAL_GAS));
        refreshSlow.add(new ChannelUID(getThing().getUID(), CHANNEL_METER_TYPE));
        refreshSlow.add(new ChannelUID(getThing().getUID(), CHANNEL_PROTOCOL_VERSION));
        online = false;
        activeValues = new ActiveValuesImplRev1(new JSONObject());
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            switch (channelUID.getId()) {
                case CHANNEL_POWER:
                    updateState(channelUID, new DecimalType(activeValues.getUsedPower()));
                    break;
                case CHANNEL_POWER_HIGH:
                    if (activeValues.getTariffStatus() == TariffStatusType.HIGH) {
                        updateState(channelUID, new DecimalType(activeValues.getUsedPower()));
                    } else {
                        updateState(channelUID, DecimalType.ZERO);
                    }
                    break;
                case CHANNEL_POWER_LOW:
                    if (activeValues.getTariffStatus() == TariffStatusType.LOW) {
                        updateState(channelUID, new DecimalType(activeValues.getUsedPower()));
                    } else {
                        updateState(channelUID, DecimalType.ZERO);
                    }
                    break;
                case CHANNEL_USED_HIGH:
                    updateState(channelUID, new DecimalType(activeValues.getUsedElectricityHigh()));
                    break;
                case CHANNEL_USED_LOW:
                    updateState(channelUID, new DecimalType(activeValues.getUsedElectricityLow()));
                    break;
                case CHANNEL_USED_GAS:
                    updateState(channelUID, new DecimalType(activeValues.getUsedGas()));
                    break;
                case CHANNEL_TARIFF:
                    updateState(channelUID, new StringType("" + activeValues.getTariffStatus()));
                    break;
                case CHANNEL_FIRMWARE:
                    updateState(channelUID, new StringType(data.getSoftwareVersion().getFirmware()));
                    break;
                case CHANNEL_HARDWARE:
                    updateState(channelUID, new StringType(data.getSoftwareVersion().getHardware()));
                    break;
                case CHANNEL_SERIAL_ELEC:
                    updateState(channelUID, new StringType(data.getSoftwareVersion().getSerialElec()));
                    break;
                case CHANNEL_SERIAL_GAS:
                    updateState(channelUID, new StringType(data.getSoftwareVersion().getSerialGas()));
                    break;
                case CHANNEL_METER_TYPE:
                    updateState(channelUID, new StringType(data.getSoftwareVersion().getName()));
                    break;
                case CHANNEL_PROTOCOL_VERSION:
                    updateState(channelUID, new StringType(data.getSoftwareVersion().getProtocolVersion()));
                    break;
                case CHANNEL_UPTIME:
                    updateState(channelUID, new DecimalType(data.getSoftwareVersion().getUptimeHours()));
                    break;
                default:
                    logger.warn("Unexpected channel {}", channelUID);
            }
        } else {
            logger.warn("Unexpected command type {}", command.getClass().getName());
        }
    }

    @Override
    public void initialize() {
        Configuration config = getThing().getConfiguration();
        String host = (String) config.get("host");
        BigDecimal port = ((BigDecimal) config.get("port"));

        // Register the device
        id = BeeClearRegistry.getInstance().registerByName(host, port.intValue());

        // Create a Facade to the API
        data = new DataCollectorFacade(host, port.intValue());

        if (data.getSoftwareVersion().getInfo().equals("notAuthenticated")) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Not authenticated. Please use the BeeClear software, login and enter http://youraddress/bc_securitybc_security?set=off and try again.");
            online = false;
        } else if (!data.isVersionSupported()) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Firmware version " + data.getSoftwareVersion().getFirmware() + " not supported.");
            online = false;
        } else {
            updateStatus(ThingStatus.ONLINE);
            online = true;
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
                    if (online) {
                        if (data.isVersionDataExpired()) {
                            for (ChannelUID channel : refreshSlow) {
                                handleCommand(channel, RefreshType.REFRESH);
                            }
                        }

                        activeValues = data.getActiveValues();
                        for (ChannelUID channel : refreshFast) {
                            handleCommand(channel, RefreshType.REFRESH);
                        }
                    }
                } catch (Exception e) {
                    logger.debug("Exception occurred during execution: {}", e.getMessage(), e);
                }
            }
        };

        refreshJob = scheduler.scheduleAtFixedRate(runnable, 0, 60, TimeUnit.SECONDS);
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
        BeeClearRegistry.getInstance().remove(id);
    }

}
