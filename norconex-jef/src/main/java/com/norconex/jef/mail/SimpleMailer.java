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

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Convenience class for sending simple emails.  It does not cover most
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
    private final String[] emailRecipients;

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
        this.emailRecipients = ArrayUtils.clone(recipients);

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
        send(emailRecipients, subject, body);
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
