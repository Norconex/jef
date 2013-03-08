package com.norconex.jef.mail;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.norconex.jef.JobException;
import com.norconex.jef.suite.ISuiteLifeCycleListener;
import com.norconex.jef.suite.JobSuite;

/**
 * Simple suite life-cycle listener notifying email recipients when a job suite
 * completes.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 */
public class SuiteCompletedMailNotifier
        extends AbstractMailNotifier implements ISuiteLifeCycleListener {

    /** Number of log lines to return. */
    private static final int LOG_LINE_QTY = 20;

    /**
     * @see AbstractMailNotifier#AbstractMailNotifier(String, String, String)
     */
    public SuiteCompletedMailNotifier(
            final String host, final String sender, final String recipient) {
        super(host, sender, recipient);
    }
    /**
     * @see AbstractMailNotifier#AbstractMailNotifier(String, String, String[])
     */
    public SuiteCompletedMailNotifier(
            final String host, final String sender, final String[] recipients) {
        super(host, sender, recipients);
    }

    @Override
    public void suiteAborted(final JobSuite suite) {
        //do nothing
    }
    @Override
    public void suiteStarted(final JobSuite suite) {
        //do nothing
    }
    @Override
    @SuppressWarnings("nls")
    public final void suiteCompleted(final JobSuite suite) {
        ResourceBundle bundle =
            ResourceBundle.getBundle(this.getClass().getName());
        String subject = MessageFormat.format(
                bundle.getString("subject"),
                new Object[]{suite.getNamespace()});
        try {
            String body = MessageFormat.format(
                    bundle.getString("body"),
                    new Object[] {
                        suite.getNamespace(),
                        Integer.toString(LOG_LINE_QTY),
                        getLogTail(suite, LOG_LINE_QTY)
                    });
            getMailer().send(getRecipients(), subject, body);
        } catch (AddressException e) {
            throw new JobException("Cannot send email.", e);
        } catch (MessagingException e) {
            throw new JobException("Cannot send email.", e);
        } catch (IOException e) {
            throw new JobException("Cannot send email.", e);
        }
    }
    @Override
    public void suiteTerminatedPrematuraly(final JobSuite suite) {
        // do nothing
    }
    @Override
    public void suiteStopped(JobSuite suite) {
        // do nothing
    }
    @Override
    public void suiteStopping(JobSuite suite) {
        // do nothing
    }
}
