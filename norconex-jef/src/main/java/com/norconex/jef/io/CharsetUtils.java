package com.norconex.jef.io;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Character set utilities.
 * @author Pascal Essiembre
 * @since 2.0
 */
@SuppressWarnings("nls")
public final class CharsetUtils {

    private static final Logger LOG = LogManager.getLogger(CharsetUtils.class);

    public static final String UTF_8 = "UTF-8";
    public static final String ISO_8859_1 = "ISO-8859-1";

    private static final Map<Character, Character> CP1252_UTF8 =
            new HashMap<Character, Character>();
    private static final Set<Character> CP1252_CHARS;
    static {
        CP1252_UTF8.put('\u0080', '\u20AC'); // Euro Sign
        CP1252_UTF8.put('\u0081', '\u0020'); // Unknown
        CP1252_UTF8.put('\u0082', '\u201A'); // SINGLE LOW-9 QUOTATION MARK
        CP1252_UTF8.put('\u0083', '\u0192'); // Latin-F
        CP1252_UTF8.put('\u0084', '\u201E'); // DOUBLE LOW-9 QUOTATION MARK
        CP1252_UTF8.put('\u0085', '\u2026'); // HORIZONTAL ELLIPSIS
        CP1252_UTF8.put('\u0086', '\u2020'); // DAGGER
        CP1252_UTF8.put('\u0087', '\u2021'); // DOUBLE DAGGER
        CP1252_UTF8.put('\u0088', '\u02C6'); // MODIFIER LETTER CIRCUMFLEX ACCENT
        CP1252_UTF8.put('\u0089', '\u2030'); // PER MILLE SIGN
        CP1252_UTF8.put('\u008A', '\u0160'); // LATIN CAPITAL LETTER S WITH CARON
        CP1252_UTF8.put('\u008B', '\u2039'); // SINGLE LEFT-POINTING ANGLE QUOTATION MARK
        CP1252_UTF8.put('\u008C', '\u0152'); // HEAVY CONCAVE-POINTED BLACK RIGHTWARDS ARROW
        CP1252_UTF8.put('\u008D', '\u0020'); // Unknown
        CP1252_UTF8.put('\u008E', '\u017D'); // LATIN CAPITAL LETTER Z WITH CARON
        CP1252_UTF8.put('\u008F', '\u0020'); // Unknown
        CP1252_UTF8.put('\u0090', '\u0020'); // Unknown
        CP1252_UTF8.put('\u0091', '\u2018'); // LEFT SINGLE QUOTATION MARK
        CP1252_UTF8.put('\u0092', '\u2019'); // RIGHT SINGLE QUOTATION MARK
        CP1252_UTF8.put('\u0093', '\u201C'); // LEFT DOUBLE QUOTATION MARK
        CP1252_UTF8.put('\u0094', '\u201D'); // RIGHT DOUBLE QUOTATION MARK
        CP1252_UTF8.put('\u0095', '\u2022'); // BULLET
        CP1252_UTF8.put('\u0096', '\u2013'); // EN DASH
        CP1252_UTF8.put('\u0097', '\u2014'); // EM DASH
        CP1252_UTF8.put('\u0098', '\u02DC'); // SMALL TILDE
        CP1252_UTF8.put('\u0099', '\u2122'); // TRADE MARK SIGN
        CP1252_UTF8.put('\u009A', '\u0161'); // MINUS SIGN
        CP1252_UTF8.put('\u009B', '\u203A'); // SINGLE RIGHT-POINTING ANGLE QUOTATION MARK
        CP1252_UTF8.put('\u009C', '\u0153'); // LATIN SMALL LIGATURE OE
        CP1252_UTF8.put('\u009D', '\u00A0'); // No-break space
        CP1252_UTF8.put('\u009E', '\u017E'); // LATIN SMALL LETTER Z WITH CARON
        CP1252_UTF8.put('\u009F', '\u0178'); // LATIN CAPITAL LETTER Y WITH DIAERESIS
        CP1252_CHARS = CP1252_UTF8.keySet();
    }

    private CharsetUtils() {
        super();
    }
    
    /**
     * When dealing with sources of varying content, there are often character 
     * encoding issues. You may deal with what appears to be latin-1 documents,
     * but there are weird characters when looking at the document content.
     * Usual conversion to UTF-8 might do, but some bad characters may persist.
     * They may very well be Windows characters (using the charset Cp1252). 
     * These use a range of character encoding values that none of 
     * latin-1 or UTF-8 are using (they may appear as control-character to 
     * these two). This annoying encoding is too frequently found on documents 
     * published via Microsoft Office products. You can use the utility method
     * to convert them to their UTF-8 equivalent. You risk little since to 
     * start with, they usually not legitimate characters in UTF-8 
     * (so you do not risk to replace anything valuable). 
     * @param latin the ISO_8859_1 encoded string
     * @return clean UTF-8 string
     * @throws UnsupportedEncodingException
     */
    public static String latinToUTF8(String latin)
            throws UnsupportedEncodingException {
        Charset charset = Charset.forName(UTF_8);
        CharsetDecoder decoder = charset.newDecoder();
        CharsetEncoder encoder = charset.newEncoder();
        String utf8 = null;
        try {
            ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(latin));
            CharBuffer cbuf = decoder.decode(bbuf);
            utf8 = cbuf.toString();
        } catch (CharacterCodingException e) {
        }
        if (containsCP1252Chars(utf8)) {
            LOG.warn("Windows characters found (Latin-1 Supplement).");
            for (char ch : CP1252_UTF8.keySet()) {
                char newChar = CP1252_UTF8.get(ch);
                utf8 = utf8.replace(ch, newChar);
            }
        }
        return utf8;
    }

    private static boolean containsCP1252Chars(String text) {
        for (Character c : CP1252_CHARS) {
            if (text.indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }

    
}
