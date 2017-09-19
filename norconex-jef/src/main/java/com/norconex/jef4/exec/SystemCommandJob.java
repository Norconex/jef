/* Copyright 2010-2017 Norconex Inc.
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
package com.norconex.jef4.exec;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.norconex.commons.lang.exec.SystemCommand;
import com.norconex.commons.lang.exec.SystemCommandException;
import com.norconex.jef4.job.IJob;
import com.norconex.jef4.job.JobException;
import com.norconex.jef4.status.IJobStatus;
import com.norconex.jef4.status.JobStatusUpdater;
import com.norconex.jef4.suite.JobSuite;

/**
 * JEF job for executing an arbitrary number of commands.  The job progress is
 * relative to the number of commands to be processed.  This is ideal
 * for submitting a batch of small commands where execution time is not
 * long enough to track individual command progresses.  For long-running
 * commands, you may want to create a custom job where you can keep track
 * of the command progress and translate that somehow into a job progress
 * (whenever possible).
 * @author Pascal Essiembre
 * @since 1.1
 */
public class SystemCommandJob implements IJob {

    /** Logger. */
    private static final Logger LOG =
            LoggerFactory.getLogger(SystemCommandJob.class);

    /** Job unique id. */
    private final String id;
    /** Commands to be executed. */
    private final SystemCommand[] systemCommands;

    
    /**
     * Creates a JEF job for executing system commands.  This is a convenience
     * constructor for taking multiple systems commands as string, every
     * commands supplied are converted to {@link SystemCommand} instances
     * internally.
     * @param id job id
     * @param commands array of individual commands to be executed
     * @see SystemCommandJob#SystemCommandJob(String, SystemCommand[])
     */
    public SystemCommandJob(String id, String... commands) {
        super();
        if (commands == null) {
            throw new IllegalArgumentException(
                    "\"commands\" argument cannot be null.");
        }
        this.id = id;
        this.systemCommands = new SystemCommand[commands.length];
        for (int i = 0; i < commands.length; i++) {
            this.systemCommands[i] = new SystemCommand(commands[i]);
        }
    }
    
    /**
     * Creates a JEF job for executing system commands.
     * @param id job id
     * @param commands commands to be executed
     */
    public SystemCommandJob(
            String id, SystemCommand... commands) {
        super();
        this.id = id;
        this.systemCommands = ArrayUtils.clone(commands);
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public void execute(JobStatusUpdater statusUpdater, JobSuite suite) {
        double commandCount = systemCommands.length;
        double commandsRan = statusUpdater.getProperties().getDouble("ran", 0d);
        for (int i = (int) commandsRan; i < commandCount; i++) {
            SystemCommand systemCommand = systemCommands[i];
            if (LOG.isDebugEnabled()) {
                LOG.debug("Executing command: " + systemCommand);
            }
            statusUpdater.setNote("Executing: " + systemCommand);
            try {
                systemCommand.execute();
                statusUpdater.setProgress(commandsRan / commandCount);
            } catch (SystemCommandException e) {
                throw new JobException("Cannot execute command: "
                        + systemCommand, e);
            }
        }
        statusUpdater.setNote("Done.");
    }
    
    /**
     * Default implementation of this method will check if the 
     * {@link SystemCommand} it runs is currently holding to a 
     * {@link Process} process instance and destroys them.
     * If no such instance is found, and even if one is found, there are 
     * no guarantees what will happen.  Implementors having better ways to
     * stop the invoked command should overwrite this method.
     * @since 2.0
     */
    @Override
    public void stop(IJobStatus status, JobSuite suite) {
        for (SystemCommand command : systemCommands) {
            command.abort();
        }
    }
}
