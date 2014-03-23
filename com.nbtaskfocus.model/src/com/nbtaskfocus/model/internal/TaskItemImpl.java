/*
 * The MIT License
 *
 * Copyright 2011 Tushar Joshi, Nagpur.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.nbtaskfocus.model.internal;

import com.nbtaskfocus.model.api.TaskItem;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

/**
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public class TaskItemImpl implements TaskItem, Serializable {

    private String itemName;
    private String itemPath;
    private String projectPath;
    private boolean sticky;
    private boolean opened;
    private PropertyChangeSupport propertySupport = 
            new PropertyChangeSupport(this);

    @Override
    public String getItemName() {
        return itemName;
    }

    @Override
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Override
    public String getItemPath() {
        return itemPath;
    }

    @Override
    public void setItemPath(String itemPath) {
        this.itemPath = itemPath;
    }

    @Override
    public boolean isSticky() {
        return sticky;
    }

    @Override
    public void setSticky(boolean sticky) {
        boolean oldSticky = this.sticky;
        this.sticky = sticky;
        firePropertyChange(TaskItem.EVENT_ITEM_CHANGED, oldSticky, sticky);
    }

    @Override
    public boolean isOpened() {
        return opened;
    }

    @Override
    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    @Override
    public String getProjectPath() {
        return projectPath;
    }

    @Override
    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    @Override
    public String toString() {
        return itemPath;
    }

    @Override
    public synchronized void removePropertyChangeListener(
            PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    private void firePropertyChange(String propertyName, 
            Object oldValue, Object newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public synchronized void addPropertyChangeListener(
            PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
}
