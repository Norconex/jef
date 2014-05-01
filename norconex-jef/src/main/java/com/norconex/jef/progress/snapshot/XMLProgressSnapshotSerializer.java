/* Copyright 2010-2014 Norconex Inc.
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
package com.norconex.jef.progress.snapshot;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.norconex.jef.JobException;
import com.norconex.jef.progress.IJobStatus;

/**
 * Renders the a job suite progress snapshot in XML format.
 * @author Pascal Essiembre
 * @since 2.0
 */
@SuppressWarnings("nls")
public class XMLProgressSnapshotSerializer
        implements IProgressSnapshotSerializer {

    private static final long serialVersionUID = -8458122489700630248L;

    @Override
    public void writeProgressSnapshot(
            Writer writer, IProgressSnapshot snapshot) {
        PrintWriter out = new PrintWriter(writer);
        writeProgressSnapshot(out, snapshot);
    }
    @Override
    public IProgressSnapshot readProgressSnapshot(Reader in) {
        try {
            DocumentBuilderFactory docBuilderFactory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder =
                    docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new InputSource(in));
            // normalize text representation
            doc.getDocumentElement().normalize();
            return readProgressSnapshot(doc.getDocumentElement());
        } catch (Exception e) {
            throw new JobException(
                    "Cannot load progress snapshot from XML.", e);
        }
    }

    private String xmlString(
            Element element, String name, String defaultValue) {
        try {
            NodeList elements = element.getElementsByTagName(name);
            if (elements.getLength() > 0) {
                for (int i = 0; i < elements.getLength(); i++){
                    Node node = elements.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE){
                        String value = node.getTextContent();
                        if (value != null && value.trim().length() > 0) {
                            return value;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // swallow
        }
        return defaultValue;
    }


    private double xmlDouble(
            Element element, String name, double defaultValue) {
        try {
            String value = xmlString(element, name, null);
            if (value != null) {
                return Double.parseDouble(value);
            }
        } catch (Exception e) {
            // swallow
        }
        return defaultValue;
    }
    private boolean xmlBoolean(
            Element element, String name, boolean defaultValue) {
        try {
            String value = xmlString(element, name, null);
            if (value != null) {
                return Boolean.parseBoolean(value);
            }
        } catch (Exception e) {
            // swallow
        }
        return defaultValue;
    }
    private long xmlLong(
            Element element, String name, long defaultValue) {
        try {
            String value = xmlString(element, name, null);
            if (value != null) {
                return Long.parseLong(value);
            }
        } catch (Exception e) {
            // swallow
        }
        return defaultValue;
    }
    private Date xmlDate(
            Element element, String name, Date defaultValue) {
        try {
            String value = xmlString(element, name, null);
            if (value != null) {
                return new Date(Long.parseLong(value));
            }
        } catch (Exception e) {
            // swallow
        }
        return defaultValue;
    }

    private IProgressSnapshot readProgressSnapshot(
            Element jobElement) {

        ProgressSnapshot snapshot = new ProgressSnapshot();
        snapshot.setCompletionRatio(
                xmlDouble(jobElement, "completionRatio", 0));
        snapshot.setElapsedTime(xmlLong(jobElement, "elapsedTime", 0));
        snapshot.setEndTime(xmlDate(jobElement, "endTime", null));
        snapshot.setJobId(xmlString(jobElement, "id", null));
        snapshot.setLastActivity(xmlDate(jobElement, "lastActivity", null));
        snapshot.setMetadata(xmlString(jobElement, "metadata", null));
        snapshot.setNote(xmlString(jobElement, "note", null));
        snapshot.setRecovery(xmlBoolean(jobElement, "recovery", false));
        snapshot.setStartTime(xmlDate(jobElement, "startTime", null));
        String status = xmlString(jobElement, "status", null);
        if (status != null) {
            snapshot.setStatus(IJobStatus.Status.valueOf(status));
        }
        snapshot.setStopRequested(
                xmlBoolean(jobElement, "stopRequested", false));

        NodeList jobNodes = jobElement.getElementsByTagName("job");
        for (int i = 0; i < jobNodes.getLength(); i++){
            Node jobNode = jobNodes.item(i);
            if (jobNode.getNodeType() == Node.ELEMENT_NODE){
                Element childJobElement = (Element) jobNode;
                snapshot.getChildren().add(
                        readProgressSnapshot(childJobElement));
            }
        }

        return snapshot;
    }


    private void writeProgressSnapshot(
            PrintWriter out, IProgressSnapshot progress) {

        out.print("<job>");
        writeTag(out, "id", progress.getJobId());
        writeTag(out, "note", progress.getNote());
        writeTag(out, "completionRatio",
                Double.toString(progress.getCompletionRatio()));
        writeDateTag(out, "startTime", progress.getStartTime());
        writeDateTag(out, "endTime", progress.getEndTime());
        writeDateTag(out, "lastActivity", progress.getLastActivity());
        writeTag(out, "status", ObjectUtils.toString(
                progress.getStatus()));
        writeTag(out, "recovery",
                Boolean.toString(progress.isRecovery()));
        List<IProgressSnapshot> children = progress.getChildren();
        out.print("<children>");
        for (IProgressSnapshot childProgress : children) {
            writeProgressSnapshot(out, childProgress);
        }
        out.print("</children>");
        out.print("</job>");
    }

    private void writeDateTag(
            PrintWriter out, String tagName, Date tagValue)  {
        if (tagValue != null) {
            writeTag(out, tagName, Long.toString(tagValue.getTime()));
        }
    }
    private void writeTag(PrintWriter out, String tagName, String tagValue) {
        if (StringUtils.isNotBlank(tagValue)) {
            out.print("<");
            out.print(tagName);
            out.print(">");
            out.print(StringEscapeUtils.escapeXml(tagValue));
            out.print("</");
            out.print(tagName);
            out.print(">");
        }
    }



}
