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
package com.norconex.jef.progress.snapshot;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.norconex.jef.JobException;
import com.norconex.jef.progress.IJobStatus;

/**
 * Renders the a job suite progress snapshot in XML format.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
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
        writeProgressSnapshot(out, snapshot, 0);
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
        } catch (Exception e) {}
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
        }
        return defaultValue;
    }
    
    private IProgressSnapshot readProgressSnapshot(
            Element jobElement) {

        //2012-02-20: Did 2 hours in plane
        
        ProgressSnapshot snapshot = new ProgressSnapshot();
        snapshot.completionRatio = xmlDouble(jobElement, "completionRatio", 0);
        snapshot.elapsedTime = xmlLong(jobElement, "elapsedTime", 0);
        snapshot.endTime = xmlDate(jobElement, "endTime", null);
//        snapshot.jobContext = jobElement.getJobContext();
        snapshot.jobId = xmlString(jobElement, "id", null);
        snapshot.lastActivity = xmlDate(jobElement, "lastActivity", null);
        snapshot.metadata = xmlString(jobElement, "metadata", null);
        snapshot.note = xmlString(jobElement, "note", null);
        snapshot.recovery = xmlBoolean(jobElement, "recovery", false);
        snapshot.startTime = xmlDate(jobElement, "startTime", null);
        String status = xmlString(jobElement, "status", null);
        if (status != null) {
            snapshot.status = IJobStatus.Status.valueOf(status);
        }
        snapshot.stopRequested = xmlBoolean(jobElement, "stopRequested", false);
        
        NodeList jobNodes = jobElement.getElementsByTagName("job");
        for (int i = 0; i < jobNodes.getLength(); i++){
            Node jobNode = jobNodes.item(i);
            if (jobNode.getNodeType() == Node.ELEMENT_NODE){
                Element childJobElement = (Element) jobNode;
                snapshot.children.add(
                        readProgressSnapshot(childJobElement));
            }
        }
        
        return snapshot;
        
//        
//        NodeList statusNodes = doc.getElementsByTagName("job");
//        for (int i = 0; i < statusNodes.getLength(); i++){
//            Node statusNode = statusNodes.item(i);
//            if (statusNode.getNodeType() == Node.ELEMENT_NODE){
//                Element element = (Element) statusNode;
//                String id = element.getAttribute("id");
//                String type = element.getAttribute("type");
//                NodeList jobNodes = element.getElementsByTagName("job");
//                Node jobNode = jobNodes.item(0);
//
//                
//                String jobXML = nodeToString(jobNode);
//                
//                System.out.println("ID:" + id + " type:" + type);
//                System.out.println("Text Content:" + nodeToString(jobNode));//statusNode));
//                
//                IProgressSnapshot snapshot = 
//                        DESERIALIZER.readProgressSnapshot(
//                                new StringReader(jobXML));
//                collectors.add(new CollectorStatus(
//                        Long.parseLong(id),
//                        CollectorType.valueOf(type),
//                        snapshot));
//                // Example:http://www.developerfusion.com/code/2064/a-simple-way-to-read-an-xml-file-in-java/
//            }
//        }
//
//        
//        
//        IJobStatus progress = jobSuite.getJobProgress(job);
//        return snapshot;
    }

    
    private void writeProgressSnapshot(
            PrintWriter out, IProgressSnapshot progress, int depth) {

        indent(out, depth);
        out.println("<job>");
        writeTag(out, "id", progress.getJobId(), depth);
        writeTag(out, "note", progress.getNote(), depth);
        writeTag(out, "completionRatio", 
                Double.toString(progress.getCompletionRatio()), depth);
        writeDateTag(out, "startTime", progress.getStartTime(), depth);
        writeDateTag(out, "endTime", progress.getEndTime(), depth);
        writeDateTag(out, "lastActivity", progress.getLastActivity(), depth);
        writeTag(out, "status", progress.getStatus().toString(), depth);
        writeTag(out, "recovery", 
                Boolean.toString(progress.isRecovery()), depth);
        List<IProgressSnapshot> children = progress.getChildren();
        indent(out, depth + 1);
        out.println("<children>");
        for (IProgressSnapshot childProgress : children) {
            writeProgressSnapshot(out, childProgress, depth + 1);
        }
        indent(out, depth + 1);
        out.println("</children>");
        indent(out, depth);
        out.println("</job>");
    }
    
    private void indent(PrintWriter out, int depth) {
        for (int i = 0; i < depth; i++) {
            out.print("    ");
        }
    }
    
    private String esc(String str) {
        // Yes, Apache Commons Lang does it. Did not want to create dependency.
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = 
                new StringCharacterIterator(str);
        char character =  iterator.current();
        while (character != CharacterIterator.DONE ){
          if (character == '<') {
            result.append("&lt;");
          }
          else if (character == '>') {
            result.append("&gt;");
          }
          else if (character == '\"') {
            result.append("&quot;");
          }
          else if (character == '\'') {
            result.append("&#039;");
          }
          else if (character == '&') {
             result.append("&amp;");
          }
          else {
            //the char is not a special one
            //add it to the result as is
            result.append(character);
          }
          character = iterator.next();
        }
        return result.toString();
    }
    private void writeDateTag(
            PrintWriter out, String tagName, Date tagValue, int depth)  {
        if (tagValue != null) {
            writeTag(out, tagName, Long.toString(tagValue.getTime()), depth);
        }
    }
    private void writeTag(PrintWriter out,
            String tagName, String tagValue, int depth) {
        if (tagValue != null && tagValue.trim().length() > 0) {
            indent(out, depth);
            out.print("    <");
            out.print(tagName);
            out.print(">");
            out.print(esc(tagValue));
            out.print("</");
            out.print(tagName);
            out.println(">");
        }
    }



}
