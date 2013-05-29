package com.norconex.jef.jobs.watcher;

import com.norconex.commons.lang.io.IStreamListener;

public class LoopProcessKiller implements IStreamListener {

    private int loopCount;

    private final Process process;
    
    private boolean killed;
    
    public LoopProcessKiller(final Process process) {
        super();
        this.process = process;
    }

    @Override
    public void lineStreamed(String type, String line) {
        if (line.indexOf("Help!!") != -1) {
            loopCount++;
        }
        if (loopCount > 10) {
            process.destroy();
            killed = true;
        }
    }

    /**
     * Gets the .
     * @return 
     */
    public boolean isKilled() {
        return killed;
    }
}
