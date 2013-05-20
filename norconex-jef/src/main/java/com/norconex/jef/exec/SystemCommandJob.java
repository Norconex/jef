/* Copyright 2010-2013 Norconex Inc.
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
package com.norconex.jef.exec;

import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.norconex.jef.IJob;
import com.norconex.jef.IJobContext;
import com.norconex.jef.JobException;
import com.norconex.jef.progress.IJobStatus;
import com.norconex.jef.progress.JobProgress;
import com.norconex.jef.suite.JobSuite;

/**
 * JEF job for executing an arbitrary number of commands.  The job progress is
 * relative to the number of commands to be processed.  This is ideal
 * for submitting a batch of small commands where execution time is not
 * long enough to track individual command progresses.  For long-running
 * commands, you may want to create a custom job where you can keep track
 * of the command progress and translate that somehow into a job progress
 * (whenever possible).
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 * @since 1.1
 */
public class SystemCommandJob implements IJob {

    /** Logger. */
    private static final Logger LOG =
    		LogManager.getLogger(SystemCommandJob.class);

    /** Job unique id. */
    private final String id;
    /** Job description. */
    private final String desc;
    /** Commands to be executed. */
    private final SystemCommand[] systemCommands;

    
    /**
     * Creates a JEF job for executing system commands.  This is a convenience
     * constructor for taking multiple systems commands as string, every
     * commands supplied are converted to {@link SystemCommand} instances
     * internally.
     * @param id job id
     * @param desc job description
     * @param commands array of individual commands to be executed
     * @see SystemCommandJob#SystemCommandJob(String, String, SystemCommand[])
     */
    @SuppressWarnings("nls")
    public SystemCommandJob(String id, String desc, String[] commands) {
        super();
        if (commands == null) {
        	throw new IllegalArgumentException(
        			"\"commands\" argument cannot be null.");
        }
        this.id = id;
        this.desc = desc;
        this.systemCommands = new SystemCommand[commands.length];
        for (int i = 0; i < commands.length; i++) {
			this.systemCommands[i] = new SystemCommand(commands[i]);
		}
    }
    /**
     * Creates a JEF job for executing a system command.  This is a convenience
     * constructor for taking a command as a string, the
     * command supplied is converted to a {@link SystemCommand} instance
     * internally.
     * @param id job id
     * @param desc job description
     * @param command commands to be executed
     */
    public SystemCommandJob(String id, String desc, String command) {
    	this(id, desc, new String[] {command});
    }
    
    /**
     * Creates a JEF job for executing system commands.
     * @param id job id
     * @param desc job description
     * @param commands commands to be executed
     */
    public SystemCommandJob(String id, String desc, SystemCommand[] commands) {
        super();
        this.id = id;
        this.desc = desc;
        this.systemCommands = ArrayUtils.clone(commands);
    }
    /**
     * Creates a JEF job for executing a system command.
     * @param id job id
     * @param desc job description
     * @param command commands to be executed
     */
    public SystemCommandJob(String id, String desc, SystemCommand command) {
        super();
        this.id = id;
        this.desc = desc;
        this.systemCommands = new SystemCommand[] { command };
    }
    
    @Override
    public String getId() {
        return id;
    }
    @Override
    public IJobContext createJobContext() {
        return new IJobContext() {
            private static final long serialVersionUID = -779499724571527547L;
            @Override
            public long getProgressMinimum() {
                return 0;
            }
            @Override
            public long getProgressMaximum() {
                return systemCommands.length;
            }
            @Override
            public String getDescription() {
                return desc;
            }
        };
    }
    
    @SuppressWarnings("nls")
    @Override
    public void execute(JobProgress progress, JobSuite suite) {
        for (int i = (int) progress.getProgress();
        		i < systemCommands.length; i++) {
            SystemCommand systemCommand = systemCommands[i];
            if (LOG.isDebugEnabled()) {
                LOG.debug("Executing command: " + systemCommand);
            }
            progress.setNote("Executing: " + systemCommand);
            try {
                systemCommand.execute();
                progress.incrementProgress(1);
            } catch (IOException e) {
                throw new JobException("Cannot execute command: "
                		+ systemCommand, e);
            } catch (InterruptedException e) {
                throw new JobException("Cannot execute command: "
                		+ systemCommand, e);
            }
        }
        progress.setNote("Done.");
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
    public void stop(IJobStatus progress, JobSuite suite) {
        for (SystemCommand command : systemCommands) {
            command.abort();
        }
    }
}
