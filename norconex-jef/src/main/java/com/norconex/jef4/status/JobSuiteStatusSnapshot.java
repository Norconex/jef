/* Copyright 2010-2019 Norconex Inc.
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
package com.norconex.jef4.status;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.norconex.commons.lang.PercentFormatter;
import com.norconex.commons.lang.config.XMLConfigurationUtil;
import com.norconex.commons.lang.file.FileUtil;
import com.norconex.jef4.job.IJob;
import com.norconex.jef4.job.group.IJobGroup;
import com.norconex.jef4.log.ILogManager;

public final class JobSuiteStatusSnapshot {

    private static final Logger LOG =
            LogManager.getLogger(JobSuiteStatusSnapshot.class);
    
    private static final int TO_STRING_INDENT = 4;
    
    private final ILogManager logManager;
    private final JobStatusTreeNode rootNode;
    private Map<String, JobStatusTreeNode> flattenNodes = 
            new ListOrderedMap<>();
    
    private JobSuiteStatusSnapshot(
            JobStatusTreeNode rootNode, ILogManager logManager) {
        this.logManager = logManager;
        this.rootNode = rootNode;
        flattenNode(rootNode);
    }

    public IJobStatus getRoot() {
        return rootNode.jobStatus;
    }
    public IJobStatus getJobStatus(IJob job) {
        return getJobStatus(job.getId());
    }
    public IJobStatus getJobStatus(String jobId) {
        JobStatusTreeNode node = flattenNodes.get(jobId);
        if (node != null) {
            return node.jobStatus;
        }
        return null;
    }
    
    public ILogManager getLogManager() {
        return logManager;
    }
    
    public List<IJobStatus> getJobStatusList() {
        List<IJobStatus> list = new ArrayList<>(flattenNodes.size());
        for (JobStatusTreeNode node : flattenNodes.values()) {
            list.add(node.jobStatus);
        }
        return list;
    }
    
    public List<IJobStatus> getChildren(IJobStatus jobStatus) {
        return getChildren(jobStatus.getJobId());
    }
    public List<IJobStatus> getChildren(String jobId) {
        JobStatusTreeNode node = flattenNodes.get(jobId);
        if (node == null) {
            return new ArrayList<IJobStatus>(0);
        }
        List<JobStatusTreeNode> nodes = node.children;
        List<IJobStatus> statuses = new ArrayList<>(nodes.size());
        for (JobStatusTreeNode childNode : nodes) {
            statuses.add(childNode.jobStatus);
        }
        return statuses;
    }

    public IJobStatus getParent(IJobStatus jobStatus) {
        return getParent(jobStatus.getJobId());
    }
    public IJobStatus getParent(String jobId) {
        JobStatusTreeNode node = flattenNodes.get(jobId);
        if (node == null) {
            return null;
        }
        return node.parentStatus;
    }
    
    public void accept(IJobStatusVisitor visitor) {
        accept(visitor, getRoot().getJobId());
    }
    private void accept(IJobStatusVisitor visitor, String jobId) {
        if (visitor != null) {
            visitor.visitJobStatus(getJobStatus(jobId));
            for (IJobStatus child : getChildren(jobId)) {
                accept(visitor, child.getJobId());
            }
        }
    }
    
    private void flattenNode(JobStatusTreeNode node) {
        flattenNodes.put(node.jobStatus.getJobId(), node);
        for (JobStatusTreeNode childNode : node.children) {
            flattenNode(childNode);
        }
    }
    
    public static JobSuiteStatusSnapshot create(
            IJob rootJob, ILogManager logManager) {
        if (rootJob == null) {
            throw new IllegalArgumentException("Root job cannot be null.");
        }
        return new JobSuiteStatusSnapshot(
                createTreeNode(null, rootJob), logManager);
    }
    private static JobStatusTreeNode createTreeNode(
            IJobStatus parentStatus, IJob job) {
        IJobStatus status = new MutableJobStatus(job.getId());
        List<JobStatusTreeNode> childNodes = new ArrayList<>();
        if (job instanceof IJobGroup) {
            IJob[] jobs = ((IJobGroup) job).getJobs();
            for (IJob childJob : jobs) {
                JobStatusTreeNode childNode = createTreeNode(status, childJob);
                if (childNode != null) {
                    childNodes.add(childNode);
                }
            }
        }
        return new JobStatusTreeNode(parentStatus, status, childNodes);
    }
    
    
    public static JobSuiteStatusSnapshot newSnapshot(File suiteIndex)
            throws IOException {
        //--- Ensure file looks good ---
        if (suiteIndex == null) {
            throw new IllegalArgumentException(
                    "Suite index file cannot be null.");
        }
        if (!suiteIndex.exists()) {
            return null;
        }
        if (!suiteIndex.isFile()) {
            throw new IllegalArgumentException("Suite index is not a file: "
                    + suiteIndex.getAbsolutePath());
        }
        if (suiteIndex.length() == 0) {
            LOG.error("Suite index file is empty.");
            return null;
        }

        //--- Load XML file ---
        String suiteName = FileUtil.fromSafeFileName(
                FilenameUtils.getBaseName(suiteIndex.getPath()));
        XMLConfiguration xml = new XMLConfiguration();
        XMLConfigurationUtil.disableDelimiterParsing(xml);
        String indexFileContent = null;
        // Using RandomAccessFile since evidence has shown it is better at 
        // dealing with files/locks in a way that cause less/no errors.
        try (RandomAccessFile ras = new RandomAccessFile(suiteIndex, "r")) {
            if (suiteIndex.exists()) {
                StringReader sr = new StringReader(ras.readUTF());
                xml.load(sr);
            }
        } catch (ConfigurationException e) {
            throw new IOException(
                    "Could not load suite index: " + suiteIndex
                    + ". Index file content:\n" + indexFileContent, e);
        }

        //--- LogManager ---
        ILogManager logManager = 
                XMLConfigurationUtil.newInstance(xml, "logManager");
        
        //--- Load status tree ---
        IJobStatusStore serial = 
                XMLConfigurationUtil.newInstance(xml, "statusStore");
        return new JobSuiteStatusSnapshot(loadTreeNode(
                null, xml.configurationAt("job"), suiteName, serial),
                        logManager);
    }
    
    private static JobStatusTreeNode loadTreeNode(
            IJobStatus parentStatus,
            HierarchicalConfiguration jobXML, String suiteName, 
            IJobStatusStore store) throws IOException {
        if (jobXML == null) {
            return null;
        }
        String jobId = jobXML.getString("[@name]");
        IJobStatus jobStatus = store.read(suiteName, jobId);
        List<HierarchicalConfiguration> xmls = jobXML.configurationsAt("job");
        List<JobStatusTreeNode> childNodes = new ArrayList<JobStatusTreeNode>();
        if (xmls != null) {
            for (HierarchicalConfiguration xml : xmls) {
                JobStatusTreeNode child = loadTreeNode(
                        jobStatus, xml, suiteName, store);
                if (child != null) {
                    childNodes.add(child);
                }
            }
        }
        return new JobStatusTreeNode(parentStatus, jobStatus, childNodes);
    }
    
    //--- Class: JobStatusTreeNode ---------------------------------------------
    public static class JobStatusTreeNode {
        private final IJobStatus parentStatus;
        private final IJobStatus jobStatus;
        private final List<JobStatusTreeNode> children;
        public JobStatusTreeNode(IJobStatus parentStatus,
                IJobStatus jobStatus, List<JobStatusTreeNode> children) {
            super();
            this.parentStatus = parentStatus;
            this.jobStatus = jobStatus;
            if (children == null) {
                this.children = new ArrayList<>(0);
            } else {
                this.children = children;
            }
        }
    }
    
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((flattenNodes == null) ? 0 : flattenNodes.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        JobSuiteStatusSnapshot other = (JobSuiteStatusSnapshot) obj;
        if (flattenNodes == null) {
            if (other.flattenNodes != null) {
                return false;
            }
        } else if (!flattenNodes.equals(other.flattenNodes)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        toString(b, getRoot().getJobId(), 0);
        return b.toString();
    }
    private void toString(StringBuilder b, String jobId, int depth) {
        IJobStatus status = getJobStatus(jobId);
        b.append(StringUtils.repeat(' ', depth * TO_STRING_INDENT));
        b.append(StringUtils.leftPad(new PercentFormatter().format(
                status.getProgress()), TO_STRING_INDENT));
        b.append("  ").append(status.getJobId());
        b.append(System.lineSeparator());
        for (IJobStatus child : getChildren(jobId)) {
            toString(b, child.getJobId(), depth + 1);
        }
    }
}
