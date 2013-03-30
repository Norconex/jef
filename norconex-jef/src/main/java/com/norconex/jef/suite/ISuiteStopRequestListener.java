package com.norconex.jef.suite;

import java.io.Serializable;

/**
 * Implementations are waiting to be notified when a 
 * {@link IJobSuiteStopRequestHandler} received a stop signal.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 * @since 2.0
 */
public interface ISuiteStopRequestListener extends Serializable {

    /**
     * Invoked when a stop signal was received.
     */
    void stopRequestReceived();
}
