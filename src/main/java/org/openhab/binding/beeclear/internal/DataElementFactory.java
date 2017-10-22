/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.beeclear.internal;

import org.json.simple.JSONObject;
import org.openhab.binding.beeclear.internal.data.ActiveValues;
import org.openhab.binding.beeclear.internal.data.ActiveValuesImplRev1;
import org.openhab.binding.beeclear.internal.data.SoftwareVersion;
import org.openhab.binding.beeclear.internal.data.Status;
import org.openhab.binding.beeclear.internal.data.StatusImplRev1;
import org.openhab.binding.beeclear.internal.data.UnsupportedVersionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The data element factory is able to create data elements based on the
 * software version and json response string.
 *
 * @author Wim Vissers - Initial contribution
 */
public class DataElementFactory {

    private static final Logger logger = LoggerFactory.getLogger(DataElementFactory.class);
    private static final DataElementFactory INSTANCE = new DataElementFactory();

    /**
     * Private constructor to avoid creating more than one instance.
     */
    private DataElementFactory() {
        logger.debug("Creating DataElementFactory {}", this.toString());
    }

    /**
     * Return the singleton instance.
     *
     * @return the singleton instance.
     */
    public static final DataElementFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Create an ActiveValues DataElement for a given BeeClear software
     * version and jsonResponse from the BeeClear device.
     *
     * @param softwareVersion the BeeClear software version.
     * @param jsonObj the response retrieved from the BeeClear device.
     * @return a new ActiveValues DataElement object.
     */
    public ActiveValues createActiveValues(SoftwareVersion softwareVersion, JSONObject jsonObj)
            throws UnsupportedVersionException {
        if (ActiveValuesImplRev1.isSupported(softwareVersion)) {
            return new ActiveValuesImplRev1(jsonObj);
        } else {
            throw new UnsupportedVersionException("This version of BeeClear software not supported.");
        }
    }

    /**
     * Create a Status DataElement for a given BeeClear software
     * version and jsonResponse from the BeeClear device.
     *
     * @param softwareVersion the BeeClear software version.
     * @param jsonObj the response retrieved from the BeeClear device.
     * @return a new ActiveValues DataElement object.
     */
    public Status createStatus(SoftwareVersion softwareVersion, JSONObject jsonObj) throws UnsupportedVersionException {
        if (ActiveValuesImplRev1.isSupported(softwareVersion)) {
            return new StatusImplRev1(jsonObj);
        } else {
            throw new UnsupportedVersionException("This version of BeeClear software not supported.");
        }
    }

    /**
     * Determine from the retrieved SoftwareVersion object if the version is supported.
     * 
     * @param softwareVersion
     * @return
     */
    public boolean isSupported(SoftwareVersion softwareVersion) {
        boolean result = true;
        try {
            createActiveValues(softwareVersion, new JSONObject());
        } catch (UnsupportedVersionException e) {
            result = false;
        }
        return result;
    }

}
