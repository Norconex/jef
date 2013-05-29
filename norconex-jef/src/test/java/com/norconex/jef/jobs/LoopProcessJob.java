/**
 * 
 */
package com.norconex.jef.jobs;

import java.io.IOException;

import com.norconex.jef.IJob;
import com.norconex.jef.IJobContext;
import com.norconex.jef.JobContext;
import com.norconex.jef.JobException;
import com.norconex.jef.exec.ExecUtils;
import com.norconex.jef.progress.IJobStatus;
import com.norconex.jef.progress.JobProgress;
import com.norconex.jef.suite.JobSuite;
import com.norconex.jef.jobs.watcher.LoopProcessKiller;

/**
 * Job for listing the files in a directory, including subdirectories.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 */
public class LoopProcessJob implements IJob {

    private final String id;
    private final String cmd;
    
    /**
     * Constructor.
     */
    public LoopProcessJob(String id, String cmd) {
        super();
        this.id = id;
        this.cmd = cmd;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getDescription() {
        return "Looping process: " + cmd + ".";
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
//            watcher.addProcessListener(listener);
            Process process = Runtime.getRuntime().exec(cmd);
            LoopProcessKiller loopKiller = new LoopProcessKiller(process);
            int exitValue = ExecUtils.watchProcess(
                    process, loopKiller);

            System.out.println("Exit value: " + exitValue);
            System.out.println("Is killed: " + loopKiller.isKilled());
            
            progress.setProgress(1);
            progress.setNote("Done printing tree.");
            
            
        } catch (IOException e) {
            throw new JobException(e);
        } catch (InterruptedException e) {
            throw new JobException(e);
        }
    }

	@Override
	public IJobContext createJobContext() {
		return new JobContext(
		        getDescription(), getProgressMinimum(), getProgressMaximum());
	}

	@Override
	public void stop(IJobStatus progress, JobSuite suite) {
	    // can't be stopped
	}
}
