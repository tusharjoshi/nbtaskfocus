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

import com.nbtaskfocus.model.api.Task;
import com.nbtaskfocus.model.api.TaskList;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

/**
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public class TaskDetailRootNode extends AbstractNode implements PropertyChangeListener {
    public static final String IMAGEPATH_ICON = "com/nbtaskfocus/core/ui/resources/task_icon.png";
    public static final String IMAGEPATH_OPENEDICON = "com/nbtaskfocus/core/ui/resources/task_icon.png";
    
    public TaskDetailRootNode(BeanTreeView beanTreeView) {
        super(Children.create(new ProjectItemChildren(beanTreeView), false));
        setDisplayName(NbBundle.getMessage(TaskDetailRootNode.class, "ND_NoTaskActive"));
        
        addListeners();
    }
    
    private void addListeners() {
        TaskList taskList = TaskList.getDefault();
        taskList.addPropertyChangeListener(
                WeakListeners.propertyChange(this, taskList));        
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
        /*
         * we do not want any parent action to be shown
         * no actions are expected on this node 
         */
        return new Action[0];
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if(TaskList.EVENTNAME_ACTIVETASKCHANGE.equals(propertyName)){
            Task activeTask = TaskList.getDefault().getActiveTask();
            if( Task.NULL_TASK != activeTask ) {
                setDisplayName(NbBundle.getMessage(TaskDetailRootNode.class, "ND_ActiveTask")
                        + " " + activeTask.getTaskName());
            } else {
                setDisplayName(NbBundle.getMessage(TaskDetailRootNode.class, "ND_NoTaskActive"));
            }
            fireDisplayNameChange(null, getDisplayName());
        }
    }

    @Override
    public String getHtmlDisplayName() {
        String htmlDisplayName;
        Task activeTask = TaskList.getDefault().getActiveTask();
        if (Task.NULL_TASK != activeTask) {
            htmlDisplayName = "<b>" + getDisplayName() + "</b>";
        } else {
            htmlDisplayName = getDisplayName();
        }
        return htmlDisplayName;
    }

    public Node findNode(FileObject objectToSelect) {
        Node[] nodes = getChildren().getNodes();
        for( Node node : nodes) {
            ProjectItemNode projectItemNode = (ProjectItemNode) node;
            Node foundNode = projectItemNode.findNode(objectToSelect);
            if( null != foundNode ) {
                return foundNode;
            }
        }
        return null;
    }
    
    
}
