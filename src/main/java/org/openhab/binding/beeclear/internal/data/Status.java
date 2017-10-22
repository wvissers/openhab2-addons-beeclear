/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.beeclear.internal.data;

/**
 * On regular intervals, the status of the energy
 * meter will be retrieved from the BeeClear. This interface is
 * intended to have a common interface, independent of the
 * BeeClear firmware version.
 *
 * @author Wim Vissers - Initial contribution
 */
public interface Status {

    /**
     * Return true if the P1 signal is available.
     *
     * @return true if BeeClear returns 1.
     */
    public boolean isP1();

    /**
     * Return true if the SD card is available.
     *
     * @return
     */
    public boolean isSdCard();

    /**
     * Return the percentage of free memory on the SD card.
     *
     * @return
     */
    public String getSdCardFree();

    /**
     * Return the total amount of memory on the SD card.
     *
     * @return
     */
    public String getSdCardTotal();

}
