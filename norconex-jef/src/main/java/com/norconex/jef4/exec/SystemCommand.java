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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.norconex.commons.lang.io.IStreamListener;
import com.norconex.jef4.job.group.AsyncJobGroup;

/**
 * Represents a program to be executed by the underlying system
 * (on the "command line").  This class attempts to be system-independent,
 * which means given an executable path should be sufficient to run
 * programs on any systems (e.g. it handles prefixing an executable with OS
 * specific commands as well as preventing process hanging on some OS when
 * there is nowhere to display the output).  
 * 
 * @author Pascal Essiembre
 * @since 1.1
 */
@SuppressWarnings("nls")
public class SystemCommand {

    /** Logger. */
    private static final Logger LOG = LogManager.getLogger(SystemCommand.class);

    private static final String[] EMPTY_STRINGS = new String[] {};
    private static final String[] CMD_PREFIXES_WIN_LEGACY = 
    		new String[] { "command.com", "/C" };
    private static final String[] CMD_PREFIXES_WIN_CURRENT = 
			new String[] { "cmd.exe", "/C" };
    private static final IStreamListener[] EMPTY_LISTENERS =
    		new IStreamListener[] {};
    
    /** The command to run. */
    private final String[] command;
    /** The command description. */
    private String desc;
    /** Whether to inherit the working directory. */
    private final File workdir;

    /** Process STDERR listeners. */
    private final List<IStreamListener> errorListeners =
            Collections.synchronizedList(new ArrayList<IStreamListener>());
    /** Process STDOUT listeners. */
    private final List<IStreamListener> outputListeners =
            Collections.synchronizedList(new ArrayList<IStreamListener>());

    private Process process;
    
    /**
     * Creates a command for which the execution will be in the working
     * directory of the current process.  If more than one command values
     * are passed, the first element of the array
     * is the command and subsequent elements are arguments.
     * @param command the command to run
     */
    public SystemCommand(String... command) {
    	this(null, command);
    }
    
    /**
     * Creates a command. If more than one command values
     * are passed, the first element of the array
     * is the command and subsequent elements are arguments.
     * @param command the command to run
     * @param workdir specifies a working directory (default inherits
     *        the working directory of the current process.
     */
    public SystemCommand(File workdir, String... command) {
        super();
        this.command = command;
        this.workdir = workdir;
    }
    
    
    /**
     * Gets the command to be run.
     * @return the command
     */
    public String[] getCommand() {
        return command;
    }

    /**
     * Gets the description for this command.
     * @return command description
     */
    public String getDescription() {
        return desc;
    }
    /**
     * Sets the description for this command.
     * @param description command description
     */
    public void setDescription(String description) {
        this.desc = description;
    }
    

    /**
     * Gets the command working directory.
     * @return command working directory.
     */
    public File getWorkdir() {
    	return workdir;
    }

    /**
     * Adds an error (STDERR) listener to this system command.
     * @param listener command error listener
     */
    public void addErrorListener(
            final IStreamListener listener) {
        synchronized (errorListeners) {
        	errorListeners.add(0, listener);
        }
    }
    /**
     * Removes an error (STDERR) listener.
     * @param listener command error listener
     */
    public void removeErrorListener(
            final IStreamListener listener) {
        synchronized (errorListeners) {
        	errorListeners.remove(listener);
        }
    }
    /**
     * Adds an output (STDOUT) listener to this system command.
     * @param listener command output listener
     */
    public void addOutputListener(
            final IStreamListener listener) {
        synchronized (outputListeners) {
        	outputListeners.add(0, listener);
        }
    }
    /**
     * Removes an output (STDOUT) listener.
     * @param listener command output listener
     */
    public void removeOutputListener(
            final IStreamListener listener) {
        synchronized (outputListeners) {
        	outputListeners.remove(listener);
        }
    }

    /**
     * Returns whether the command is currently running.
     * @return <code>true</code> if running
     */
    public boolean isRunning() {
    	if (process == null) {
    		return false;
    	}
    	try {
        	process.exitValue();
        	return false;
    	} catch (IllegalThreadStateException e) {
    		return true;
    	}
    }

    /**
     * Aborts the running command.  If the command is not currently running,
     * aborting it will have no effect.
     */
    public void abort() {
        if (process != null) {
            process.destroy();
        }
    }

    /**
     * Executes the given command and returns only when the underlying process
     * stopped running.  
     * @return process exit value
     * @throws InterruptedException problem executing command
     * @throws IOException problem executing command
     */
    public int execute() throws InterruptedException, IOException {
        return execute(false);
    }
    
    /**
     * Executes the given system command.  When run in the background,
     * this method will return quickly, with a status code of 0.
     * The status will not reflect the sub-process termination status.  
     * When NOT run in the background, this method waits and returns 
     * only when the underlying process stopped running.  
     * Alternatively, to run a command asynchronously, you can wrap it in 
     * its own thread (e.g. wrapping it in a {@link AsyncJobGroup}).
     * @param runInBackground <code>true</code> to runs the system command in 
     *         background.
     * @return process exit value
     * @throws InterruptedException problem executing command
     * @throws IOException problem executing command
     * @since 2.0
     */
    public int execute(boolean runInBackground) 
            throws InterruptedException, IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Executing command: " + toString());
        }
        if (isRunning()) {
        	throw new IllegalStateException("Command is already running: "
        			+ toString());
        }
        String[] prefixes = getOSCommandPrefixes();
        String[] fullCommand = ArrayUtils.addAll(prefixes, command);
        process = Runtime.getRuntime().exec(fullCommand, null, workdir);
        int exitValue = 0;
        if (runInBackground) {
            ExecUtils.watchProcessOutput(
                    process, 
                    outputListeners.toArray(EMPTY_LISTENERS),
                    errorListeners.toArray(EMPTY_LISTENERS));
            try {
                // Check in case the process terminated abruptly.
                exitValue = process.exitValue();
            } catch (IllegalThreadStateException e) {
                // Do nothing
            }
        } else {
            exitValue = ExecUtils.watchProcess(
                    process, 
                    outputListeners.toArray(EMPTY_LISTENERS),
                    errorListeners.toArray(EMPTY_LISTENERS));
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Command returned with exit value " + exitValue
            		+ ": " + toString());
        }
        if (exitValue != 0) {
            LOG.warn("Command returned exit value " + process.exitValue()
                    + ": " + toString());
        }
        process = null;
        return exitValue;
    }

    /**
     * Returns the command to be executed.
     */
    @Override
    public String toString() {
    	String cmd = "";
    	for (int i = 0; i < command.length; i++) {
			String element = command[i];
			cmd += element + " ";
		}
        return cmd.trim();
    }

    private String[] getOSCommandPrefixes() {
    	//TODO consider using "nice" on *nix systems.
    	if (SystemUtils.OS_NAME == null) {
    		return EMPTY_STRINGS;
    	}
    	if (SystemUtils.IS_OS_WINDOWS) {
    		if (SystemUtils.IS_OS_WINDOWS_95
    				|| SystemUtils.IS_OS_WINDOWS_98
    				|| SystemUtils.IS_OS_WINDOWS_ME) {
    			return CMD_PREFIXES_WIN_LEGACY;
    		}
            // NT, 2000, XP and up
			return CMD_PREFIXES_WIN_CURRENT;
    	}
    	return EMPTY_STRINGS;
    }
}