package com.norconex.jef.jobs;

import com.norconex.jef.JobException;
import com.norconex.jef.progress.IJobStatus;
import com.norconex.jef.progress.JobProgressAdapter;

public class PrintProgressListener extends JobProgressAdapter {

    @Override
    public void jobProgressed(IJobStatus status) throws JobException {
        System.out.println(status.getJobId() + ": "
                + ((long)(status.getCompletionRatio() * 10000)) / 100
                + "%");
    }
}
