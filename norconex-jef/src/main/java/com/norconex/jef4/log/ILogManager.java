/* Copyright 2010-2014 Norconex Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * <br><br>
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
