package com.norconex.jef.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Convinience class for sending simple emails.  It does not cover most
 * advanced needs (HTML images, attachments, etc).
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 */
@SuppressWarnings("nls")
public class SimpleMailer {

    /** Email sender ("From" field). */
    private final String sender;
    /** System properties. */
    private final Properties props;
    /** Email content type. */
    private final String contentType;
    /** Email recipients ("To field). */
    private final String[] recipients;

    /**
     * Constructor.
     * @param host mail server host
     * @param sender email address of sender ("From" field)
     */
    public SimpleMailer(final String host, final String sender) {
        this(host, sender, "text/plain");
    }
    /**
     * Constructor.
     * @param host mail server host
     * @param sender email address of sender ("From" field)
     * @param recipients email address of recipients ("To" field)
     */
    public SimpleMailer(final String host, final String sender,
            final String[] recipients) {
        this(host, sender, recipients, "text/plain");
    }
    /**
     * Constructor.
     * @param host mail server host
     * @param sender email address of sender ("From" field)
     * @param contentType content type of the email message
     */
    public SimpleMailer(
            final String host, final String sender, final String contentType) {
        this(host, sender, null, contentType);
    }
    /**
     * Constructor.
     * @param host mail server host
     * @param sender email address of sender ("From" field)
     * @param recipients email address of recipients ("To" field)
     * @param contentType content type of the email message
     */
    public SimpleMailer(
            final String host, final String sender,
            final String[] recipients, final String contentType) {
        super();
        this.sender = sender;
        this.recipients = recipients;

        Properties sysProps = System.getProperties();
        if (sysProps == null) {
            sysProps = new Properties();
        }
        this.props = sysProps;
        this.props.put("mail.smtp.host", host);
        this.contentType = contentType;
    }

    /**
     * Sends an email.
     * @param subject email subject
     * @param body email body (content)
     * @throws MessagingException problem sending email
     */
    public final void send(
            final String subject, final String body)
            throws MessagingException {
        send(recipients, subject, body);
    }
    /**
     * Sends an email.
     * @param recipient email recipient ("To" field)
     * @param subject email subject
     * @param body email body (content)
     * @throws MessagingException problem sending email
     */
    public final void send(
            final String recipient, final String subject, final String body)
            throws MessagingException {
        send(new String[] {recipient}, subject, body);
    }
    /**
     * Sends an email.
     * @param recipients email recipients ("To" field)
     * @param subject email subject
     * @param body email body (content)
     * @throws MessagingException problem sending email
     */
    public final void send(
            final String[] recipients, final String subject, final String body)
            throws MessagingException {
        if (recipients == null || recipients.length == 0) {
            throw new IllegalArgumentException(
                    "No mail recipient provided.");
        }
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sender));
        for (int i = 0; i < recipients.length; i++) {
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(recipients[i]));
        }
        message.setSubject(subject);
        message.setContent(body, contentType);
        Transport.send(message);
    }
}
