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
package com.nbtaskfocus.core.ui.nodes;

import com.nbtaskfocus.model.api.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.WeakListeners;

/**
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public class TaskItemChildren extends ChildFactory<TaskItem> implements PropertyChangeListener {
    
    private TaskList taskList = null;
    private ProjectItem projectItem;
    
    private final BeanTreeView beanTreeView;
    
    public TaskItemChildren(ProjectItem projectItem, BeanTreeView beanTreeView) {
        taskList = TaskList.getDefault();
        this.beanTreeView = beanTreeView;
        
        this.projectItem = projectItem;
        taskList.addPropertyChangeListener(
                WeakListeners.propertyChange(this, taskList));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if( TaskList.EVENTNAME_ACTIVETASKCHANGE.equals(propertyName) ||
            TaskList.EVENTNAME_ITEMADDED.equals(propertyName) ||
            TaskList.EVENTNAME_ITEMREMOVED.equals(propertyName)) {
            refresh();
        }
    }

    @Override
    protected boolean createKeys(List<TaskItem> list) {
        Task activeTask = taskList.getActiveTask();
        if( Task.NULL_TASK != activeTask) {
            TaskContext taskContext = activeTask.getTaskContext();
            List<TaskItem> taskItemList = taskContext.getTaskItems(projectItem);
            if (null != taskItemList) {
                for (TaskItem item : taskItemList) {
                    list.add(item);
                }
            }
        }
        
        return true;
    }

    @Override
    protected Node createNodeForKey(TaskItem key) {
        Node node = new TaskItemNode(key);
        return node;
    }
    
    public void refresh() {
        this.refresh(true);
        this.beanTreeView.expandAll();
    }
    
}
