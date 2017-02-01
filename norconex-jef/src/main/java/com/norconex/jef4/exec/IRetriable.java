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
 * Upon failure, code embedded in the <code>run</code> method will get
 * executed over and over again, provided that the executing class
 * supports <code>IRetriable</code>.
 * @author Pascal Essiembre
 * @see RetriableExecutor
 * @deprecated Since 4.1.0, this class can be found in Norconex Commons Lang
 */
@Deprecated
public interface IRetriable {
    /**
     * Code to be executed until successful (no exception thrown).
     * @throws RetriableException any exception
     */
    void run() throws RetriableException;
}
