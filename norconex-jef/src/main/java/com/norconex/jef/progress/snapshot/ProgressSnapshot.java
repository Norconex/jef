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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.norconex.jef.IJobContext;

/*default*/ class ProgressSnapshot implements IProgressSnapshot {
    private static final long serialVersionUID = -1602020031077532682L;

    private String jobId;
    private IJobContext jobContext;
    private String note;
    private long progress;
    private Date endTime;
    private Date startTime;
    private Date lastActivity;
    private String metadata;
    private boolean stopRequested;
    private long elapsedTime;
    private double completionRatio;
    private Status status;
    private boolean recovery;
    private final List<IProgressSnapshot> children = 
            new ArrayList<IProgressSnapshot>();
    public String getJobId() {
        return jobId;
    }
    public IJobContext getJobContext() {
        return jobContext;
    }
    public String getNote() {
        return note;
    }
    public long getProgress() {
        return progress;
    }
    public Date getEndTime() {
        return endTime;
    }
    public Date getStartTime() {
        return startTime;
    }
    public Date getLastActivity() {
        return lastActivity;
    }
    public String getMetadata() {
        return metadata;
    }
    public boolean isStopRequested() {
        return stopRequested;
    }
    public long getElapsedTime() {
        return elapsedTime;
    }
    public double getCompletionRatio() {
        return completionRatio;
    }
    public Status getStatus() {
        return status;
    }
    public boolean isRecovery() {
        return recovery;
    }
    public List<IProgressSnapshot> getChildren() {
        return children;
    }
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    public void setJobContext(IJobContext jobContext) {
        this.jobContext = jobContext;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public void setProgress(long progress) {
        this.progress = progress;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public void setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
    }
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    public void setStopRequested(boolean stopRequested) {
        this.stopRequested = stopRequested;
    }
    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
    public void setCompletionRatio(double completionRatio) {
        this.completionRatio = completionRatio;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public void setRecovery(boolean recovery) {
        this.recovery = recovery;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(children)
            .append(completionRatio)
            .append(elapsedTime)
            .append(endTime)
            .append(jobContext)
            .append(jobId)
            .append(lastActivity)
            .append(metadata)
            .append(note)
            .append(progress)
            .append(recovery)
            .append(startTime)
            .append(status)
            .append(stopRequested)
            .toHashCode();
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ProgressSnapshot)) {
            return false;
        }
        ProgressSnapshot other = (ProgressSnapshot) obj;
        return new EqualsBuilder()
            .append(children, other.children)
            .append(completionRatio, other.completionRatio)
            .append(elapsedTime, other.elapsedTime)
            .append(endTime, other.endTime)
            .append(jobContext, other.jobContext)
            .append(jobId, other.jobId)
            .append(lastActivity, other.lastActivity)
            .append(metadata, other.metadata)
            .append(note, other.note)
            .append(progress, other.progress)
            .append(recovery, other.recovery)
            .append(startTime, other.startTime)
            .append(status, other.status)
            .append(stopRequested, other.stopRequested)
            .isEquals();
    }
    
    @Override
    public String toString() {
        return "ProgressSnapshot [jobId=" + jobId + ", jobContext="
                + jobContext + ", note=" + note + ", progress=" + progress
                + ", endTime=" + endTime + ", startTime=" + startTime
                + ", lastActivity=" + lastActivity + ", metadata=" + metadata
                + ", stopRequested=" + stopRequested + ", elapsedTime="
                + elapsedTime + ", completionRatio=" + completionRatio
                + ", status=" + status + ", recovery=" + recovery
                + ", children=" + children + "]";
    }


}