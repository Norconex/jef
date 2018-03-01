/* Copyright 2018 Norconex Inc.
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
package com.norconex.jef5.shutdown;

import java.nio.file.Path;

import com.norconex.jef5.suite.JobSuite;

/**
 * Responsible listening for shutdown events as well as firing them.
 * The listening happens during the job suite life duration. The firing
 * is normally done by a separate JVM instance.
 * @author Pascal Essiembre
 */
public interface IShutdownHook {

    //TODO add hook implementation as part of suite index.
    //TODO filebased + port based + Runtime.getRuntime().addShutdownHook(new Thread() 
    
    /**
     * Setup and/or start the shutdown hook, which can be terminated
     * by invoking shutdown in the same or different JVM. 
     * @param suite job suite
     */
    void setup(JobSuite suite);
    /**
     * Destroys resources allocated with this shutdown hook.  
     * Called at the end of job suite execution upon completion. 
     */
    void destroy();
    
    /**
     * Shuts down a currently running job suite.
     * @param indexFile path to index file, containing suite session info
     * @return <code>true</code> if the process was running and successfully 
     *         shut down. <code>false</code> if no process running.
     * @throws ShutdownException could not shutdown running suite.
     */
    boolean shutdown(Path indexFile) throws ShutdownException;
}
