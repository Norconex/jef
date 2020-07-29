/* Copyright 2018 Norconex Inc.
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
package com.norconex.jef5.shutdown;

/**
 * Exception thrown when a problem occured while trying to shutdown
 * a running job suite.
 * @author Pascal Essiembre
 */
public class ShutdownException extends Exception {

    private static final long serialVersionUID = 1L;

    public ShutdownException(final String message) {
        super(message);
    }
    public ShutdownException(final Throwable exception) {
        super(exception);
    }
    public ShutdownException(final String message, final Throwable exception) {
        super(message, exception);
    }
}
