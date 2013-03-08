package com.norconex.jef.io;

/**
 * Filters lines of text read from an InputStream decorated with 
 * {@link FilteredInputStream}.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 * @since 1.1
 */
public interface IInputStreamFilter {

    /**
     * Whether a line is "accepted" or not.  An accepted line is being
     * returned when read.
     * @param line line being read
     * @return <code>true</code> if line is accepted
     */
    boolean accept(String line);
}
