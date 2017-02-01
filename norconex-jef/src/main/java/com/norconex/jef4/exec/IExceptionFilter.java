/* Copyright 2010-2017 Norconex Inc.
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
 * Responsible for filtering exceptions.  Only exceptions returning
 * <code>true</code> shall be considered in their given context.
 * @author Pascal Essiembre
 * @see RetriableExecutor
 * @deprecated Since 4.1.0, this class can be found in Norconex Commons Lang
 */
@Deprecated
public interface IExceptionFilter {

    /**
     * Filters an exception.
     * @param e the exception to filter
     * @return <code>true</code> to consider an exception, <code>false</code>
     *         to rule it out
     */
    boolean accept(Exception e);
}
