package com.norconex.jef4.status;

import java.io.IOException;
import java.util.List;

import com.norconex.commons.lang.map.IMapChangeListener;
import com.norconex.commons.lang.map.MapChangeEvent;
import com.norconex.commons.lang.map.Properties;
import com.norconex.jef4.JEFException;

public class JobStatusUpdater {

    private final String suiteName;
    private final MutableJobStatus status;
    private final IJobStatusStore store;
    
    public JobStatusUpdater(
            String suiteName,
            IJobStatusStore store,
            MutableJobStatus status) {
        this.suiteName = suiteName;
        this.store = store;
        this.status = status;
        status.getProperties().addMapChangeListener(
                new IMapChangeListener<String, List<String>>() {
            @Override
            public void mapChanged(MapChangeEvent<String, List<String>> event) {
                serialize();
            }
        });
    }

    public Properties getProperties() {
        return status.getProperties();
    }
    public double getProgress() {
        return status.getProgress();
    }
    public void setProgress(double progress) {
        status.setProgress(progress);
        serialize();
    }
    public void incrementProgress(double increment) {
        status.setProgress(status.getProgress() + increment);
        serialize();
    }
    public long getDuration() {
        return status.getDuration().getDuration();
    }
    public void setNote(String note) {
        status.setNote(note);
        serialize();
    }
    
    private void serialize() {
        try {
            store.write(suiteName, status);
        } catch (IOException e) {
            throw new JEFException("Cannot persist status update for job: "
                    + status.getJobName(), e);
        }
    }
}
