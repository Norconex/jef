package com.norconex.jef4.status;

import java.util.Date;

import com.norconex.commons.lang.map.Properties;

public class MutableJobStatus implements IJobStatus {

    private static final long serialVersionUID = -3106380316055356588L;
    private final String name;
    private double progress;
    private String note;
    private Properties properties = new Properties();
    private JobDuration duration = new JobDuration();
    private Date lastActivity;

    public MutableJobStatus(String jobName) {
        this.name = jobName;
    }

    @Override
    public String getJobName() {
        return name;
    }

    @Override
    public JobState getState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getProgress() {
        return progress;
    }

    @Override
    public String getNote() {
        return note;
    }

    @Override
    public JobDuration getDuration() {
        return duration;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    

    
    public void setDuration(JobDuration duration) {
        this.duration = duration;
    }

    /**
     * Gets the last activity.
     * @return last activity
     */
    public Date getLastActivity() {
        return lastActivity;
    }
    /**
     * Sets the last activity.
     * @param lastActivity last activity
     */
    public void setLastActivity(final Date lastActivity) {
        this.lastActivity = lastActivity;
    }
    
    @Override
    public int getResumeAttempts() {
        // TODO Auto-generated method stub
        return 0;
    }
}
