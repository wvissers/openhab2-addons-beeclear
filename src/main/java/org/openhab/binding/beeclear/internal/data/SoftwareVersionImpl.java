/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.beeclear.internal.data;

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

    private JSONObject _jsonObj;

    public SoftwareVersionImpl(JSONObject jsonObj) {
        _jsonObj = jsonObj;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getName() {
        return _jsonObj.getOrDefault("www", "").toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getFirmware() {
        return _jsonObj.getOrDefault("firmware", "").toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getHardware() {
        return _jsonObj.getOrDefault("hardware", "").toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getProtocolVersion() {
        return _jsonObj.getOrDefault("protocolVersion", "").toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getSerialElec() {
        return _jsonObj.getOrDefault("serialElec", "").toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getSerialGas() {
        return _jsonObj.getOrDefault("serialGas", "").toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getInfo() {
        return _jsonObj.getOrDefault("info", "").toString();
    }

}
