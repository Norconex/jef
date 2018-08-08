/* Copyright 2010-2018 Norconex Inc.
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
package com.norconex.jef5.suite;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.norconex.commons.lang.collection.CollectionUtil;
import com.norconex.commons.lang.config.IXMLConfigurable;
import com.norconex.jef5.event.IJefEventListener;

//TODO really have a config still??? given it contains so little, shall
// we move these settings directly on JobSuite? Else, make IXMLConfigurable?

public class JobSuiteConfig implements IXMLConfigurable {

    private Path workdir;
    private boolean backupDisabled;
    private final List<IJefEventListener> eventListeners = new ArrayList<>();

    public JobSuiteConfig() {
        super();
    }

    public Path getWorkdir() {
        return workdir;
    }
    public void setWorkdir(Path workdir) {
        this.workdir = workdir;
    }

    public boolean isBackupDisabled() {
        return backupDisabled;
    }
    public void setBackupDisabled(boolean backupDisabled) {
        this.backupDisabled = backupDisabled;
    }

    public List<IJefEventListener> getEventListeners() {
        return eventListeners;
    }
    public void setEventListeners(IJefEventListener... eventListeners) {
        CollectionUtil.setAll(this.eventListeners, eventListeners);
    }
    public void addEventListeners(IJefEventListener... eventListeners) {
        this.eventListeners.addAll(Arrays.asList(eventListeners));
    }
    public void removeEventListeners() {
        this.eventListeners.clear();
    }
    public void removeEventListeners(IJefEventListener... eventListeners) {
        this.eventListeners.removeAll(Arrays.asList(eventListeners));
    }

    @Override
    public void loadFromXML(Reader in) throws IOException {
        // TODO Auto-generated method stub

    }
    @Override
    public void saveToXML(Writer out) throws IOException {
        // TODO Auto-generated method stub

    }
}
