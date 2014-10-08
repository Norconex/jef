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
package com.norconex.jef4.suite;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.norconex.commons.lang.Sleeper;
import com.norconex.commons.lang.file.FileUtil;
import com.norconex.jef4.JEFException;
import com.norconex.jef4.job.IJob;
import com.norconex.jef4.job.IJobLifeCycleListener;
import com.norconex.jef4.job.IJobVisitor;
import com.norconex.jef4.status.IJobStatus;
import com.norconex.jef4.status.JobState;
import com.norconex.jef4.status.MutableJobStatus;

/**
 * Listens for STOP requests using a stop file.  The stop file
 * file name matches the suite namespace, plus the ".stop" extension.  
 * The directory where to locate the file depends on the constructor invoked.
 *
 * @author Pascal Essiembre
 * @since 2.0
 */
@SuppressWarnings("nls")
public class StopRequestMonitor extends Thread {

    /** Logger. */
    private static final Logger LOG =
            LogManager.getLogger(StopRequestMonitor.class);
    private static final int STOP_WAIT_DELAY = 3;
    
    private final File stopFile;
    private final JobSuite suite;
    private boolean monitoring = false;
    

    public StopRequestMonitor(JobSuite suite) {
        this.stopFile = suite.getSuiteStopFile();
        this.suite = suite;
    }


    @Override
    public void run() {
        monitoring = true;
        while(monitoring) {
            if (stopFile.exists()) {
                stopMonitoring();
                stopSuite();
            }
            Sleeper.sleepSeconds(1);
        }
    }
    
    public synchronized void stopMonitoring() {
        monitoring = false;
        if (stopFile.exists()) {
            try {
                FileUtil.delete(stopFile);
            } catch (IOException e) {
                throw new JEFException(
                        "Cannot delete stop file: " + stopFile, e);
            }
        }
    }

 
    private void stopSuite() {
        monitoring = false;
        LOG.info("STOP request received.");
        
        /// Notify Suite Life Cycle listeners
        for (ISuiteLifeCycleListener l : suite.getSuiteLifeCycleListeners()) {
            l.suiteStopping(suite);
        }
        
        /// Notify Job Life Cycle listeners and stop them
        suite.accept(new IJobVisitor() {
            @Override
            public void visitJob(final IJob job, final IJobStatus jobStatus) {
                for (IJobLifeCycleListener l : 
                        suite.getJobLifeCycleListeners()) {
                    l.jobStopping(jobStatus);
                }
                new Thread(){
                    @Override
                    public void run() {
                        stopJob(job, jobStatus);
                    }
                }.start();                
            }
        });
    }
    private void stopJob(final IJob job, final IJobStatus status) {
        ((MutableJobStatus) status).setStopRequested(true);
        job.stop(status, suite);
        while (status.getState() == JobState.RUNNING) {
            Sleeper.sleepSeconds(STOP_WAIT_DELAY);
        }
        if (status.getState() == JobState.STOPPED) {
            for (IJobLifeCycleListener l : 
                    suite.getJobLifeCycleListeners()) {
                l.jobStopped(status);
            }
            if (job.getId().equals(suite.getRootJob().getId())) {
                for (ISuiteLifeCycleListener l : 
                        suite.getSuiteLifeCycleListeners()) {
                    l.suiteStopped(suite);
                }
            }
        }
    }

}
