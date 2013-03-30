package com.norconex.jef.error;

/**
 * Job Execution Framework error handler. Error handlers are typically added to
 * a <code>JobSuite</code>.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 */
public interface IErrorHandler {

    /**
     * Handles an error.
     * @param event error event
     */
    void handleError(IErrorEvent event);

}
