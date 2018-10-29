/*
 * Copyright (C) 2016 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fraunhofer.iosb.ilt.sta.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jab
 */
public class StringHelper {

    /**
     * The logger for this class.
     */
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(StringHelper.class);
    private static final String UTF8_NOT_SUPPORTED = "UTF-8 not supported?";

    public static final Charset ENCODING = StandardCharsets.UTF_8;

    private StringHelper() {

    }

    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, ENCODING.name());
        } catch (UnsupportedEncodingException exc) {
            // Should never happen, UTF-8 is build in.
            LOGGER.error(UTF8_NOT_SUPPORTED, exc);
            throw new IllegalStateException(UTF8_NOT_SUPPORTED, exc);
        }
    }

    /**
     * Decode the given input using UTF-8 as character set.
     *
     * @param input The input to urlDecode.
     * @return The decoded input.
     */
    public static String urlDecode(String input) {
        try {
            return URLDecoder.decode(input, ENCODING.name());
        } catch (UnsupportedEncodingException exc) {
            // Should never happen, UTF-8 is build in.
            LOGGER.error(UTF8_NOT_SUPPORTED, exc);
            throw new IllegalStateException(UTF8_NOT_SUPPORTED, exc);
        }
    }
}
