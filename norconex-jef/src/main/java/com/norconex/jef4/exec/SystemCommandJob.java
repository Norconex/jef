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
package com.norconex.jef4.exec;

import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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
            LogManager.getLogger(SystemCommandJob.class);

    /** Job unique name. */
    private final String name;
    /** Commands to be executed. */
    private final SystemCommand[] systemCommands;

    
    /**
     * Creates a JEF job for executing system commands.  This is a convenience
     * constructor for taking multiple systems commands as string, every
     * commands supplied are converted to {@link SystemCommand} instances
     * internally.
     * @param name job name
     * @param commands array of individual commands to be executed
     * @see SystemCommandJob#SystemCommandJob(String, SystemCommand[])
     */
    @SuppressWarnings("nls")
    public SystemCommandJob(String name, String... commands) {
        super();
        if (commands == null) {
            throw new IllegalArgumentException(
                    "\"commands\" argument cannot be null.");
        }
        this.name = name;
        this.systemCommands = new SystemCommand[commands.length];
        for (int i = 0; i < commands.length; i++) {
            this.systemCommands[i] = new SystemCommand(commands[i]);
        }
    }
    
    /**
     * Creates a JEF job for executing system commands.
     * @param name job name
     * @param commands commands to be executed
     */
    public SystemCommandJob(
            String name, SystemCommand... commands) {
        super();
        this.name = name;
        this.systemCommands = ArrayUtils.clone(commands);
    }
    
    @Override
    public String getName() {
        return name;
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
            } catch (IOException | InterruptedException e) {
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
