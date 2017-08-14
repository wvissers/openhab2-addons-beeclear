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
 * Exception to indicate that a data element for the given software
 * version could not be created.
 *
 * @author Wim Vissers - Initial contribution
 */
public class UnsupportedVersionException extends Exception {

    private static final long serialVersionUID = 100290201L;

    public UnsupportedVersionException(String message) {
        super(message);
    }

}
