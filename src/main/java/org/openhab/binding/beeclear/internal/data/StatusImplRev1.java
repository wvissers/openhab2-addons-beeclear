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
 * On regular intervals, the status of the energy
 * meter will be retrieved from the BeeClear. This interface is
 * intended to have a common interface, independent of the
 * BeeClear firmware version.
 *
 * @author Wim Vissers - Initial contribution
 */
public class StatusImplRev1 implements Status {

    private JSONObject jsonObj;

    public StatusImplRev1(JSONObject jsonObj) {
        this.jsonObj = jsonObj;
    }

    /**
     * Return true if the P1 signal is available.
     *
     * @return true if BeeClear returns 1.
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean isP1() {
        return jsonObj.getOrDefault("p1", "0").toString().equals("1");
    }

    /**
     * Return true if the SD card is available.
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean isSdCard() {
        return jsonObj.getOrDefault("sdcard", "0").toString().equals("1");
    }

    /**
     * Return the percentage of free memory on the SD card.
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public String getSdCardFree() {
        return jsonObj.getOrDefault("sdcardFree", "0%").toString();
    }

    /**
     * Return the total amount of memory on the SD card.
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public String getSdCardTotal() {
        return jsonObj.getOrDefault("sdcardTotal", "0 MB").toString();
    }

}
