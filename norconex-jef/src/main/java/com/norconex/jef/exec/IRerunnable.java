package com.norconex.jef.exec;

/**
 * Upon failure, code embedded in the <code>run</code> method will get
 * executed over and over again, provided that the executing class
 * supports <code>IRerunnable</code>.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 * @see Rerunner
 */
public interface IRerunnable {
    /**
     * Code to be executed until successful (no exception thrown).
     * @throws Exception any exception
     */
    void run() throws Exception;
}
