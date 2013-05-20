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
package com.norconex.jef.mail;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.norconex.jef.JobException;
import com.norconex.jef.error.IErrorEvent;
import com.norconex.jef.error.IErrorHandler;
import com.norconex.jef.suite.JobSuite;

/**
 * Simple error handler notifying email recipients when exceptions occur.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 */
public class ErrorMailNotifier
        extends AbstractMailNotifier implements IErrorHandler {

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

    @SuppressWarnings("nls")
    @Override
    public final void handleError(final IErrorEvent event) {
        JobSuite suite = event.getJobSuite();
        ResourceBundle bundle =
                ResourceBundle.getBundle(this.getClass().getName());
        String subject = MessageFormat.format(
                bundle.getString("subject"),
                new Object[]{suite.getNamespace()});
        String jobId = "N/A";
        String percent = "N/A";

        if (event.getProgress() != null) {
            jobId = event.getProgress().getJobId();
            percent = Double.toString(
                    event.getProgress().getCompletionRatio() * PROGRESS_RATIO);
        }

        try {
            String body = MessageFormat.format(
                    bundle.getString("body"),
                    new Object[] {
                        jobId,
                        suite.getNamespace(),
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
