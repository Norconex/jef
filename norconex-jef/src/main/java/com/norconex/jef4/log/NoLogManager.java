/* Copyright 2019-2020 Norconex Inc.
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
package com.norconex.jef4.log;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.input.NullInputStream;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Appender;

/**
 * <p>
 * Use this log manager if you do not want JEF to manage logs. Only the
 * settings defined through usual Log4j customization will take effect.
 * </p>
 * <p><b>Side effect:</b> Disabling log management means JEF will no longer
 * produce its own log files and automated log backups.  It also means it has
 * no way to report on logs through its API.  This may affect integration
 * with other tools such as JEF Monitor.
 * </p>
 * <p>
 * A typical use case for disabling JEF log management is to use console
 * logging only, or have your own file rotation stategy.
 * </p>
 *
 * <h3>XML configuration usage:</h3>
 * <pre>
 *  &lt;logManager class="com.norconex.jef4.log.NoLogManager"/&gt;
 * </pre>
 *
 * @author Pascal Essiembre
 * @since 4.1.2
 */
public class NoLogManager implements ILogManager {

    @Override
    public void loadFromXML(Reader in) throws IOException {
        //NOOP
    }

    @Override
    public void saveToXML(Writer out) throws IOException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try {
            XMLStreamWriter writer = factory.createXMLStreamWriter(out);
            writer.writeStartElement("logManager");
            writer.writeAttribute("class", getClass().getCanonicalName());
            writer.writeEndElement();
            writer.flush();
            writer.close();
        } catch (XMLStreamException e) {
            throw new IOException("Cannot save as XML.", e);
        }
    }

    @Override
    public Appender createAppender(String namespace) throws IOException {
        return null;
    }

    @Override
    public InputStream getLog(String namespace) throws IOException {
        return new NullInputStream(0);
    }

    @Override
    public InputStream getLog(String namespace, String jobId)
            throws IOException {
        return new NullInputStream(0);
    }

    @Override
    public void backup(String namespace, Date backupDate) throws IOException {
        //NOOP
    }

    @Override
    public boolean equals(final Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
    @Override
    public String toString() {
        return new ReflectionToStringBuilder(
                this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }
}
