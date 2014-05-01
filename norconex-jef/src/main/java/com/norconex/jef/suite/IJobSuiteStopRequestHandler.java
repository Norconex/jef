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
package com.norconex.jef.suite;

import java.io.Serializable;

/**
 * Implementations of this interface will listen for stop signals sent 
 * to a running suite.   It is up to implementors to define the mechanism
 * by which to stop a running suite.  
 * @author Pascal Essiembre
 * @since 2.0
 */
public interface IJobSuiteStopRequestHandler extends Serializable {

    /**
     * Starts to listen for stop requests. Implementors <b>must</b> call
     * the {@link ISuiteStopRequestListener#stopRequestReceived()} method
     * when a stop request was received.
     * @param listener framework listener for stop signals
     */
    void startListening(ISuiteStopRequestListener listener);
    /**
     * Stops listening for stop requests.
     */
    void stopListening();
}
