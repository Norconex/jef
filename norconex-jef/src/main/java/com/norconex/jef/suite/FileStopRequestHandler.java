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
package com.norconex.jef.suite;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.norconex.commons.lang.Sleeper;
import com.norconex.jef.JobException;

/**
 * Listens for STOP requests using a stop file.  The stop file
 * file name matches the suite namespace, plus the ".stop" extension.  
 * The directory where to locate the file depends on the constructor invoked.
 *
 * @author Pascal Essiembre
 * @since 2.0
 */
@SuppressWarnings("nls")
public class FileStopRequestHandler
        implements IJobSuiteStopRequestHandler {

    private static final long serialVersionUID = -9221330761347440944L;

    /** Logger. */
    private static final Logger LOG =
            LogManager.getLogger(FileStopRequestHandler.class);

    private final File stopFile;
    private boolean listening = false;
    

    /**
     * Creates a file-based job stop request advisor storing files in the given
     * job directory.
     * @param namespace suite namespace
     * @param jobDir the base directory where to store/read the stop file
     */
    public FileStopRequestHandler(
            String namespace, final String jobDir) {
        String jobdirProgress = jobDir + "/latest";
        File dir = new File(jobdirProgress);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.stopFile = new File(jobdirProgress + "/" + namespace + ".stop");
    }


    @Override
    public void startListening(
            final ISuiteStopRequestListener listener) {
        listening = true;
        new Thread() {
            @Override
            public void run() {
                while(listening) {
                    boolean exists = stopFile.exists();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Stop file exists:" + exists + " ("
                                + stopFile.getAbsolutePath() + ")");
                    }
                    if (exists) {
                        listening = false;
                        LOG.info("STOP request received.");
                        listener.stopRequestReceived();
                        stopFile.delete();
                    }
                    Sleeper.sleepSeconds(1);
                }
            }
        }.start();
    }

    @Override
    public void stopListening() {
        listening = false;
    }
    
    /**
     * Fires a stop request.  It will write a stop file where it normally
     * expects it.  This method conveniently create a stop file for a running
     * instance of a suite to pick it up.
     */
    public void fireStopRequest() {
        try {
            stopFile.createNewFile();
        } catch (IOException e) {
            throw new JobException("Cannot fire stop request." , e);
        }
    }
}
