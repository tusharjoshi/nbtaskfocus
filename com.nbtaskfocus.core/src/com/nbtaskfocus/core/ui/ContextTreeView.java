/*
 * The MIT License
 *
 * Copyright 2011 Tushar Joshi <tusharvjoshi@gmail.com>.
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

import com.nbtaskfocus.core.ui.nodes.ContextRootNode;
import com.nbtaskfocus.model.api.TaskList;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * 
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public class ContextTreeView extends CustomTreeView
        implements PropertyChangeListener {

    public static final String PROJECTTABLOGICAL_TC = "projectTabLogical_tc";
    public static final String PROJECTTAB_TC = "projectTab_tc";
    private transient volatile ContextRootNode filterNode;
    
    private transient volatile List<String[]> exPaths = null;
    private final String type;
    
    public static final RequestProcessor RP = new RequestProcessor(ContextTreeView.class);
    private RequestProcessor.Task expansionTask;

    private static final int EXPANSION_DELAY = 0;

    /**
     * Needs a component which provides ExplorerManager to set the
     * root context of that manager.
     * 
     * @param expManagerProvider component manager to set the root node.  This
     * node will be set many times when the change in task context is 
     * detected.
     * @param type ContextTreeView.PROJECTTABLOGICAL_TC for Project Context or 
     * ContextTreeView.PROJECTTAB_TC for Files Context
     */
    public ContextTreeView(ExplorerManager.Provider expManagerProvider, String type) {
        super(expManagerProvider);
        this.type = type;

        setRootContext();
        setRootVisible(false);
        
        expansionTask = createExpansionTask();
    }

    public final void setRootContext() {
        TopComponent tc = WindowManager.getDefault().findTopComponent(this.type);
        ExplorerManager.Provider provider = (ExplorerManager.Provider) tc;
        Node rootContext = provider.getExplorerManager().getRootContext();

        filterNode = new ContextRootNode(rootContext);
        getExplorerManagerProvider().getExplorerManager().setRootContext(filterNode);

    }
    
    private RequestProcessor.Task createExpansionTask() {
        return RP.create(new Runnable() {

            @Override
            public void run() {
                EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        if (null != exPaths) {
                            expandNodes(exPaths);
                        }
                    }
                });
            }
        });
    };
    
    private Runnable treeExpander = new Runnable() {

        @Override
        public void run() {
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    if (null != exPaths) {
                        expandNodes(exPaths);
                    }
                }
            });
        }
    };
    private volatile boolean bulkEventStarted = false;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (TaskList.EVENTNAME_ITEMADDED.equals(propertyName)
                || TaskList.EVENTNAME_ITEMREMOVED.equals(propertyName)) {
            setRootContext();
            if (!bulkEventStarted) {
                //WindowManager.getDefault().invokeWhenUIReady(treeExpander);
                expansionTask.schedule(EXPANSION_DELAY);
            }
        } else if (TaskList.EVENTNAME_ACTIVETASKCHANGE.equals(propertyName)) {
            if (null != filterNode) {
                exPaths = getExpandedPaths();
            }
            setRootContext();
            //WindowManager.getDefault().invokeWhenUIReady(treeExpander);
            expansionTask.schedule(EXPANSION_DELAY);
        } else if (ContextManager.EVENT_TREESTATECHANGED.equals(propertyName)) {
            if (null != filterNode) {
                exPaths = getExpandedPaths();
            }
            setRootContext();
            //WindowManager.getDefault().invokeWhenUIReady(treeExpander);
            expansionTask.schedule(EXPANSION_DELAY);
        } else if (ContextManager.EVENT_ACTIVATION_START.equals(propertyName)
                || ContextManager.EVENT_DEACTIVATION_START.equals(propertyName)) {
            bulkEventStarted = true;
            if (null != filterNode) {
                exPaths = getExpandedPaths();
            }
        } else if (ContextManager.EVENT_ACTIVATION_END.equals(propertyName)
                || ContextManager.EVENT_ACTIVATION_END.equals(propertyName)) {
            //WindowManager.getDefault().invokeWhenUIReady(treeExpander);
            expansionTask.schedule(EXPANSION_DELAY);
            bulkEventStarted = false;
        }
    }
}
