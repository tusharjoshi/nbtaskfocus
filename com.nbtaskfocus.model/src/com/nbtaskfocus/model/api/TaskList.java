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
package com.nbtaskfocus.model.api;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public final class TaskList {
    public static final String EVENTNAME_ACTIVETASKCHANGE = "activeTaskChange"; //NOI18N
    public static final String EVENTNAME_ITEMADDED = "itemAdded"; //NOI18N
    public static final String EVENTNAME_ITEMREMOVED = "itemRemoved"; //NOI18N
    public static final String EVENTNAME_TASKADDED = "taskAdded"; //NOI18N
    public static final String EVENTNAME_TASKDELETED = "taskDeleted"; //NOI18N
    
    private PropertyChangeSupport propertySupport;
    private Task activeTask = Task.NULL_TASK;

    public synchronized void removePropertyChangeListener(
            PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    public void firePropertyChange(String propertyName, 
            Object oldValue, Object newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    public synchronized void addPropertyChangeListener(
            PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    private static final TaskList DEFAULT_TASKLIST = new TaskList();
    
    private final List<Task> TASK_LIST = new ArrayList<Task>();
    
    private TaskList() {
        propertySupport = new PropertyChangeSupport(this);
    }
    
    public static TaskList getDefault() {
        return DEFAULT_TASKLIST;
    }
    
    public int getCount() {
        return TASK_LIST.size();
    }
    
    public Task getTask(int index) {
        return TASK_LIST.get(index);
    }
    
    public void addTask(Task task) {
        TASK_LIST.add(task);
        firePropertyChange(EVENTNAME_TASKADDED, null, task);
    }
    
    public void removeTask(Task task) {
        TASK_LIST.remove(task);
        firePropertyChange(EVENTNAME_TASKDELETED, null, task);
        
    }
    
    public void activateTask(Task task) {
        Task oldTask = activeTask;
        if (Task.NULL_TASK != activeTask) {
            activeTask.setActive(false);
        }
        if (null != task && Task.NULL_TASK != task) {
            task.setActive(true);
            activeTask = task;
        } else {
            activeTask = Task.NULL_TASK;
        }

        firePropertyChange(EVENTNAME_ACTIVETASKCHANGE, oldTask, activeTask);
        
    }
    
    public void deactivateTask(Task task) {
        Task oldTask = activeTask;
        if (task == activeTask && Task.NULL_TASK != activeTask) {
            if (Task.NULL_TASK != activeTask) {
                activeTask.setActive(false);
            }
            activeTask = Task.NULL_TASK;

            firePropertyChange(EVENTNAME_ACTIVETASKCHANGE, oldTask, activeTask);
        }
    }
    
    public Task getActiveTask() {
        return activeTask;
    }

    public void addContextItem(String itemPath, String projectPath) { 
        
        if( Task.NULL_TASK != activeTask ) {
            activeTask.getTaskContext().addTaskItem(itemPath, projectPath);
            firePropertyChange(EVENTNAME_ITEMADDED, null, itemPath);
        }
        
    }

    public void removeContextItem(String itemPath, String projectPath) {
        
        if( Task.NULL_TASK != activeTask && 
                activeTask.getTaskContext().removeTaskItem(itemPath)) {
            
                firePropertyChange(EVENTNAME_ITEMREMOVED, null, itemPath);
            
        }
    }
    
}
