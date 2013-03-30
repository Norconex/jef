package com.norconex.jef.log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.log4j.Appender;

/**
 * Class responsible for everything that relates to the underlying logging
 * mechanism.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 */
public interface ILogManager {

    /**
     * Creates a Log4J appender for the given name space.
     * @param namespace namespace of the executing context
     * @return an appender
     * @throws IOException problem creating the appender
     */
    Appender createAppender(String namespace) throws IOException;

    /**
     * Gets the log for the given namespace.
     * @param namespace namespace of the executing context
     * @return the log
     * @throws IOException problem getting log
     */
    InputStream getLog(String namespace) throws IOException;

    /**
     * Gets the log for the given namespace and job identifier.
     * @param namespace namespace of the executing context
     * @param jobId job identifier
     * @return the log
     * @throws IOException problem getting log
     */
    InputStream getLog(String namespace, String jobId) throws IOException;

    
    /**
     * Backups the log for the given name space, and time stamps it with
     * the given date.  A backed-up log can no longer be retrieved by
     * the <code>getLog(String)</code> method.
     * @param namespace namespace of the executing context
     * @param backupDate date of this backup
     * @throws IOException problem creating backup
     */
    void backup(String namespace, Date backupDate) throws IOException;

}
