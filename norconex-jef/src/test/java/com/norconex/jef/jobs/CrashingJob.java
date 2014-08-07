/**
 * 
 */
package com.norconex.jef.jobs;

import java.lang.reflect.Constructor;

import com.norconex.jef.IJob;
import com.norconex.jef.IJobContext;
import com.norconex.jef.JobContext;
import com.norconex.jef.JobException;
import com.norconex.jef.progress.IJobStatus;
import com.norconex.jef.progress.JobProgress;
import com.norconex.jef.suite.JobSuite;

/**
 * This job crashes, throwing a <code>JobException</code> after the number
 * of specified seconds.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 *
 */
@SuppressWarnings("rawtypes")
public class CrashingJob implements IJob {

    private int seconds;
    private Class exceptionClass;

    public CrashingJob(int seconds, Class exceptionClass) {
        super();
        this.seconds = seconds;
        this.exceptionClass = exceptionClass;
    }

    @Override
    public String getId() {
        return "job.crash." + seconds + "." + exceptionClass.toString();
    }

    public String getDescription() {
        return "This job throws a JobException after " + seconds + " seconds.";
    }

    public long getProgressMinimum() {
        return 0;
    }

    public long getProgressMaximum() {
        return 100;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(JobProgress progress, JobSuite context)
            throws JobException {
        
        try {
            Thread.sleep(seconds * 1000);
            //Throw exception.
            Constructor constructor =
                    exceptionClass.getConstructor(new Class[] {String.class});
            throw (Throwable) constructor.newInstance(new Object[]{
                    "I am job " + getId() + " and I just felt "
                  + "like throwing an exception of type "
                  + exceptionClass.toString()});
            
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
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
		// TODO Unstoppable
	}

}
