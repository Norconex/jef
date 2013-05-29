package com.norconex.jef.jobs.watcher;

import java.util.ArrayList;
import java.util.Collection;

import com.norconex.commons.lang.io.IStreamListener;

public class ProcessListener implements IStreamListener {

    /** Error detection pattern. */
    private static final String DIR_PATTERN = "DIR";
    
    private final Collection<String> dirs = new ArrayList<String>();

    @Override
    public void lineStreamed(String type, String line) {
        if (line.indexOf(DIR_PATTERN) != -1) {
            dirs.add(line);
        }
    }
    
    public boolean hasDirs() {
        return !dirs.isEmpty();
    }
    public String[] getDirs() {
        return dirs.toArray(new String[] {});
    }
    
}
