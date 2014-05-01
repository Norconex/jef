/* Copyright 2010-2014 Norconex Inc.
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
package com.norconex.jef;

/**
 * Represents a job-related exception.  Implementors are invited to
 * wrap exceptions they want explicitly handled by the framework in instances
 * of <code>JobException</code>.
 * @author Pascal Essiembre
 */
public class JobException extends RuntimeException {

    /** For serialisation. */
    private static final long serialVersionUID = 5236102272021889018L;

    /**
     * @see Exception#Exception(java.lang.String)
     */
    public JobException(final String arg0) {
        super(arg0);
    }
    /**
     * @see Exception#Exception(java.lang.Throwable)
     */
    public JobException(final Throwable arg0) {
        super(arg0);
    }
    /**
     * @see Exception#Exception(java.lang.String, java.lang.Throwable)
     */
    public JobException(final String arg0, final Throwable arg1) {
        super(arg0, arg1);
    }
}
