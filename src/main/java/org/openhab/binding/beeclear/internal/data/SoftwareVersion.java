/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.beeclear.internal.data;

import java.math.BigDecimal;

/**
 * On initialization of the handler, the software version of the
 * BeeClear unit will be stored for future reference. Factories
 * use the version information to interpret the data correctly.
 *
 * @author Wim Vissers - Initial contribution
 */
public interface SoftwareVersion {

    /**
     * Get the name part of the json response which apparently
     * represents the meter type.
     *
     * @return
     */
    public String getName();

    /**
     * Get the firmware part of the json response.
     *
     * @return
     */
    public String getFirmware();

    /**
     * Get the hardware part of the json response.
     *
     * @return
     */
    public String getHardware();

    /**
     * Get the protocolVersion part of the json response.
     *
     * @return
     */
    public String getProtocolVersion();

    /**
     * Get the serialElec part of the json response.
     *
     * @return
     */
    public String getSerialElec();

    /**
     * Get the serialGas part of the json response.
     *
     * @return
     */
    public String getSerialGas();

    /**
     * Get the up time in hours.
     *
     * @return
     */
    public BigDecimal getUptimeHours();

    /**
     * Get the info (basically the response status).
     *
     * @return
     */
    public String getInfo();

}
