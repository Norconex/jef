/* Copyright 2010-2018 Norconex Inc.
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
package com.norconex.jef5.session;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.text.StringEscapeUtils;

import com.norconex.commons.lang.config.XMLConfigurationUtil;
import com.norconex.jef5.JefException;
import com.norconex.jef5.job.IJob;
import com.norconex.jef5.job.group.IJobGroup;
import com.norconex.jef5.session.store.IJobSessionStore;
import com.norconex.jef5.suite.JobSuite;

// snapshot in time.  
public final class JobSessionFacade {

//    private static final Logger LOG = 
//            LoggerFactory.getLogger(JobSessionFacade.class);
    
//    private final String suiteName;
    private final IJobSessionStore store;

    // These two could be merged with an ordered multivalue Map instead?  
    // Root would be first key.
    private final TreeNode rootNode;
    private final Map<String, TreeNode> flattenNodes = 
            new ListOrderedMap<>();

    private JobSessionFacade(
            /*String suiteName, */
            TreeNode rootNode, 
            Map<String, TreeNode> flattenNodes, IJobSessionStore store) {
//        this.suiteName = suiteName;
        this.rootNode = rootNode;
        this.store = store;
        this.flattenNodes.putAll(flattenNodes);
    }

    public static JobSessionFacade get(JobSuite jobSuite) throws IOException {
        if (jobSuite == null) {
            return null;
        }
        
        Map<String, TreeNode> flattenNodes = new ListOrderedMap<>();
        return new JobSessionFacade(
                loadJobTree(null, jobSuite.getRootJob(), flattenNodes), 
                flattenNodes,
                jobSuite.getJobSessionStore());        
    }

//  //TODO move these writeXX methods to JobSessionFacade??
//  private void writeJobSuiteIndex() 
//          throws IOException {
//      
//      Path indexFile = getSuiteIndexFile();
//      
//      StringWriter out = new StringWriter();
//      out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
//      out.write("<suite-index>");
//          
//      //--- JobStatusSerializer ---
//      out.flush();
//      if (jobSessionStore instanceof IXMLConfigurable) {
//          ((IXMLConfigurable) jobSessionStore).saveToXML(out);
//      }
//      
//      //--- Jobs ---
//      writeJobSuiteIndexJob(out, rootJob);
//      
//      out.write("</suite-index>");
//      out.flush();
//      
//      // Using RandomAccessFile since evidence has shown it is better at 
//      // dealing with files/locks in a way that cause less/no errors.
//      try (RandomAccessFile ras = 
//              new RandomAccessFile(indexFile.toFile(), "rwd");
//              FileChannel channel = ras.getChannel();
//              FileLock lock = channel.lock()) {
//          ras.writeUTF(out.toString());
//      }
//  }
//  private void writeJobSuiteIndexJob(
//          Writer out, IJob job) throws IOException {
//      out.write("<job id=\"");
//      out.write(StringEscapeUtils.escapeXml11(job.getId()));
//      out.write("\">");
//      if (job instanceof IJobGroup) {
//          for (IJob childJob: ((IJobGroup) job).getJobs()) {
//              writeJobSuiteIndexJob(out, childJob);
//          }
//      }
//      out.write("</job>");
//  }
        
    public static JobSessionFacade get(Path suiteIndex) throws IOException {
        
        //--- Ensure file looks good ---
        if (suiteIndex == null) {
            throw new IllegalArgumentException(
                    "Suite index file cannot be null.");
        }
        if (!suiteIndex.toFile().exists()) {
            return null;
        }
        if (!suiteIndex.toFile().isFile()) {
            throw new IllegalArgumentException("Suite index is not a file: "
                    + suiteIndex.toAbsolutePath());
        }

        //--- Load XML file ---
//        String suiteName = FileUtil.fromSafeFileName(
//                FilenameUtils.getBaseName(suiteIndex.toString()));
//TODO have a readIndex and writeIndex methods... and remove writeUTF from JobSuite
        return get(/*suiteName, */ new FileReader(suiteIndex.toFile()));
    }

    // reader will be closed.
    public static JobSessionFacade get(/*String suiteName, */Reader reader)
            throws IOException {
       
        try (Reader r = reader) {
            XMLConfiguration xml = XMLConfigurationUtil.newXMLConfiguration(r);
            if (xml == null) {
                return null;
            }
            IJobSessionStore store = 
                    XMLConfigurationUtil.newInstance(xml, "store");
            if (store == null) {
                throw new IOException("No job session store configuration "
                      + "defined in job session index.");
            }
            Map<String, TreeNode> flattenNodes = new ListOrderedMap<>();
            return new JobSessionFacade(
                    loadJobTree(null, xml.configurationAt("job"), flattenNodes), 
                    flattenNodes,
                    store);
        }
    }
    
    public void write(Path suiteIndex) throws IOException {
        write(new FileWriter(suiteIndex.toFile()));
    }


    // Closes writer
    public void write(Writer writer) throws IOException {
        try (Writer w = writer) {
            w.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            w.write("<suite-index>");
            w.flush();
            XMLConfigurationUtil.saveToXML(store, w, "store");
            writeSuiteIndexJob(w, getRootId());
            w.write("</suite-index>");
            w.flush();
        }
    }    
    private void writeSuiteIndexJob(Writer w, String jobId) throws IOException {
        w.write("<job id=\"");
        w.write(StringEscapeUtils.escapeXml11(jobId));
        List<String> childIds = getChildIds(jobId);
        if (!childIds.isEmpty()) {
            w.write("\">");
            for (String childId : childIds) {
                writeSuiteIndexJob(w, childId);
            }
            w.write("</job>");
        } else {
            w.write("\"/>");
        }
    }    

    private static TreeNode loadJobTree(
            String parentId, HierarchicalConfiguration<ImmutableNode> nodeXML,
            Map<String, TreeNode> flattenNodes) throws IOException {
        if (nodeXML == null) {
            return null;
        }
        String jobId = nodeXML.getString("[@id]");
        
        TreeNode node = new TreeNode();
        node.parentId = parentId;
        node.jobId = jobId;

        flattenNodes.put(jobId, node);

        List<HierarchicalConfiguration<ImmutableNode>> xmls = 
                nodeXML.configurationsAt("job");
        if (xmls != null) {
            for (HierarchicalConfiguration<ImmutableNode> xml : xmls) {
                TreeNode child = loadJobTree(jobId, xml, flattenNodes);
                if (child != null) {
                    node.childIds.add(child.jobId);
                }
            }
        }
        return node;
    }
    private static TreeNode loadJobTree(String parentId, IJob job,
            Map<String, TreeNode> flattenNodes) throws IOException {
        if (job == null) {
            return null;
        }
        String jobId = job.getId();
        
        TreeNode node = new TreeNode();
        node.parentId = parentId;
        node.jobId = jobId;

        flattenNodes.put(jobId, node);

        if (job instanceof IJobGroup) {
            for (IJob childJob: ((IJobGroup) job).getJobs()) {
                TreeNode child = loadJobTree(jobId, childJob, flattenNodes);
                if (child != null) {
                    node.childIds.add(child.jobId);
                }
            }
        }
        return node;
    }
    
    public JobSession getRootSession() {
        return read(rootNode.jobId);
    }
    public String getRootId() {
        return rootNode.jobId;
    }
    
    public JobSession getSession(IJob job) {
        return read(job.getId());
    }
    public JobSession getSession(String jobId) {
        return read(jobId);
    }
    private JobSession read(String jobId) {
        try {
            return store.read(getRootId(), jobId);
        } catch (IOException e) {
            throw new JefException("Cannot read session information for job: "
                    + jobId, e);
        }
    }
    
    public List<JobSession> getAllSessions() {
        List<JobSession> list = new ArrayList<>(flattenNodes.size());
        for (TreeNode treeNode : flattenNodes.values()) {
            list.add(read(treeNode.jobId));
        }
        return list;
    }
    public List<String> getAllIds() {
        List<String> list = new ArrayList<>(flattenNodes.size());
        for (TreeNode treeNode : flattenNodes.values()) {
            list.add(treeNode.jobId);
        }
        return list;
    }
    
    public List<JobSession> getChildSessions(JobSession jobSession) {
        return getChildSessions(jobSession.getJobId());
    }
    public List<JobSession> getChildSessions(String jobId) {
        TreeNode treeNode = flattenNodes.get(jobId);
        if (treeNode == null) {
            return new ArrayList<>(0);
        }
        List<String> treeNodes = treeNode.childIds;
        List<JobSession> sessions = new ArrayList<>(treeNodes.size());
        for (String childId : treeNodes) {
            sessions.add(read(childId));
        }
        return sessions;
    }
    public List<String> getChildIds(JobSession jobSession) {
        return getChildIds(jobSession.getJobId());
    }
    public List<String> getChildIds(String jobId) {
        TreeNode treeNode = flattenNodes.get(jobId);
        if (treeNode == null) {
            return new ArrayList<>(0);
        }
        return treeNode.childIds;
    }

    public JobSession getParentSession(JobSession jobSession) {
        return getParentSession(jobSession.getJobId());
    }
    public JobSession getParentSession(String jobId) {
        TreeNode treeNode = flattenNodes.get(jobId);
        if (treeNode == null) {
            return null;
        }
        return read(treeNode.parentId);
    }
    public String getParentId(JobSession jobSession) {
        return getParentId(jobSession.getJobId());
    }
    public String getParentId(String jobId) {
        TreeNode treeNode = flattenNodes.get(jobId);
        if (treeNode == null) {
            return null;
        }
        return treeNode.parentId;
    }
    
    public void accept(IJobSessionVisitor visitor) {
        accept(visitor, getRootSession().getJobId());
    }
    private void accept(IJobSessionVisitor visitor, String jobId) {
        if (visitor != null) {
            visitor.visitJobSession(getSession(jobId));
            for (JobSession child : getChildSessions(jobId)) {
                accept(visitor, child.getJobId());
            }
        }
    }
    
    private static class TreeNode {
        private String jobId;
        private String parentId;
        private final List<String> childIds = new ArrayList<>();
        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof TreeNode)) {
                return false;
            }
            TreeNode castOther = (TreeNode) other;
            return new EqualsBuilder()
                    .append(jobId, castOther.jobId)
                    .append(parentId, castOther.parentId)
                    .append(childIds, castOther.childIds)
                    .isEquals();
        }
        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(jobId)
                    .append(parentId)
                    .append(childIds)
                    .toHashCode();
        }
        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                    .append("jobId", jobId)
                    .append("parentId", parentId)
                    .append("childIds", childIds)
                    .toString();
        }        
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof JobSessionFacade)) {
            return false;
        }
        JobSessionFacade castOther = (JobSessionFacade) other;
        return new EqualsBuilder()
                .append(store, castOther.store)
                .append(flattenNodes, castOther.flattenNodes)
                .isEquals();
    }
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(store)
                .append(flattenNodes)
                .toHashCode();
    }

    //TODO have multiple export options instead?  Or formatters that use
    // visitors?
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        toString(b, rootNode.jobId, 0);
        return b.toString();
    }
    private void toString(StringBuilder b, String jobId, int depth) {
        for (int i = 0; i < depth; i++) {
            b.append("│  ");
        }
        b.append("├──");
        b.append(jobId);
        b.append(System.lineSeparator());
        for (String childId : getChildIds(jobId)) {
            toString(b, childId, depth + 1);
        }
//        JobSession status = getSession(jobId);
//        String percent;
//        if (status == null) {
//            LOG.error("Could not obtain status for job Id: {}", jobId);
//            percent = "?";
//        } else {
//            percent = new PercentFormatter().format(status.getProgress());
//        }
//        b.append(StringUtils.repeat(' ', depth * TO_STRING_INDENT));
//        b.append(StringUtils.leftPad(percent, TO_STRING_INDENT));
//        b.append("  ").append(jobId);
//        b.append(System.lineSeparator());
//        for (JobSession child : getChildSessions(jobId)) {
//            toString(b, child.getJobId(), depth + 1);
//        }
    }
}
