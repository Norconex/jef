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
    public void startListening(ISuiteStopRequestListener listener);
    /**
     * Stops listening for stop requests.
     */
    public void stopListening();
}
