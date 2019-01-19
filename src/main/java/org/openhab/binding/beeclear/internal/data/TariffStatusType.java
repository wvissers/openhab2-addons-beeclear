/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.beeclear.internal.data;

/**
 * Tariff for electricity can be eitehr high or low.
 *
 * @author Wim Vissers - Initial contribution
 */
public enum TariffStatusType {

    HIGH(2),
    LOW(1);

    private final int _code;

    private TariffStatusType(int code) {
        _code = code;
    }

    public static TariffStatusType getByCode(int code) throws UnsupportedTariffCodeException {
        if (HIGH._code == code) {
            return HIGH;
        } else if (LOW._code == code) {
            return LOW;
        } else {
            throw new UnsupportedTariffCodeException("Tariff code " + code + " is not supported.");
        }
    }

}
