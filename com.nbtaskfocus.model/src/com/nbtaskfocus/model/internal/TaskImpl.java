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

import com.nbtaskfocus.model.api.Task;
import com.nbtaskfocus.model.api.TaskContext;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

/**
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public class TaskImpl implements Task, Serializable {

    private String taskId;
    private String taskName;
    private String taskDescription;
    private TaskContext taskContext;
    private PropertyChangeSupport propertySupport =
            new PropertyChangeSupport(this);

    public TaskImpl() {
        taskContext = new TaskContextImpl();
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
    private boolean active;

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        boolean oldValue = this.active;
        this.active = active;
        firePropertyChange(EVENTNAME_CHANGED, oldValue, active);
    }

    @Override
    public TaskContext getTaskContext() {
        return taskContext;
    }

    @Override
    public String getTaskDescription() {
        return taskDescription;
    }

    @Override
    public void setTaskDescription(String taskDescription) {
        String oldValue = this.taskDescription;
        this.taskDescription = taskDescription;
        firePropertyChange(EVENTNAME_CHANGED, oldValue, taskDescription);
    }

    @Override
    public String getTaskId() {
        return taskId;
    }

    @Override
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public String getTaskName() {
        return taskName;
    }

    @Override
    public void setTaskName(String taskName) {
        String oldValue = this.taskName;
        this.taskName = taskName;
        firePropertyChange(EVENTNAME_CHANGED, oldValue, taskName);

    }

    @Override
    public String toString() {
        return taskName;
    }
}
