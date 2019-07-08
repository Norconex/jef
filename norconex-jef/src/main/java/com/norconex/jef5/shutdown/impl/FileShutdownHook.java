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
package com.norconex.jef5.shutdown.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.norconex.commons.lang.Sleeper;
import com.norconex.jef5.JefException;
import com.norconex.jef5.event.JefEvent;
import com.norconex.jef5.job.IJob;
import com.norconex.jef5.shutdown.IShutdownHook;
import com.norconex.jef5.shutdown.ShutdownException;
import com.norconex.jef5.status.JobState;
import com.norconex.jef5.status.JobStatus;
import com.norconex.jef5.status.JobSuiteStatus;
import com.norconex.jef5.suite.JobSuite;

/**
 * Listens for STOP requests using a stop file.  The stop file
 * file has the same path of the suite index file location, with ".stop"
 * extension instead.
 *
 * @author Pascal Essiembre
 */
public class FileShutdownHook implements IShutdownHook {

    private static final Logger LOG =
            LoggerFactory.getLogger(FileShutdownHook.class);

    private static final int STOP_WAIT_DELAY = 3;

    private boolean monitoring = false;
    private JobSuite suite;


    @Override
    public void setup(final JobSuite suite) {
        this.suite = suite;
        final Path stopFile = getStopFile(suite.getStatusIndex());
        new Thread(() -> {
            monitoring = true;
            while(monitoring) {
                if (stopFile.toFile().exists()) {
                    stopMonitoring(stopFile);
                    stopSuite();
                }
                Sleeper.sleepNanos(500);
            }
        }, "ShutdownHook Thread").start();
    }

    @Override
    public void destroy() {
        monitoring = false;
        Path stopFile = getStopFile(suite.getStatusIndex());
        if (stopFile.toFile().exists()) {
            try {
                Files.delete(stopFile);
            } catch (IOException e) {
                throw new JefException("Cannot delete stop file: "
                        + stopFile.toAbsolutePath(), e);
            }
        }
        suite = null;
    }

    @Override
    public boolean shutdown(Path indexFile) throws ShutdownException {
        if (indexFile == null || !indexFile.toFile().isFile()) {
            throw new ShutdownException("Invalid index file: " + indexFile);
        }

        Path stopFile = getStopFile(indexFile);
        if (stopFile.toFile().exists()) {
            throw new ShutdownException("Stop already requested. Stop file: "
                    + stopFile.toAbsolutePath());
        }

        try {
            if (!JobSuiteStatus.getInstance(
                    indexFile).getRootStatus().isRunning()) {
                LOG.info("The job suite is not running.");
                return false;
            }
        } catch (IOException e) {
            throw new ShutdownException(
                    "Could not obtain job suite satus from: "
                            + stopFile.toAbsolutePath(), e);
        }
        try {
            Files.createFile(stopFile);
        } catch (IOException e) {
            throw new ShutdownException("Could not create stop file: "
                    + stopFile.toAbsolutePath(), e);
        }
        return true;
    }

    private synchronized void stopMonitoring(Path stopFile) {
        monitoring = false;
        try {
            Files.deleteIfExists(stopFile);
        } catch (IOException e) {
            throw new JefException("Cannot delete stop file: " + stopFile, e);
        }
    }
    private void stopSuite() {
        monitoring = false;
        LOG.info("STOP request received.");

        // Notify Suite Life Cycle listeners
        suite.getEventManager().fire(
                JefEvent.create(JefEvent.SUITE_STOPPING, null, suite));

        // Notify Job Life Cycle listeners and stop them
        suite.accept((final IJob job, final JobStatus jobStatus) -> {
            suite.getEventManager().fire(
                    JefEvent.create(JefEvent.JOB_STOPPING, jobStatus, suite));
            new Thread(() -> stopJob(job, jobStatus), "Stop Thread").start();
        });
    }
    private void stopJob(final IJob job, final JobStatus status) {
        status.setStopRequested(true);
        job.stop(status, suite);
        while (status.isRunning()) {
            Sleeper.sleepSeconds(STOP_WAIT_DELAY);
        }
        if (status.getState() == JobState.STOPPED) {
            suite.getEventManager().fire(
                    JefEvent.create(JefEvent.JOB_STOPPED, status, suite));
            if (job.getId().equals(suite.getRootJob().getId())) {
                suite.getEventManager().fire(
                        JefEvent.create(JefEvent.SUITE_STOPPED, null, suite));
            }
        }
    }

    private Path getStopFile(Path indexFile) {
        return Paths.get(StringUtils.removeEnd(
                indexFile.toString(), ".index") + ".stop");
    }
}
