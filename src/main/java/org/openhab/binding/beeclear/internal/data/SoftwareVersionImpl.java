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

import org.json.simple.JSONObject;

/**
 * The basic implementation of the SoftwareVersion interface. It is not
 * likely that this class will change with different versions, unlike
 * the other data classes that may change from version to version. The
 * factories will use version information to extract data from the JSON
 * responses correctly.
 *
 * @author Wim Vissers - Initial contribution
 */
public class SoftwareVersionImpl implements SoftwareVersion {

    private JSONObject jsonObj;

    public SoftwareVersionImpl(JSONObject jsonObj) {
        this.jsonObj = jsonObj;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getName() {
        return jsonObj.getOrDefault("name", "").toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getFirmware() {
        return jsonObj.getOrDefault("firmware", "").toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getHardware() {
        return jsonObj.getOrDefault("hardware", "").toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getProtocolVersion() {
        return jsonObj.getOrDefault("protocolVersion", "").toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getSerialElec() {
        return jsonObj.getOrDefault("serialElec", "").toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getSerialGas() {
        return jsonObj.getOrDefault("serialGas", "").toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getInfo() {
        return jsonObj.getOrDefault("info", "").toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public BigDecimal getUptimeHours() {
        return new BigDecimal((Long) jsonObj.getOrDefault("uptime", 0L) / 60 / 60);
    }

}
