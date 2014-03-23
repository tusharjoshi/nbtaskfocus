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

import com.nbtaskfocus.core.ui.actions.*;
import com.nbtaskfocus.model.api.Task;
import com.nbtaskfocus.model.api.TaskList;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public class TaskNode extends AbstractNode implements PropertyChangeListener {
    public static final String IMAGEPATH_ICON = "com/nbtaskfocus/core/ui/resources/task_icon.png";
    public static final String IMAGEPATH_OPENEDICON = "com/nbtaskfocus/core/ui/resources/task_icon.png";

    private final Task task;

    public TaskNode(Task task) {
        super(Children.LEAF, Lookups.singleton(task));
        setDisplayName(task.toString());
        setShortDescription(task.toString());
        this.task = task;
        
        task.addPropertyChangeListener(WeakListeners.propertyChange(this, task));
    }
    
    public Task getTask() {
        return task;
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage(IMAGEPATH_ICON);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return ImageUtilities.loadImage(IMAGEPATH_OPENEDICON);
    }

    @Override
    public Action[] getActions(boolean context) {
        List<Action> actionsList = new ArrayList<Action>();
        
        if( TaskList.getDefault().getActiveTask() != task ) {
            actionsList.add(new ActivateTaskAction(task));
        } else {
            actionsList.add(new DeactivateTaskAction(task));
        }
        
        actionsList.add(null);
        
        actionsList.add(new OpenTaskAction(task));
        actionsList.add(new ClearTaskContextAction(task));
        actionsList.add(new DeleteTaskAction(task));
        
        return actionsList.toArray(new Action[0]);
    }

    @Override
    public String getHtmlDisplayName() {
        String htmlName = null;
        if (null != task) {

            if (task.isActive()) {
                htmlName = "<b>" + task.getTaskName() + "</b>";
            } else {
                htmlName = task.getTaskName();
            }
        } else {
            htmlName = super.getHtmlDisplayName();
        }

        return htmlName;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Task.EVENTNAME_CHANGED.equals(evt.getPropertyName())) {
            this.fireDisplayNameChange(null, getDisplayName());
        } 
    }

    @Override
    public Action getPreferredAction() {
        if (task.isActive()) {
            return new DeactivateTaskAction(task);
        } else {
            return new ActivateTaskAction(task);
        }
    }
    
    
}
