/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nslookup;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link NsLookupBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Wim Vissers - Initial contribution
 */
public class NsLookupBindingConstants {

    private static final String BINDING_ID = "nslookup";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_HOST = new ThingTypeUID(BINDING_ID, "host");

    // List of all Channel ids
    public static final String CHANNEL_ONLINE = "online";
    public static final String CHANNEL_IP = "ip";

}
