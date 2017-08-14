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
 * On regular intervals, the current active values of the energy
 * meter will be retrieved from the BeeClear. This interface is
 * intended to have a common interface, independent of the
 * BeeClear firmware version.
 *
 * @author Wim Vissers - Initial contribution
 */
public interface ActiveValues {

    /**
     * Get the value of the used electricity meter (high tariff).
     *
     * @return value in kWh.
     */
    public BigDecimal getUsedElectricityHigh();

    /**
     * Get the value of the used electricity meter (low tariff).
     *
     * @return value in kWh.
     */
    public BigDecimal getUsedElectricityLow();

    /**
     * Get the value of the generated electricity meter (high tariff).
     *
     * @return value in kWh.
     */
    public BigDecimal getGeneratedElectricityHigh();

    /**
     * Get the value of the generated electricity meter (low tariff).
     *
     * @return value in kWh.
     */
    public BigDecimal getGeneratedElectricityLow();

    /**
     * Get the value of the actual user power.
     *
     * @return value in kW.
     */
    public BigDecimal getUsedPower();

    /**
     * Get the value of the actual generated power.
     *
     * @return value in kW.
     */
    public BigDecimal getGeneratedPower();

    /**
     * Get the value of the used gas meter.
     *
     * @return value in cubic meters.
     */
    public BigDecimal getUsedGas();

    /**
     * Return the current tariff status. Either high or low.
     * 
     * @return
     */
    public TariffStatusType getTariffStatus();

}
