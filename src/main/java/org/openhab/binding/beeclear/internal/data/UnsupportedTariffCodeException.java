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
 * Exception to indicate that a tariff code could is not supported.
 *
 * @author Wim Vissers - Initial contribution
 */
public class UnsupportedTariffCodeException extends Exception {

    private static final long serialVersionUID = 1261692707369771411L;

    public UnsupportedTariffCodeException(String message) {
        super(message);
    }

}
