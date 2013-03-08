package com.norconex.jef.error;

/**
 * Job Execution Framework error handler. Error handlers are typically added to
 * a <code>JobSuite</code>.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 */
public interface IErrorHandler {

    /**
     * Handles an error.
     * @param event error event
     */
    void handleError(IErrorEvent event);

}
