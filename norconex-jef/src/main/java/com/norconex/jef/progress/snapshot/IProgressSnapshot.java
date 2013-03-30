package com.norconex.jef.progress.snapshot;

import java.util.List;

import com.norconex.jef.progress.IJobStatus;

/**
 * Immutable instance of a job suite progress at a moment in time.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 * @since 2.0
 */
public interface IProgressSnapshot extends IJobStatus {

    /**
     * Children progress if the job this progress represents has child jobs.
     * @return child progress snaphots
     */
    List<IProgressSnapshot> getChildren();
}
