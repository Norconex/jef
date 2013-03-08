package com.norconex.jef;

/**
 * Convenient base class for implementing jobs.  Provides a default
 * implementation of getId() and getDescription() where those values are
 * passed at construction time.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 * @since 1.1
 */
public abstract class AbstractJob implements IJob {

    /** Job unique identifier. */
    private final String id;
    /** Job description. */
    private final String description;
    
    /**
     * Creates a new job.
     * @param id unique job identifier
     * @param description job description
     */
    public AbstractJob(String id, String description) {
        super();
        this.id = id;
        this.description = description;
    }

    /**
     * @see com.norconex.jef.IJob#getDescription()
     */
    public final String getDescription() {
        return description;
    }
    /**
     * @see com.norconex.jef.IJob#getId()
     */
    public final String getId() {
        return id;
    }
}
