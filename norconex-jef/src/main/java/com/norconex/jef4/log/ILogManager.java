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
package com.norconex.jef4.log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.log4j.Appender;

import com.norconex.commons.lang.config.IXMLConfigurable;

/**
 * Class responsible for everything that relates to the underlying logging
 * mechanism.
 * <p/>
 * When saving as XML, the tag name must be called "logManager".
 * @author Pascal Essiembre
 */
public interface ILogManager extends IXMLConfigurable {

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
