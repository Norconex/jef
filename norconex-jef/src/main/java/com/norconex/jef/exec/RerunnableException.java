/* Copyright 2010-2013 Norconex Inc.
 * 
 * This file is part of Norconex JEF.
 * 
 * Norconex JEF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Norconex JEF is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Norconex JEF. If not, see <http://www.gnu.org/licenses/>.
 */
package com.norconex.jef.exec;

/**
 * An exception throws by a executing {@link Rerunner} instance when the 
 * code executed itself threw an exception, or maximum retry attempts has
 * been reached.  In cases when the code being rerun threw one or more
 * exception, this exception's cause will hold the last exception thrown
 * by that code.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 * @see Rerunner
 */
public class RerunnableException extends Exception {

    /** For serialisation. */
    private static final long serialVersionUID = 5236102272021889018L;

    /**
     * @see Exception#Exception(java.lang.String)
     */
    public RerunnableException(final String message) {
        super(message);
    }
    /**
     * @see Exception#Exception(java.lang.Throwable)
     */
    public RerunnableException(final Throwable cause) {
        super(cause);
    }
    /**
     * @see Exception#Exception(java.lang.String, java.lang.Throwable)
     */
    public RerunnableException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
