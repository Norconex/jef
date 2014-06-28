package com.norconex.jef4.status;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.norconex.commons.lang.PercentFormatter;
import com.norconex.commons.lang.config.ConfigurationUtil;
import com.norconex.commons.lang.file.FileUtil;
import com.norconex.jef4.job.IJob;
import com.norconex.jef4.job.group.IJobGroup;

public class JobSuiteStatuses implements Serializable {
    private static final long serialVersionUID = 74258557029725685L;

    private final JobStatusTreeNode rootNode;
    private Map<String, JobStatusTreeNode> flattenNodes = 
            new ListOrderedMap<>();
    
    public JobSuiteStatuses(JobStatusTreeNode rootNode) {
        this.rootNode = rootNode;
        flattenNode(rootNode);
    }

    public IJobStatus getRoot() {
        return rootNode.jobStatus;
    }
    public IJobStatus getJobStatus(IJob job) {
        return getJobStatus(job.getName());
    }
    public IJobStatus getJobStatus(String jobName) {
        JobStatusTreeNode node = flattenNodes.get(jobName);
        if (node != null) {
            return node.jobStatus;
        }
        return null;
    }
    
    public List<IJobStatus> getJobStatusList() {
        List<IJobStatus> list = new ArrayList<>(flattenNodes.size());
        for (JobStatusTreeNode node : flattenNodes.values()) {
            list.add(node.jobStatus);
        }
        return list;
    }
    
    public List<IJobStatus> getChildren(IJobStatus jobStatus) {
        return getChildren(jobStatus.getJobName());
    }
    public List<IJobStatus> getChildren(String jobName) {
        JobStatusTreeNode node = flattenNodes.get(jobName);
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
        return getParent(jobStatus.getJobName());
    }
    public IJobStatus getParent(String jobName) {
        JobStatusTreeNode node = flattenNodes.get(jobName);
        if (node == null) {
            return null;
        }
        return node.parentStatus;
    }
    
    public void accept(IJobStatusVisitor visitor) {
        accept(visitor, getRoot().getJobName());
    }
    private void accept(IJobStatusVisitor visitor, String jobName) {
        if (visitor != null) {
            IJobStatus status = getJobStatus(jobName);
            visitor.visitJobStatus(getJobStatus(jobName));
            for (IJobStatus child : getChildren(status)) {
                accept(visitor, child.getJobName());
            }
        }
    }
    
    private void flattenNode(JobStatusTreeNode node) {
        flattenNodes.put(node.jobStatus.getJobName(), node);
        for (JobStatusTreeNode childNode : node.children) {
            flattenNode(childNode);
        }
    }
    
    public static JobSuiteStatuses create(IJob rootJob) {
        if (rootJob == null) {
            throw new IllegalArgumentException("Root job cannot be null.");
        }
        return new JobSuiteStatuses(createTreeNode(null, rootJob));
    }
    private static JobStatusTreeNode createTreeNode(
            IJobStatus parentStatus, IJob job) {
        IJobStatus status = new MutableJobStatus(job.getName());
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
    
    
    public static JobSuiteStatuses snapshot(File suiteIndex)
            throws IOException {
        //--- Ensure file looks good ---
        if (suiteIndex == null) {
            throw new NullPointerException(
                    "\"suiteIndex\" argument cannot be null.");
        }
        if (!suiteIndex.exists()) {
            return null;
        }
        if (!suiteIndex.isFile()) {
            throw new IllegalArgumentException("Suite index is not a file: "
                    + suiteIndex.getAbsolutePath());
        }

        //--- Load XML file ---
        String suiteName = FileUtil.fromSafeFileName(
                FilenameUtils.getBaseName(suiteIndex.getPath()));
        XMLConfiguration xml = new XMLConfiguration();
        xml.setDelimiterParsingDisabled(false);
        try {
            xml.load(suiteIndex);
        } catch (ConfigurationException e) {
            throw new IOException(
                    "Could not load suite index: " + suiteIndex, e);
        }

        //--- Load status tree ---
        IJobStatusStore serial = 
                ConfigurationUtil.newInstance(xml, "jobStatusSerializer");
        return new JobSuiteStatuses(loadTreeNode(
                null, xml.configurationAt("job"), suiteName, serial));
    }
    
    private static JobStatusTreeNode loadTreeNode(
            IJobStatus parentStatus,
            HierarchicalConfiguration jobXML, String suiteName, 
            IJobStatusStore serial) throws IOException {
        if (jobXML == null) {
            return null;
        }
        String jobName = jobXML.getString("[@name]");
        IJobStatus jobStatus = serial.read(suiteName, jobName);
        List<HierarchicalConfiguration> xmls = jobXML.configurationsAt("job");
        List<JobStatusTreeNode> childNodes = new ArrayList<JobStatusTreeNode>();
        if (xmls != null) {
            for (HierarchicalConfiguration xml : xmls) {
                JobStatusTreeNode child = loadTreeNode(
                        jobStatus, xml, suiteName, serial);
                if (child != null) {
                    childNodes.add(child);
                }
            }
        }
        return new JobStatusTreeNode(parentStatus, jobStatus, childNodes);
    }
    
    //--- Class: JobStatusTreeNode ---------------------------------------------
    public static class JobStatusTreeNode implements Serializable {
        private static final long serialVersionUID = 5605697815222704629L;
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
        JobSuiteStatuses other = (JobSuiteStatuses) obj;
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
        toString(b, getRoot().getJobName(), 0);
        return b.toString();
    }
    private void toString(StringBuilder b, String jobName, int depth) {
        IJobStatus status = getJobStatus(jobName);
        b.append(StringUtils.repeat(' ', depth * 4));
        b.append(StringUtils.leftPad(new PercentFormatter().format(
                status.getProgress()), 4));
        b.append("  ").append(status.getJobName());
        b.append(System.lineSeparator());
        for (IJobStatus child : getChildren(status)) {
            toString(b, child.getJobName(), depth + 1);
        }
    }
}
