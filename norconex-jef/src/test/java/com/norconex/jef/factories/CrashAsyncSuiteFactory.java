package com.norconex.jef.factories;

import static org.junit.Assert.assertFalse;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.norconex.jef.AsyncJobGroup;
import com.norconex.jef.IJob;
import com.norconex.jef.JobException;
import com.norconex.jef.JobRunner;
import com.norconex.jef.jobs.CrashingJob;
import com.norconex.jef.jobs.SleepyJob;
import com.norconex.jef.mail.ErrorMailNotifier;
import com.norconex.jef.suite.IJobSuiteFactory;
import com.norconex.jef.suite.JobSuite;

public class CrashAsyncSuiteFactory implements IJobSuiteFactory {

    private String smtpServer;
    private String fromEmail;
    private String toEmail;

    public String getSmtpServer() {
        return smtpServer;
    }
    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }
    public String getFromEmail() {
        return fromEmail;
    }
    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }
    public String getToEmail() {
        return toEmail;
    }
    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }
    
    public CrashAsyncSuiteFactory() {
        super();
    }

    @Override
    public JobSuite createJobSuite() {
        
        IJob asyncListGroup = new AsyncJobGroup(
            "test.crash.async", new IJob[] {
                    new SleepyJob(10, 5),
                    new CrashingJob(15, IllegalArgumentException.class),
//                    new CrashingJob(15, IllegalAccessError.class),
                    new SleepyJob(30, 10),
            }
        );
                
        JobSuite suite = new JobSuite(asyncListGroup);
        
        if (StringUtils.isNotBlank(smtpServer)
                && StringUtils.isNotBlank(fromEmail)
                && StringUtils.isNotBlank(toEmail)) {
            suite.addErrorHandler(new ErrorMailNotifier(
                    smtpServer,fromEmail, toEmail));
        }
        return suite;
    }
    @Test
    public void testJobSuite() throws JobException {
        JobSuite suite = new CrashAsyncSuiteFactory().createJobSuite();
        JobRunner runner = new JobRunner();
        boolean success = runner.runSuite(suite, true);
        assertFalse("Suite was expected to crash, but did not.", success);
    }
}
