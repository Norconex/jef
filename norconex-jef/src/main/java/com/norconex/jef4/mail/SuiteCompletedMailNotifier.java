/* Copyright 2010-2014 Norconex Inc.
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
package com.norconex.jef4.mail;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.norconex.jef4.job.JobException;
import com.norconex.jef4.suite.ISuiteLifeCycleListener;
import com.norconex.jef4.suite.JobSuite;


/**
 * Simple suite life-cycle listener notifying email recipients when a job suite
 * completes.
 * @author Pascal Essiembre
 */
public class SuiteCompletedMailNotifier
        extends AbstractMailNotifier implements ISuiteLifeCycleListener {

    /** Number of log lines to return. */
    private static final int LOG_LINE_QTY = 20;

    /**
     * @see AbstractMailNotifier#AbstractMailNotifier(String, String, String)
     * @param host mail server host
     * @param sender email address of sender ("From" field)
     * @param recipient email address of recipient ("To" field)
     */
    public SuiteCompletedMailNotifier(
            final String host, final String sender, final String recipient) {
        super(host, sender, recipient);
    }
    /**
     * @see AbstractMailNotifier#AbstractMailNotifier(String, String, String[])
     * @param host mail server host
     * @param sender email address of sender ("From" field)
     * @param recipients email address of recipients ("To" field)
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
                new Object[]{suite.getId()});
        try {
            String body = MessageFormat.format(
                    bundle.getString("body"),
                    new Object[] {
                        suite.getId(),
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
