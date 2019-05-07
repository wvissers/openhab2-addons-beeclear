/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.beeclear;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link BeeClearBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Wim Vissers - Initial contribution
 */
public class BeeClearBindingConstants {

    private static final String BINDING_ID = "beeclear";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_METER = new ThingTypeUID(BINDING_ID, "meter");

    // List of all Channel ids
    public static final String CHANNEL_ONLINE = "online";
    public static final String CHANNEL_POWER = "power";
    public static final String CHANNEL_POWER_HIGH = "powerHigh";
    public static final String CHANNEL_POWER_LOW = "powerLow";
    public static final String CHANNEL_USED_HIGH = "usedHigh";
    public static final String CHANNEL_USED_LOW = "usedLow";
    public static final String CHANNEL_USED_GAS = "gas";
    public static final String CHANNEL_TARIFF = "tariff";
    public static final String CHANNEL_FIRMWARE = "firmware";
    public static final String CHANNEL_HARDWARE = "hardware";
    public static final String CHANNEL_SERIAL_ELEC = "serialElec";
    public static final String CHANNEL_SERIAL_GAS = "serialGas";
    public static final String CHANNEL_METER_TYPE = "meterType";
    public static final String CHANNEL_PROTOCOL_VERSION = "protocolVersion";
    public static final String CHANNEL_UPTIME = "uptime";
    public static final String CHANNEL_P1 = "p1";
    public static final String CHANNEL_SDCARD = "sdcard";
    public static final String CHANNEL_SDCARD_FREE = "sdcardFree";
    public static final String CHANNEL_SDCARD_TOTAL = "sdcardTotal";
    public static final String CHANNEL_IP = "ip";

}
