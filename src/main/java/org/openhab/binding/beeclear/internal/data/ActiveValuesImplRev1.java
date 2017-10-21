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
 * On regular intervals, the current active values of the energy
 * meter will be retrieved from the BeeClear. This interface is
 * intended to have a common interface, independent of the
 * BeeClear firmware version.
 *
 * @author Wim Vissers - Initial contribution
 */
public class ActiveValuesImplRev1 implements ActiveValues {

    private JSONObject jsonObj;

    public ActiveValuesImplRev1(JSONObject jsonObj) {
        this.jsonObj = jsonObj;
    }

    /**
     * Returns true if the DataElement supports the given version
     * of the BeeClear software.
     *
     * @param softwareVersion the BeeClear software version.
     * @return true if the version is supported.
     */
    public static boolean isSupported(SoftwareVersion softwareVersion) {
        return softwareVersion.getFirmware().startsWith("49.");
    }

    /**
     * Helper method. The input String is considered a number without
     * decimal point. It is divided by 1000 by introducing a decimal
     * point at the right position. The use of double is avoided to
     * overcome the issue with a large number of digits after the
     * decimal point.
     *
     * @param input
     * @return
     */
    private BigDecimal convert(String input) {
        // Make sure the length is sufficient
        String a = input == null ? "0000" : "0000" + input;
        int p = a.length() - 3;
        String x = a.substring(0, p) + "." + a.substring(p);
        return new BigDecimal(x);
    }

    @SuppressWarnings("unchecked")
    @Override
    public BigDecimal getUsedElectricityHigh() {
        return convert(jsonObj.getOrDefault("uh", "0").toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public BigDecimal getUsedElectricityLow() {
        return convert(jsonObj.getOrDefault("ul", "0").toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public BigDecimal getGeneratedElectricityHigh() {
        return convert(jsonObj.getOrDefault("gh", "0").toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public BigDecimal getGeneratedElectricityLow() {
        return convert(jsonObj.getOrDefault("gl", "0").toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public BigDecimal getUsedPower() {
        return new BigDecimal(jsonObj.getOrDefault("u", "0").toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public BigDecimal getGeneratedPower() {
        return new BigDecimal(jsonObj.getOrDefault("g", "0").toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public BigDecimal getUsedGas() {
        return new BigDecimal(jsonObj.getOrDefault("gas", "0").toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public TariffStatusType getTariffStatus() {
        Long status = (Long) jsonObj.getOrDefault("tariefStatus", 0);
        try {
            return TariffStatusType.getByCode(status.intValue());
        } catch (UnsupportedTariffCodeException e) {
            return null;
        }
    }

}
