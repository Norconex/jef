package com.norconex.jef.progress.snapshot;

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;

/**
 * Interface meant to favour integration with other systems, such as monitoring
 * systems.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 * @since 2.0
 */
public interface IProgressSnapshotSerializer extends Serializable {

    /**
     * Writes a job progress snapshot.
     * @param out where to write the progress
     * @param suite the job progress snapshopt to write
     */
    void writeProgressSnapshot(Writer out, IProgressSnapshot snapshot);
    /**
     * Reads a job progress snapshot.
     * @param out where to write the progress
     * @param suite the job progress snapshot to write
     */
    IProgressSnapshot readProgressSnapshot(Reader in);
}
