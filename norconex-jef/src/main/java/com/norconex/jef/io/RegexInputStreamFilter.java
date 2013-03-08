package com.norconex.jef.io;

import java.util.regex.Pattern;

/**
 * Filters lines of text read from an InputStream decorated with 
 * {@link FilteredInputStream}, based on a given regular expression.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 * @since 1.1
 */
public class RegexInputStreamFilter implements IInputStreamFilter {

    private final Pattern pattern;
    
    private RegexInputStreamFilter(String regex) {
        super();
        this.pattern = Pattern.compile(regex);
    }

    private RegexInputStreamFilter(Pattern pattern) {
        super();
        this.pattern = pattern;
    }

    /**
     * @see com.norconex.jef.io.IInputStreamFilter#accept(java.lang.String)
     */
    public boolean accept(String string) {
        return pattern.matcher(string).matches();
    }

}
