package com.norconex.jef.exec;

/**
 * Responsible for filtering exceptions.  Only exceptions returning
 * <code>true</code> shall be considered in their given context.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 * @see Rerunner
 */
public interface IExceptionFilter {

    /**
     * Filters an exception.
     * @param e the exception to filter
     * @return <code>true</code> to consider an exception, <code>false</code>
     *         to rule it out
     */
    boolean accept(Exception e);
}
