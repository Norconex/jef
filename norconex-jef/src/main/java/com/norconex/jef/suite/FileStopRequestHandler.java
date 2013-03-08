package com.norconex.jef.suite;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.norconex.jef.JobException;
import com.norconex.jef.io.FileSystemUtils;

/**
 * Listens for STOP requests using a stop file.  The stop file
 * file name matches the suite namespace, plus the ".stop" extension.  
 * The directory where to locate the file depends on the constructor invoked.
 *
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 * @since 2.0
 */
@SuppressWarnings("nls")
public class FileStopRequestHandler
        implements IJobSuiteStopRequestHandler {

    private static final long serialVersionUID = -9221330761347440944L;

    /** Logger. */
    private static final Logger LOG =
            LogManager.getLogger(FileStopRequestHandler.class);

    /** Directory where to store the file. */
    private final String jobdirProgress;
    private final File stopFile;
    private boolean listening = false;
    

    /**
     * Creates a file-based job stop request advisor.  If a file bearing
     * the job suite name (i.e. namespace) is found, with the extension
     * ".stop", a stop request event will be send to the listener and the stop
     * file will be deleted.  The mechanism used
     * to find the base directory where to store the stop file is described 
     * in the {@link FileSystemUtils#getDefaultWorkDir()} method.
     */
    public FileStopRequestHandler(String namespace) {
        this(namespace, FileSystemUtils.getDefaultWorkDir());
    }
    /**
     * Creates a file-based job stop request advisor storing files in the given
     * job directory.
     * @param jobDir the base directory where to store/read the stop file
     */
    public FileStopRequestHandler(
            String namespace, final String jobDir) {
        this.jobdirProgress = jobDir + "/latest";
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
            public void run() {
                try {
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
                        Thread.sleep(1 * 1000);
                    }
                } catch (InterruptedException e) {
                    throw new JobException("Cannot sleep.", e);
                }
            };
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
