/* Copyright 2017 Norconex Inc.
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
package com.norconex.jef5.log;

import java.io.IOException;
import java.io.Reader;
import java.util.Date;
import java.util.List;

/**
 * Class responsible for everything that relates to the underlying logging
 * mechanism.
 * @author Pascal Essiembre
 */
public interface ILogManager {

    //TODO pass some kind of init string that tells how to configure
    //this manager.  That string will be used for both write and read,
    //but for reading, no need to (un)register. (so JefMon can use).
    
    //TODO allow disabling JEF logger altogether, but done by a flag in jef
    //config... not here.
    
    //TODO document that the pattern must be of a specific format
    //OR: check if we can always decorate the pattern with the job/suite id.

    //TODO document that SLF4J is used and registering this log manger should
    //be adding appenders to the concrete implementation.
    
    //TODO have log4jManager, logbackManager, and the default, which guesses
    //which one to use.
    
    //TODO either wrap in ThreadSafeLayout for each logging system, or 
    //document that lines should be prefixed with thread name (probably simpler
    // for implementors?
    
//    void register(ILoggerFactory loggerFactory);
//    void unregister(ILoggerFactory loggerFactory);
    
// Call start/stop instead???    
    
    void init(String initString); // e.g, config is file path for FileManager
    void destroy();
    
    //TODO do we pass jobSuite to above methods and remove these:
//    void register(JobSuite suite); // pass config instead?
//    void unregister(JobSuite suite);  // pass config instead?
    
    
    // if jobId is null, then we return for full suite.
    // do we still go about prefixing the sing log lines with the job, 
    // or multi log files (less preferred).
    Reader getReader(String suiteId, String jobId) throws IOException;
    List<String> tail(String suiteId, String jobId, int qty) throws IOException;
    List<String> head(String suiteId, String jobId, int qty) throws IOException;
    
    // cannot really pass file since it could be database or else.
    void backup(String namespace, Date backupDate, int qtyToKeep) 
            throws IOException;
    
    
//  InputStream getLog(String namespace) throws IOException;
//    InputStream getLog(String namespace, String jobId) throws IOException;

    
    /**
     * Backups the log for the given name space, and time stamps it with
     * the given date.  A backed-up log can no longer be retrieved by
     * the <code>getLog(String)</code> method.
     * @param namespace namespace of the executing context
     * @param backupDate date of this backup
     * @throws IOException problem creating backup
     */
//    void backup(String namespace, Date backupDate) throws IOException;

}
