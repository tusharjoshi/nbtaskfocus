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
package com.nbtaskfocus.core.ui;

import com.nbtaskfocus.model.api.Task;
import com.nbtaskfocus.model.api.TaskContext;
import com.nbtaskfocus.model.api.TaskItem;
import com.nbtaskfocus.model.api.TaskList;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import javax.swing.Action;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public final class ContextManager implements PropertyChangeListener {
    
    public static final String EVENT_TREESTATECHANGED = "TreeStateChanged"; //NOI18N
    
    public static final String EVENT_ACTIVATION_START = "ActivatonStart"; //NOI18N
    public static final String EVENT_ACTIVATION_END = "ActivationEnd"; //NOI18N
    public static final String EVENT_DEACTIVATION_START = "DeactivationStart"; //NOI18N
    public static final String EVENT_DEACTIVATION_END = "DeactivationEnd"; //NOI18N
    
    private final static ContextManager DEFAULT = new ContextManager();

    public static final int CONTEXT_TREE_EXCLUDE = 0;
    public static final int CONTEXT_TREE_GRAYED = 1;
    
    private int treeState = CONTEXT_TREE_GRAYED;
    
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public int getTreeState() {
        return treeState;
    }

    public void setTreeState(int treeState) {
        int oldState = this.treeState;
        this.treeState = treeState;
        firePropertyChange(EVENT_TREESTATECHANGED, oldState, treeState);
    }
    

    private ContextManager() {
    }

    public static ContextManager getDefault() {
        return DEFAULT;
    }

    public void activateContext(TaskContext context) {
        firePropertyChange(EVENT_ACTIVATION_START, 0, 1);
        int count = context.getItemCount();
        for (int i = 0; i < count; i++) {
            activateTaskItem(context.getTaskItem(i));
        }
        firePropertyChange(EVENT_ACTIVATION_END, 0, 1);
    }

    public void activateTaskItem(TaskItem taskItem) {
        
        // do not open sticky task which was deliberately
        // closed by user
        if( taskItem.isSticky() && !taskItem.isOpened())
            return;
        
        String itemPath = taskItem.getItemPath();
        openFile(itemPath);
    }

    public void deactivateContext(TaskContext context) {
        
        firePropertyChange(EVENT_DEACTIVATION_START, 0, 1);
        int count = context.getItemCount();
        for (int i = 0; i < count; i++) {
            deactivateTaskItem(context.getTaskItem(i));
        }
        firePropertyChange(EVENT_DEACTIVATION_END, 0, 1);
    }

    public void deactivateTaskItem(TaskItem taskItem) {
        String itemPath = taskItem.getItemPath();
        closeTopComponent(itemPath);
    }

    private void openFile(final String itemPath) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    FileObject fo = FileUtil.toFileObject(new File(itemPath).getAbsoluteFile());
                    DataObject newDo = DataObject.find(fo);
                    final Node node = newDo.getNodeDelegate();
                    Action a = node.getPreferredAction();
                    if (a instanceof ContextAwareAction) {
                        a = ((ContextAwareAction) a).createContextAwareInstance(node.getLookup());
                    }
                    if (a != null) {
                        a.actionPerformed(new ActionEvent(node, ActionEvent.ACTION_PERFORMED, "")); // NOI18N 
                    }
                } catch (Exception ex) {
                    //
                }
            }
        });
    }

    private void closeTopComponent(final String filePath) {

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                Mode tcMode = WindowManager.getDefault().findMode("editor"); // NOI18N 
                TopComponent[] topComponents = tcMode.getTopComponents();
                TopComponent tcToClose = null;
                for (TopComponent tc : topComponents) {
                    Lookup lookup = tc.getLookup();
                    FileObject fileObject = getFileObject(lookup);
                    if (null != fileObject) {
                        String path = fileObject.getPath();
                        if (path.equals(filePath)) {
                            tcToClose = tc;
                            break;
                        }
                    }
                }
                if (null != tcToClose) {
                    tcToClose.close();
                }
            }
        });
    }
    
    private static FileObject getFileObject(Lookup lookup) {
        FileObject fileObject = lookup.lookup(FileObject.class);
        if( null == fileObject) {
            DataObject dataObject = lookup.lookup(DataObject.class);
            if( null != dataObject ) {
                fileObject = dataObject.getPrimaryFile();
            }
        }
        
        return fileObject;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(TaskList.EVENTNAME_ACTIVETASKCHANGE)) {
            Task oldTask = (Task) evt.getOldValue();
            if (null != oldTask && Task.NULL_TASK != oldTask) {
                deactivateContext(oldTask.getTaskContext());
            }
            Task newTask = (Task) evt.getNewValue();
            if (null != newTask && Task.NULL_TASK != newTask) {
                activateContext(newTask.getTaskContext());
            }
        }
    }
}
