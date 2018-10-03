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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.norconex.commons.lang.collection.CollectionUtil;
import com.norconex.commons.lang.event.Event;
import com.norconex.commons.lang.event.IEventListener;
import com.norconex.commons.lang.xml.IXMLConfigurable;
import com.norconex.commons.lang.xml.XML;

//TODO really have a config still??? given it contains so little, shall
// we move these settings directly on JobSuite? Else, make IXMLConfigurable?

public class JobSuiteConfig implements IXMLConfigurable {

    private Path workdir;
    private boolean backupDisabled;
    private final List<IEventListener<Event<?>>> eventListeners =
            new ArrayList<>();

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

    public List<IEventListener<Event<?>>> getEventListeners() {
        return Collections.unmodifiableList(eventListeners);
    }
    @SuppressWarnings("unchecked")
    public void setEventListeners(IEventListener<Event<?>>... eventListeners) {
        CollectionUtil.setAll(this.eventListeners, eventListeners);
    }
    @SuppressWarnings("unchecked")
    public void addEventListeners(IEventListener<Event<?>>... eventListeners) {
        this.eventListeners.addAll(Arrays.asList(eventListeners));
    }
    public void removeEventListeners() {
        this.eventListeners.clear();
    }
    @SuppressWarnings("unchecked")
    public void removeEventListeners(
            IEventListener<Event<?>>... eventListeners) {
        this.eventListeners.removeAll(Arrays.asList(eventListeners));
    }

    @Override
    public void loadFromXML(XML xml) {
        // TODO Auto-generated method stub

    }
    @Override
    public void saveToXML(XML xml) {
        // TODO Auto-generated method stub

    }
}
