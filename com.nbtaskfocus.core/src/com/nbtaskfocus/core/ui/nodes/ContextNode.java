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
package com.nbtaskfocus.core.ui.nodes;

import com.nbtaskfocus.core.ui.ContextManager;
import com.nbtaskfocus.model.api.Task;
import com.nbtaskfocus.model.api.TaskContext;
import com.nbtaskfocus.model.api.TaskList;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public class ContextNode extends FilterNode {
    
    public boolean activeState = false;

    public boolean isActiveState() {
        return activeState;
    }
    
    public ContextNode(Node original) {
        //super(original, new ContextChildren(original));
        super(original, createChildren(original));
        setStatus();
    }
    
    private static org.openide.nodes.Children createChildren(Node original) {
        if( original.isLeaf()) {
            return Children.LEAF;
        } else {
            return new ContextChildren(original);
        }
    }
    
    public final void setStatus() {
        
        activeState = false;
        
        Task activeTask = TaskList.getDefault().getActiveTask();
        if (Task.NULL_TASK != activeTask) {
            TaskContext taskContext = activeTask.getTaskContext();

            final Node original = getOriginal();
            FileObject fileObject = getFileObject(original.getLookup());

            if (null != fileObject) {
                String path = fileObject.getPath();
                if (null != path && taskContext.isItemActive(path)) {
                    activeState = true;
                }
            }
            Project project = original.getLookup().lookup(Project.class);
            if (null != project) {
                activeState = true;
            }
        } else {
            activeState = true;
        }
        
        fireDisplayNameChange(null, getDisplayName());
        fireIconChange();
        fireOpenedIconChange();
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
    
    public String getFilePath() {
        String filePath = null;
        final Node original = getOriginal();
        FileObject fileObject = getFileObject(original.getLookup());
        if (null != fileObject) {
            filePath = fileObject.getPath();
        }
        
        return filePath;
    }

    @Override
    public String getHtmlDisplayName() {
        String label;
        if( !activeState ) {
            label = "<font color='#c2c2c2'>" + escapeChars(getDisplayName()) + "</font>";
        } else {
            label = escapeChars(getDisplayName());
        }
        
        return label;
    }
    
    private String escapeChars(String source) {
        String target = source;
        target = target.replaceAll("<", "&lt;");
        target = target.replaceAll(">", "&gt;");
        return target;
    }

    @Override
    public Image getIcon(int type) {
        Image icon;
        if( !activeState ) {
            icon = ImageUtilities.createDisabledImage(super.getIcon(type));   
        } else {
            icon = super.getIcon(type);   
        }
        return icon;
    }

    @Override
    public Image getOpenedIcon(int type) {
        Image icon;
        if( !activeState ) {
            icon = ImageUtilities.createDisabledImage(super.getOpenedIcon(type));   
        } else {
            icon = super.getOpenedIcon(type);   
        }
        return icon;
    }

    ContextNode findNode(FileObject objectToSelect) {
        String filePath = getFilePath();
        if( null != filePath && filePath.equals(objectToSelect.getPath())) {
            return this;
        } else {
            Node[] nodes = getChildren().getNodes();
            for( Node node : nodes) {
                ContextNode contextNode = ((ContextNode)node).findNode(objectToSelect);
                if( null != contextNode) {
                    return contextNode;
                }
            }
        }
        
        return null;
    }
    
    public static class ContextChildren extends FilterNode.Children {
        
        public ContextChildren(Node original) {
            super(original);
        }

        @Override
        protected Node copyNode(Node node) {
            return new ContextNode(node);
        }

        @Override
        protected Node[] createNodes(Node key) {
            List<Node> result = new ArrayList<Node>();
            Node[] nodeList = super.createNodes(key);

            for (Node node : nodeList) {
                if( accept(node)) {
                    result.add(node);
                }

            }

            return result.toArray(new Node[0]);
        }
        
        private boolean accept(Node node) {
            ContextNode contextNode = (ContextNode) node;
            String filePath = contextNode.getFilePath();
            
            if( null != filePath && 
                    filePath.indexOf(".jar") > -1) {
                
                return false;                
            }
            
            if( ContextManager.CONTEXT_TREE_GRAYED 
                    == ContextManager.getDefault().getTreeState()) {
                contextNode.setStatus();
                return true;
            } else {                
                contextNode.setStatus();

                return contextNode.isActiveState();
            }
        }
        
    }
}
