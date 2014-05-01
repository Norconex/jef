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

import com.norconex.commons.lang.config.ConfigurationUtil;
import com.norconex.commons.lang.file.FileUtil;
import com.norconex.jef4.job.IJob;
import com.norconex.jef4.job.group.IJobGroup;

public class JobStatusTree implements Serializable {
    private static final long serialVersionUID = 74258557029725685L;

    private final JobStatusTreeNode rootNode;
    private Map<String, JobStatusTreeNode> flattenNodes = 
            new ListOrderedMap<>();
    
    public JobStatusTree(JobStatusTreeNode rootNode) {
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
    private void flattenNode(JobStatusTreeNode node) {
        flattenNodes.put(node.jobStatus.getJobName(), node);
        for (JobStatusTreeNode childNode : node.children) {
            flattenNode(childNode);
        }
    }
    
    public static JobStatusTree create(IJob rootJob) {
        if (rootJob == null) {
            throw new IllegalArgumentException("Root job cannot be null.");
        }
        return new JobStatusTree(createTreeNode(rootJob));
    }
    private static JobStatusTreeNode createTreeNode(IJob job) {
        IJobStatus status = new MutableJobStatus(job.getName());
        List<JobStatusTreeNode> childNodes = new ArrayList<>();
        if (job instanceof IJobGroup) {
            IJob[] jobs = ((IJobGroup) job).getJobs();
            for (IJob childJob : jobs) {
                JobStatusTreeNode childNode = createTreeNode(childJob);
                if (childNode != null) {
                    childNodes.add(childNode);
                }
            }
        }
        return new JobStatusTreeNode(status, childNodes);
    }
    
    
    public static JobStatusTree snapshot(File suiteIndex)
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
        return new JobStatusTree(
                loadTreeNode(xml.configurationAt("job"), suiteName, serial));
    }
    
    private static JobStatusTreeNode loadTreeNode(
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
                JobStatusTreeNode child = loadTreeNode(xml, suiteName, serial);
                if (child != null) {
                    childNodes.add(child);
                }
            }
        }
        return new JobStatusTreeNode(jobStatus, childNodes);
    }
    
    //--- Class: JobStatusTreeNode ---------------------------------------------
    public static class JobStatusTreeNode implements Serializable {
        private static final long serialVersionUID = 5605697815222704629L;
        private final IJobStatus jobStatus;
        private final List<JobStatusTreeNode> children;
        public JobStatusTreeNode(
                IJobStatus jobStatus, List<JobStatusTreeNode> children) {
            super();
            this.jobStatus = jobStatus;
            if (children == null) {
                this.children = new ArrayList<>(0);
            } else {
                this.children = children;
            }
        }
    }
}
