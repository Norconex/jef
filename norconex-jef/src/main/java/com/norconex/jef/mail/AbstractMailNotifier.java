package com.norconex.jef.mail;

import java.io.IOException;

import com.norconex.commons.lang.io.IOUtil;
import com.norconex.jef.suite.JobSuite;

/**
 * Convenience base class for sending email notifications.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 */
public abstract class AbstractMailNotifier {

    /** Simple mailer. */
    private final SimpleMailer mailer;
    /** Email recipients. */
    private final String[] recipients;

    /**
     * Constructor.
     * @param host mail server host
     * @param sender email address of sender ("From" field)
     * @param recipient email address of recipient ("To" field)
     */
    public AbstractMailNotifier(
            final String host, final String sender, final String recipient) {
        super();
        this.mailer = new SimpleMailer(host, sender);
        this.recipients = new String[] {recipient};
    }
    /**
     * Constructor.
     * @param host mail server host
     * @param sender email address of sender ("From" field)
     * @param recipients email address of recipients ("To" field)
     */
    public AbstractMailNotifier(
            final String host, final String sender, final String[] recipients) {
        super();
        this.mailer = new SimpleMailer(host, sender);
        this.recipients = recipients;
    }

    /**
     * Gets the mailer for this notifier.
     * @return mailer
     */
    protected final SimpleMailer getMailer() {
        return mailer;
    }

    /**
     * Gets the email recipients for this notifier.
     * @return email recipients
     */
    protected final String[] getRecipients() {
        return recipients;
    }

    /**
     * Gets the last lines from a suite log.
     * @param suite the suite to extract the log from
     * @param lineQty the number of lines to retrieve
     * @return string representation of log last lines
     * @throws IOException problem getting lines
     */
    @SuppressWarnings("nls")
    protected final String getLogTail(final JobSuite suite, final int lineQty)
            throws IOException {
        StringBuffer logTail = new StringBuffer();
        String[] lines = IOUtil.tail(
                suite.getLogManager().getLog(suite.getNamespace()), lineQty);
        if (lines.length == 0) {
            logTail.append("*** No log found. ***\n");
        } else {
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                logTail.append(line);
                logTail.append('\n');
            }
        }
        return logTail.toString();
    }
}
