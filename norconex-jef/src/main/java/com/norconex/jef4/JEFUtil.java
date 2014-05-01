package com.norconex.jef4;

import java.io.File;

public final class JEFUtil {

    public static final File FALLBACK_WORKDIR = 
            new File(System.getProperty("user.home") + "/Norconex/jef/workdir");

    private JEFUtil() {
        super();
    }
}
