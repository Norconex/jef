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

    /**
     * @see com.norconex.jef.error.IErrorHandler#handleError(IErrorEvent)
     */
    @SuppressWarnings("nls")
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
