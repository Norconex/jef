package com.norconex.jef.factories;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.norconex.jef.AsyncJobGroup;
import com.norconex.jef.IJob;
import com.norconex.jef.JobException;
import com.norconex.jef.JobRunner;
import com.norconex.jef.jobs.SleepyJob;
import com.norconex.jef.mail.SuiteCompletedMailNotifier;
import com.norconex.jef.suite.IJobSuiteFactory;
import com.norconex.jef.suite.JobSuite;

public class AsyncSuiteFactory implements IJobSuiteFactory {

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
    public AsyncSuiteFactory() {
        super();
    }

    @Override
    public JobSuite createJobSuite() {
        IJob asyncListGroup = new AsyncJobGroup(
            "test.async", new IJob[] {
                    new SleepyJob(10, 5),
                    new SleepyJob(15, 10),
            }
        );
        JobSuite context = new JobSuite(asyncListGroup);  
        
        if (StringUtils.isNotBlank(smtpServer)
                && StringUtils.isNotBlank(fromEmail)
                && StringUtils.isNotBlank(toEmail)) {
            context.addSuiteLifeCycleListener(new SuiteCompletedMailNotifier(
                    smtpServer,fromEmail, toEmail));
        }
        
        return context;
    }

    
    @Test
    public void testJobSuite() throws JobException {
        JobSuite suite = new AsyncSuiteFactory().createJobSuite();
        JobRunner runner = new JobRunner();
        boolean success = runner.runSuite(suite, true);
        assertTrue("Suite did not complete properly: "
                + suite.getSuiteStatus(), success);
    }
}
