/* Copyright 2010-2014 Norconex Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.norconex.jef4.exec;

/**
 * An exception throws by a executing {@link RetriableExecutor} instance when the 
 * code executed itself threw an exception, or maximum retry attempts has
 * been reached.  In cases when the code being rerun threw one or more
 * exception, this exception's cause will hold the last exception thrown
 * by that code.
 * @author Pascal Essiembre
 * @see RetriableExecutor
 */
public class RetriableException extends Exception {

    /** For serialisation. */
    private static final long serialVersionUID = 5236102272021889018L;

    /**
     * @see Exception#Exception(java.lang.String)
     */
    public RetriableException(final String message) {
        super(message);
    }
    /**
     * @see Exception#Exception(java.lang.Throwable)
     */
    public RetriableException(final Throwable cause) {
        super(cause);
    }
    /**
     * @see Exception#Exception(java.lang.String, java.lang.Throwable)
     */
    public RetriableException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
