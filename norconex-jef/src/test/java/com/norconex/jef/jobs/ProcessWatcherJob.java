/**
 * 
 */
package com.norconex.jef.jobs;

import java.io.File;
import java.io.IOException;

import com.norconex.jef.IJob;
import com.norconex.jef.IJobContext;
import com.norconex.jef.JobContext;
import com.norconex.jef.JobException;
import com.norconex.jef.exec.ExecUtils;
import com.norconex.jef.jobs.watcher.ProcessListener;
import com.norconex.jef.progress.IJobStatus;
import com.norconex.jef.progress.JobProgress;
import com.norconex.jef.suite.JobSuite;

/**
 * Job for listing the files in a directory, including subdirectories.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 */
public class ProcessWatcherJob implements IJob {

    private final String id;
    private final File dir;
    

    public ProcessWatcherJob(String id, String dir) {
        super();
        this.id = id;
        File directory = new File(dir);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException(
                    "Must specify a valid directory.");
        }
        this.dir = directory;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getDescription() {
        return "Print tree in " + dir + ".";
    }

    public long getProgressMinimum() {
        return 0;
    }

    public long getProgressMaximum() {
        return 1;
    }

    @Override
    public void execute(JobProgress progress, JobSuite context)
            throws JobException {
        
        try {
//            ProcessWatcher watcher = new ProcessWatcher();
            ProcessListener listener = new ProcessListener();
//            watcher.addProcessListener(listener);
            Process process = Runtime.getRuntime().exec(
                    new String[] {"cmd.exe", "/C", "dir", dir.toString()});
            int exitValue = ExecUtils.watchProcess(
                    process, listener, listener);
            System.out.println("Exit value: " + exitValue);
            
            progress.setProgress(1);
            progress.setNote("Done printing tree.");
            
            String[] dirs = listener.getDirs();
            for (int i = 0; i < dirs.length; i++) {
                System.out.println("FOUND DIR:" + dirs[i]);
            }
            
        } catch (IOException e) {
            throw new JobException(e);
        } catch (InterruptedException e) {
            throw new JobException(e);
        }
    }

	@Override
	public IJobContext createJobContext() {
		return new JobContext(
		        getDescription(),
		        getProgressMinimum(),
		        getProgressMaximum());
	}

	@Override
	public void stop(IJobStatus progress, JobSuite suite) {
		// Unstoppable
	}
}
