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

import com.norconex.commons.lang.io.IStreamListener;
import com.norconex.commons.lang.io.StreamGobbler;
import com.norconex.jef.JobRunner;

/**
 * Utility methods related to process execution.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 */
@SuppressWarnings("nls")
public final class ExecUtils {

    /** Identifier for standard output. */
    public static final String STDOUT = "STDOUT";
    /** Identifier for standard error. */
    public static final String STDERR = "STDERR";
    
    /**
     * Constructor.
     */
    private ExecUtils() {
        super();
    }

    /**
     * Watches a running process.  This method will wait until the process
     * as finished executing before returning with its exit value.
     * It ensures the process does not hang on some platform by making use
     * of the {@link StreamGobbler} to read its error and output stream.
     * @param process the process to watch
     * @return process exit value
     * @throws InterruptedException problem while waiting for process to finish
     */
    public static int watchProcess(Process process)
            throws InterruptedException {
        return watchProcess(
                process, new IStreamListener[] {}, new IStreamListener[] {});
    }
    /**
     * Watches a running process.  This method will wait until the process
     * as finished executing before returning with its exit value.
     * It ensures the process does not hang on some platform by making use
     * of the {@link StreamGobbler} to read its error and output stream.
     * The listener will be notified every time an error or output line
     * gets written by the process.
     * The listener line type will either be "STDERR" or "STDOUT".
     * @param process the process to watch
     * @param listener the listener to use for both "STDERR" or "STDOUT". 
     * @return process exit value
     * @throws InterruptedException problem while waiting for process to finish
     */
    public static int watchProcess(
            Process process,
            IStreamListener listener) throws InterruptedException {
        return watchProcess(process,
                new IStreamListener[] {listener},
                new IStreamListener[] {listener});
    }
    /**
     * Watches a running process.  This method will wait until the process
     * as finished executing before returning with its exit value.
     * It ensures the process does not hang on some platform by making use
     * of the {@link StreamGobbler} to read its error and output stream.
     * The listener will be notified every time an error or output line
     * gets written by the process.
     * The listener line type will either be "STDERR" or "STDOUT".
     * @param process the process to watch
     * @param listeners the listeners to use for both "STDERR" or "STDOUT". 
     * @return process exit value
     * @throws InterruptedException problem while waiting for process to finish
     */
    public static int watchProcess(
            Process process,
            IStreamListener[] listeners) throws InterruptedException {
        return watchProcess(process, listeners, listeners);
    }
    
    /**
     * Watches a running process.  This method will wait until the process
     * as finished executing before returning with its exit value.
     * It ensures the process does not hang on some platform by making use
     * of the {@link StreamGobbler} to read its error and output stream.
     * The listener will be notified every time an error or output line
     * gets written by the process.
     * The listener line type will either be "STDERR" or "STDOUT".
     * @param process the process to watch
     * @param outputListener the process output listener 
     * @param errorListener the process error listener 
     * @return process exit value
     * @throws InterruptedException problem while waiting for process to finish
     */
    public static int watchProcess(
            Process process,
            IStreamListener outputListener,
            IStreamListener errorListener) throws InterruptedException {
        return watchProcess(process,
                new IStreamListener[] {outputListener},
                new IStreamListener[] {errorListener});
    }
    /**
     * Watches a running process.  This method will wait until the process
     * as finished executing before returning with its exit value.
     * It ensures the process does not hang on some platform by making use
     * of the {@link StreamGobbler} to read its error and output stream.
     * The listeners will be notified every time an error or output line
     * gets written by the process.
     * The listener line type will either be "STDERR" or "STDOUT".
     * @param process the process to watch
     * @param outputListeners the process output listeners
     * @param errorListeners the process error listeners 
     * @return process exit value
     * @throws InterruptedException problem while waiting for process to finish
     */
    public static int watchProcess(
            Process process,
            IStreamListener[] outputListeners,
            IStreamListener[] errorListeners) throws InterruptedException {
        watchProcessOutput(process, outputListeners, errorListeners);
        return process.waitFor();
    }
    

    /**
     * Watches process output.  This method is the same as 
     * {@link #watchProcess(Process, IStreamListener, IStreamListener)}
     * with the exception of not waiting for the process to complete before
     * returning.
     * @param process the process on which to watch outputs
     * @param outputListener the process output listeners
     * @param errorListener the process error listeners 
     * @since 2.0
     */
    public static void watchProcessOutput(
            Process process,
            IStreamListener outputListener,
            IStreamListener errorListener) throws InterruptedException {
        watchProcessOutput(process,
                new IStreamListener[] {outputListener},
                new IStreamListener[] {errorListener});
    }
    
    
    /**
     * Watches process output.  This method is the same as 
     * {@link #watchProcess(Process, IStreamListener[], IStreamListener[])}
     * with the exception of not waiting for the process to complete before
     * returning.
     * @param process the process on which to watch outputs
     * @param outputListeners the process output listeners
     * @param errorListeners the process error listeners 
     * @since 2.0
     */
    public static void watchProcessOutput(
            Process process,
            IStreamListener[] outputListeners,
            IStreamListener[] errorListeners) {
        final String jobId = JobRunner.getCurrentJobId();
        // listen for output
        StreamGobbler output = 
                new StreamGobbler(process.getInputStream(), STDOUT) {
            @Override
            protected void beforeStreaming() {
                JobRunner.setCurrentJobId(jobId);
            }
        };
        output.addStreamListeners(outputListeners);
        output.start();

        // listen for error
        StreamGobbler error = 
            new StreamGobbler(process.getErrorStream(), STDERR) {
            @Override
            protected void beforeStreaming() {
                JobRunner.setCurrentJobId(jobId);
            }
        };
        error.addStreamListeners(errorListeners);
        error.start();
    }
    
}
