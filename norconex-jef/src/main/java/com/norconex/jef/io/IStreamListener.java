package com.norconex.jef.io;

/**
 * Listener that is being notified every time a line is process from
 * a given stream.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 * @see com.norconex.jef.exec.ExecUtils
 * @see com.norconex.jef.exec.SystemCommand
 * @see StreamGobbler
 */
public interface IStreamListener {
    /**
     * Invoked when a line is streamed.
     * @param type type of line, as defined by the class using the listener
     * @param line line processed
     */
    void lineStreamed(String type, String line);
}
