package com.norconex.jef;

import java.io.Serializable;

/**
 * Holds contextual information about a job.  Some attributes are descriptive 
 * in nature while others defines boundaries to be used by the JEF framework.
 * Concrete implementations should be considered immutable once instantiated.  
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 * @since 2.0
 */
public interface IJobContext extends Serializable {

    /**
     * Gets the job description.
     * @return job description
     */
    String getDescription();

    /**
     * Gets the minimum execution progress value.
     * @return minimum progress value
     */
    long getProgressMinimum();
    /**
     * Gets the maximum execution progress value.
     * @return maximum progress value
     */
    long getProgressMaximum();
    
}
