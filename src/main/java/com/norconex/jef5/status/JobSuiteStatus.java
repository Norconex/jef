/* Copyright 2018 Norconex Inc.
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
package com.norconex.jef5.status;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.norconex.commons.lang.xml.EnhancedXMLStreamWriter;
import com.norconex.commons.lang.xml.XML;
import com.norconex.jef5.JefException;
import com.norconex.jef5.job.IJob;
import com.norconex.jef5.job.group.IJobGroup;
import com.norconex.jef5.suite.JobSuite;

/**
 * Class responsible for a job suite index file.
 * @author Pascal Essiembre
 */

//TODO merge with JobSuiteFacade and FileJobSessionStore but keep this naming.


// Anything under /workdir/SuiteId/session/ will be backed-up???
// non session stuff shall remain under /workdir/SuiteId/

      // related to all above... get rid of session store

//TODO have static get() method instead of public constructor so we can
// reuse instances if they are requested for same index path.
//TODO derive workdir and suite name from path when we add path here

//TODO what about making it readonly?  what about write methods?  We leave those in a separate class?  Use dao for that.

public class JobSuiteStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    private final JobSuiteStatusDAO dao;

//    private JobSuiteStatus(JobSuiteStatusDAO dao) {
//        super();
//        this.dao = dao;
//    }

    // These two could be merged with an ordered multivalue Map instead?
    // Root would be first key.
    private final TreeNode rootNode;
    private final Map<String, TreeNode> flatNodes = new ListOrderedMap<>();

    private JobSuiteStatus(
            /*String suiteName, */
            TreeNode rootNode,
//            Map<String, TreeNode> flattenNodes,
            JobSuiteStatusDAO dao) {
//        this.suiteName = suiteName;
        this.rootNode = rootNode;
        this.dao = dao;
//        this.flatNodes.putAll(flattenNodes);

        flattenNodes(rootNode, flatNodes);
    }

    public static JobSuiteStatus getInstance(JobSuite jobSuite) throws IOException {
        if (jobSuite == null) {
            return null;
        }
        return new JobSuiteStatus(
                loadJobTree(null, jobSuite.getRootJob()),
                jobSuite.getJobSuiteStatusDAO());
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

    public static JobSuiteStatus getInstance(Path suiteIndex)
            throws IOException {

        File file = suiteIndex.toFile();

        //TODO check if instance is defined for path already (weak hashmap).

        //--- Ensure file looks good ---
        Objects.requireNonNull(suiteIndex, "Suite index file cannot be null.");
        if (!file.exists()) {
            return null;
//            throw new IllegalArgumentException(
//                    "Suite index file does not exists: "
//                            + suiteIndex.toAbsolutePath());
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("Suite index is not a file: "
                    + suiteIndex.toAbsolutePath());
        }

        //--- Load XML file ---
//        String suiteName = FileUtil.fromSafeFileName(
//                FilenameUtils.getBaseName(suiteIndex.toString()));
//TODO have a readIndex and writeIndex methods... and remove writeUTF from JobSuite
//        return getInstance(/*suiteName, */ new FileReader(suiteIndex.toFile()));
//    }
//
//    // reader will be closed.
//    private static JobSuiteStatus getInstance(/*String suiteName, */Reader reader)
//            throws IOException {

// first job is suiteId (root).
//        <?xml version="1.0" encoding="UTF-8" ?>
//        <job id="async sleepy jobs">
//            <job id="Sleepy Job 10-3"/>
//            <job id="Sleepy Job 5-2"/>
//            <job id="Sleepy Job 3-1"/>
//        </job>

        if (file.exists()) {
            XML xml = new XML(file);
            JobSuiteStatusDAO dao = null;

            TreeNode tree = loadJobTree(null, xml.getXML("job"));
            if (tree == null) {
                return null;
            }
            dao = new JobSuiteStatusDAO(tree.jobId, suiteIndex.getParent());
            return new JobSuiteStatus(tree, dao);
        }
        return null;
    }

    public static JobSuiteStatus getInstance(
            Path statusDir, Reader reader) throws IOException {
        try (Reader r = reader) {
            XML xml = new XML(r);
            if (xml.getNode() == null) {
                return null;
            }

            TreeNode tree = loadJobTree(null, xml.getXML("job"));
            if (tree == null) {
                return null;
            }
            JobSuiteStatusDAO dao =
                    new JobSuiteStatusDAO(tree.jobId, statusDir);
            return new JobSuiteStatus(tree, dao);
        }
    }


//    public String toXML() {
//        return null;
//    }
//    public void toXML(Path suiteIndex) {
//
//    }
    public String toXML() throws IOException {
        StringWriter w = new StringWriter();
        toXML(w);
        return w.toString();
    }
    public void toXML(Path path) throws IOException {
        Files.createDirectories(path.getParent());
        try (Writer w = Files.newBufferedWriter(path)) {
            toXML(w);
        }
    }
    public void toXML(Writer writer) throws IOException {
        try {
            EnhancedXMLStreamWriter w = new EnhancedXMLStreamWriter(writer);
            w.writeStartDocument("UTF-8", "1.0");
            w.writeStartElement("suite-index");
            writeSuiteIndexJob(w, getRootId());
            w.writeEndElement();
            w.writeEndDocument();
            w.flush();
        } catch (XMLStreamException e) {
            throw new IOException("Could not write suite status as XML.", e);
        }
    }
    private void writeSuiteIndexJob(EnhancedXMLStreamWriter w, String jobId)
            throws XMLStreamException  {
        w.writeStartElement("job");
        w.writeAttributeString("id", jobId);
        for (String childId : getChildIds(jobId)) {
            writeSuiteIndexJob(w, childId);
        }
        w.writeEndElement();
    }


//    public String toJSON() {
//
//    }
//    public void toJSON(Writer writer) {
//
//    }

//    public void write(JobSuiteSessionWriter w) {
        //TODO have xml, json, etc.

//    }


    private static TreeNode loadJobTree(String parentId, XML nodeXML)
            throws IOException {
        if (nodeXML == null) {
            return null;
        }
        String jobId = nodeXML.getString("@id");

        TreeNode node = new TreeNode();
        node.parentId = parentId;
        node.jobId = jobId;

        List<XML> xmls = nodeXML.getXMLList("job");
        for (XML xml : xmls) {
            TreeNode child = loadJobTree(jobId, xml);
            if (child != null) {
                node.children.add(child);
            }
        }
        return node;
    }
    private static TreeNode loadJobTree(String parentId, IJob job)
            throws IOException {
        if (job == null) {
            return null;
        }
        String jobId = job.getId();

        TreeNode node = new TreeNode();
        node.parentId = parentId;
        node.jobId = jobId;

        if (job instanceof IJobGroup) {
            for (IJob childJob: ((IJobGroup) job).getJobs()) {
                TreeNode child = loadJobTree(jobId, childJob);
                if (child != null) {
                    node.children.add(child);
                }
            }
        }
        return node;
    }

    private static void flattenNodes(
            TreeNode node, Map<String, TreeNode> flatNodes) {
        flatNodes.put(node.jobId, node);
        for (TreeNode childNode : node.children) {
            flattenNodes(childNode, flatNodes);
        }
    }


    public JobStatus getRootStatus() {
        return read(rootNode.jobId);
    }
    public String getRootId() {
        return rootNode.jobId;
    }

    public JobStatus getStatus(IJob job) {
        return read(job.getId());
    }
    public JobStatus getStatus(String jobId) {
        return read(jobId);
    }
    private JobStatus read(String jobId) {
        try {
            return dao.read(jobId);
        } catch (IOException e) {
            throw new JefException("Cannot read status information for job: "
                    + jobId, e);
        }
    }

    public List<JobStatus> getAllStatuses() {
        List<JobStatus> list = new ArrayList<>(flatNodes.size());
        for (TreeNode treeNode : flatNodes.values()) {
            list.add(read(treeNode.jobId));
        }
        return list;
    }
    public List<String> getAllIds() {
        List<String> list = new ArrayList<>(flatNodes.size());
        for (TreeNode treeNode : flatNodes.values()) {
            list.add(treeNode.jobId);
        }
        return list;
    }

    public List<JobStatus> getChildStatuses(JobStatus jobStatus) {
        return getChildStatuses(jobStatus.getJobId());
    }
    public List<JobStatus> getChildStatuses(String jobId) {
        TreeNode treeNode = flatNodes.get(jobId);
        if (treeNode == null) {
            return new ArrayList<>(0);
        }
        List<TreeNode> treeNodes = treeNode.children;
        List<JobStatus> statuses = new ArrayList<>(treeNodes.size());
        for (TreeNode childNode : treeNodes) {
            statuses.add(read(childNode.jobId));
        }
        return statuses;
    }
    public List<String> getChildIds(JobStatus jobStatus) {
        return getChildIds(jobStatus.getJobId());
    }
    public List<String> getChildIds(String jobId) {
        TreeNode treeNode = flatNodes.get(jobId);
        List<String> ids = new ArrayList<>();
        if (treeNode == null) {
            return ids;
        }
        //TODO cache?
        for (TreeNode node : treeNode.children) {
            ids.add(node.jobId);
        }
        return ids;
    }

    public JobStatus getParentStatus(JobStatus jobStatus) {
        return getParentStatus(jobStatus.getJobId());
    }
    public JobStatus getParentStatus(String jobId) {
        TreeNode treeNode = flatNodes.get(jobId);
        if (treeNode == null) {
            return null;
        }
        return read(treeNode.parentId);
    }
    public String getParentId(JobStatus jobStatus) {
        return getParentId(jobStatus.getJobId());
    }
    public String getParentId(String jobId) {
        TreeNode treeNode = flatNodes.get(jobId);
        if (treeNode == null) {
            return null;
        }
        return treeNode.parentId;
    }

    public void accept(IJobStatusVisitor visitor) {
        accept(visitor, getRootStatus().getJobId());
    }
    private void accept(IJobStatusVisitor visitor, String jobId) {
        if (visitor != null) {
            visitor.visitJobStatus(getStatus(jobId));
            for (JobStatus child : getChildStatuses(jobId)) {
                accept(visitor, child.getJobId());
            }
        }
    }

    protected static class TreeNode implements Serializable {
        private static final long serialVersionUID = 1L;

        private String jobId;
        private String parentId;
        private final List<TreeNode> children = new ArrayList<>();
        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof TreeNode)) {
                return false;
            }
            TreeNode castOther = (TreeNode) other;
            return new EqualsBuilder()
                    .append(jobId, castOther.jobId)
                    .append(parentId, castOther.parentId)
                    .append(children, castOther.children)
                    .isEquals();
        }
        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(jobId)
                    .append(parentId)
                    .append(children)
                    .toHashCode();
        }
        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                    .append("jobId", jobId)
                    .append("parentId", parentId)
                    .append("childIds", children)
                    .toString();
        }
    }

//    private static class TreeNode {//implements Serializable {
//        //private static final long serialVersionUID = 1L;
//
//        private String jobId;
//        private String parentId;
//        private final List<String> childIds = new ArrayList<>();
//        @Override
//        public boolean equals(final Object other) {
//            if (!(other instanceof TreeNode)) {
//                return false;
//            }
//            TreeNode castOther = (TreeNode) other;
//            return new EqualsBuilder()
//                    .append(jobId, castOther.jobId)
//                    .append(parentId, castOther.parentId)
//                    .append(childIds, castOther.childIds)
//                    .isEquals();
//        }
//        @Override
//        public int hashCode() {
//            return new HashCodeBuilder()
//                    .append(jobId)
//                    .append(parentId)
//                    .append(childIds)
//                    .toHashCode();
//        }
//        @Override
//        public String toString() {
//            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
//                    .append("jobId", jobId)
//                    .append("parentId", parentId)
//                    .append("childIds", childIds)
//                    .toString();
//        }
//    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof JobSuiteStatus)) {
            return false;
        }
        JobSuiteStatus castOther = (JobSuiteStatus) other;
        return new EqualsBuilder()
                .append(dao, castOther.dao)
                .append(flatNodes, castOther.flatNodes)
                .isEquals();
    }
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(dao)
                .append(flatNodes)
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
//        JobStatus status = getSession(jobId);
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
//        for (JobStatus child : getChildSessions(jobId)) {
//            toString(b, child.getJobId(), depth + 1);
//        }
    }



//    public static final String SESSION_SUBDIR = "session";
//    public static final String SESSION_BACKUP_SUBDIR = "backups/session";
//
//    private final Path workdir;
//    private final String suiteId;

//    public JobSuiteStatus(Path workdir, String suiteId) {
//        super();
//        this.workdir = workdir;
//        this.suiteId = suiteId;
//    }
//    public JobSuiteStatus(JobSuite jobSuite) {
//        this(jobSuite.getWorkdir(), jobSuite.getId());
//    }

//    public Path getWorkdir() {
//        return workdir;
//    }
//    public String getSuiteId() {
//        return suiteId;
//    }

//    public Path getSessionDir() {
//        return getSessionDir(workdir, suiteId);
//    }
//    public static Path getSessionDir(Path suiteWorkdir, String suiteId) {
//        return suiteWorkdir.resolve(Paths.get(
//                FileUtil.toSafeFileName(suiteId), SESSION_SUBDIR));
//    }
//
//    public Path getSessionBackupDir(LocalDateTime date) {
//        return getSessionBackupDir(workdir, suiteId, date);
//    }
//    public static Path getSessionBackupDir(
//            Path suiteWorkdir, String suiteId, LocalDateTime date) {
//        return FileUtil.toDateFormattedDir(suiteWorkdir.resolve(Paths.get(
//                FileUtil.toSafeFileName(suiteId), SESSION_SUBDIR)).toFile(),
//                DateUtil.toDate(date), "yyyy/MM/dd/HH-mm-ss").toPath();
//    }
//
//    public Path getSessionIndex() {
//        return getSessionIndex(workdir, suiteId);
//    }
//    /**
//     * Gets the path to job suite index.
//     * @param suiteWorkdir suite working directory
//     * @param suiteId suite unique ID (ID of the root job)
//     * @return file the index file
//     */
//    public static Path getSessionIndex(Path suiteWorkdir, String suiteId) {
//        return getSessionDir(suiteWorkdir, suiteId).resolve("suite.index");
//    }
}
