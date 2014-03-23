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

import com.nbtaskfocus.core.ui.actions.OpenTaskItemAction;
import com.nbtaskfocus.core.ui.actions.RemoveTaskItemAction;
import com.nbtaskfocus.core.ui.actions.StickyTaskItemAction;
import com.nbtaskfocus.model.api.TaskItem;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public class TaskItemNode extends AbstractNode implements PropertyChangeListener {
    
    public static final String IMAGEPATH_ICON = "com/nbtaskfocus/core/ui/resources/taskitem_icon.png";
    public static final String IMAGEPATH_STICKY_ICON = "com/nbtaskfocus/core/ui/resources/taskitem_sticky_icon.png";
    
    private final TaskItem taskItem;

    public TaskItemNode(TaskItem taskItem) {
        super(Children.LEAF, createLookup(taskItem));
        this.taskItem = taskItem;
        setDisplayName(taskItem.getItemName());
        setShortDescription(taskItem.getItemPath());
        
        taskItem.addPropertyChangeListener(WeakListeners.propertyChange(this, taskItem));

    }
    
    private static Lookup createLookup(TaskItem taskItem) {
        List<Object> objects = new ArrayList<Object>();
        objects.add(taskItem);
        
        String itemPath = taskItem.getItemPath();
        File file = new File(itemPath);
        FileObject fileObject = FileUtil.toFileObject(file);
        objects.add( fileObject);
        
        DataObject dataObject = null;
        try {
            dataObject = DataObject.find(fileObject);
        } catch (DataObjectNotFoundException ex) {
            // no data object found ignore it
        }
        if( null != dataObject ) {
            objects.add(dataObject);
        }
        return Lookups.fixed(objects.toArray());
        
    }

    @Override
    public Image getIcon(int type) {
        if( taskItem.isSticky()) {            
            return ImageUtilities.loadImage(IMAGEPATH_STICKY_ICON);
        } else {
            return ImageUtilities.loadImage(IMAGEPATH_ICON);
        }
    }

    @Override
    public Image getOpenedIcon(int type) {
        if( taskItem.isSticky()) {            
            return ImageUtilities.loadImage(IMAGEPATH_STICKY_ICON);
        } else {
            return ImageUtilities.loadImage(IMAGEPATH_ICON);
        }
    }

    @Override
    public Action[] getActions(boolean context) {
        List<Action> actionList = new ArrayList<Action>();
        
        if (taskItem.isSticky()) {
            actionList.add(new StickyTaskItemAction(taskItem, false));
        } else {
            actionList.add(new StickyTaskItemAction(taskItem, true));
        }
        actionList.add(null);
        actionList.add(new OpenTaskItemAction(taskItem));
        actionList.add(new RemoveTaskItemAction(taskItem));
        
        return actionList.toArray(new Action[0]);
    }

    @Override
    public Action getPreferredAction() {
        return new OpenTaskItemAction(taskItem);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if( TaskItem.EVENT_ITEM_CHANGED.equals(evt.getPropertyName())) {
            fireIconChange();
        }
    }

    Node findNode(FileObject objectToSelect) {
        String itemPath = taskItem.getItemPath();
        if (null != itemPath
                && itemPath.equals(objectToSelect.getPath())) {
            return this;
        }
        return null;
    }
    
    
}
