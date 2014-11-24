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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.norconex.jef4.job.IJobErrorListener;
import com.norconex.jef4.job.JobErrorEvent;
import com.norconex.jef4.job.JobException;
import com.norconex.jef4.suite.JobSuite;

/**
 * Simple error handler notifying email recipients when exceptions occur.
 * @author Pascal Essiembre
 */
public class ErrorMailNotifier
        extends AbstractMailNotifier implements IJobErrorListener {

    /** Number of log lines to return. */
    private static final int LOG_LINE_QTY = 50;
    /** Progress ratio. */
    private static final int PROGRESS_RATIO = 100;

    /**
     * @see AbstractMailNotifier#AbstractMailNotifier(String, String, String)
     */
    public ErrorMailNotifier(
            final String host, final String sender, final String recipient) {
        super(host, sender, recipient);
    }
    /**
     * @see AbstractMailNotifier#AbstractMailNotifier(String, String, String[])
     */
    public ErrorMailNotifier(
            final String host, final String sender, final String[] recipients) {
        super(host, sender, recipients);
    }

    @Override
    public void jobError(JobErrorEvent event) {
        JobSuite suite = event.getSuite();
        ResourceBundle bundle =
                ResourceBundle.getBundle(this.getClass().getName());
        String subject = MessageFormat.format(
                bundle.getString("subject"),
                new Object[]{suite.getId()});
        String jobId = "N/A";
        String percent = "N/A";

        if (event.getStatus() != null) {
            jobId = event.getStatus().getJobId();
            percent = Double.toString(
                    event.getStatus().getProgress() * PROGRESS_RATIO);
        }

        try {
            String body = MessageFormat.format(
                    bundle.getString("body"),
                    new Object[] {
                        jobId,
                        suite.getId(),
                        percent,
                        getStackTrace(event.getException()),
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

    protected final String getStackTrace(final Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
