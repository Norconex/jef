package com.norconex.jef;

/**
 * Represents a job-related exception.  Implementors are invited to
 * wrap exceptions they want explicitly handled by the framework in instances
 * of <code>JobException</code>.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 */
public class JobException extends RuntimeException {

    /** For serialisation. */
    private static final long serialVersionUID = 5236102272021889018L;

    /**
     * @see Exception#Exception(java.lang.String)
     */
    public JobException(final String arg0) {
        super(arg0);
    }
    /**
     * @see Exception#Exception(java.lang.Throwable)
     */
    public JobException(final Throwable arg0) {
        super(arg0);
    }
    /**
     * @see Exception#Exception(java.lang.String, java.lang.Throwable)
     */
    public JobException(final String arg0, final Throwable arg1) {
        super(arg0, arg1);
    }
}
